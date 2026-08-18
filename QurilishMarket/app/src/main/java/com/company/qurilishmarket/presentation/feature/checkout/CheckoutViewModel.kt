package com.company.qurilishmarket.presentation.feature.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.qurilishmarket.domain.model.AuthState
import com.company.qurilishmarket.domain.model.CartItem
import com.company.qurilishmarket.domain.model.DeliveryType
import com.company.qurilishmarket.domain.model.PaymentMethod
import com.company.qurilishmarket.domain.repository.AddressRepository
import com.company.qurilishmarket.domain.repository.AuthRepository
import com.company.qurilishmarket.domain.repository.CartRepository
import com.company.qurilishmarket.domain.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// Sodda flat tarif — masofaga qarab hisoblash keyingi bosqichda (§10dagi kelajak reja)
private const val FLAT_DELIVERY_FEE = 20_000L

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val addressRepository: AddressRepository,
    private val orderRepository: OrderRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val authState: StateFlow<AuthState> = authRepository.authState
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AuthState.Loading)

    val cartItems: StateFlow<List<CartItem>> = cartRepository.observeCart()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _deliveryType = MutableStateFlow(DeliveryType.YETKAZIB_BERISH)
    val deliveryType: StateFlow<DeliveryType> = _deliveryType.asStateFlow()

    private val _address = MutableStateFlow("")
    val address: StateFlow<String> = _address.asStateFlow()

    private val _paymentMethod = MutableStateFlow(PaymentMethod.NAQD)
    val paymentMethod: StateFlow<PaymentMethod> = _paymentMethod.asStateFlow()

    private val _comment = MutableStateFlow("")
    val comment: StateFlow<String> = _comment.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _orderPlaced = Channel<String>(Channel.BUFFERED)
    val orderPlaced: Flow<String> = _orderPlaced.receiveAsFlow()

    val deliveryFee: Long get() = if (_deliveryType.value == DeliveryType.OLIB_KETISH) 0L else FLAT_DELIVERY_FEE

    fun setDeliveryType(type: DeliveryType) { _deliveryType.value = type }
    fun setAddress(value: String) { _address.value = value }
    fun setPaymentMethod(method: PaymentMethod) { _paymentMethod.value = method }
    fun setComment(value: String) { _comment.value = value }

    fun submitOrder() {
        if (_deliveryType.value == DeliveryType.YETKAZIB_BERISH && _address.value.isBlank()) {
            _error.value = "Manzilni kiriting"
            return
        }

        viewModelScope.launch {
            _isSubmitting.value = true
            _error.value = null

            var addressId: String? = null
            if (_deliveryType.value == DeliveryType.YETKAZIB_BERISH) {
                val addressResult = addressRepository.createAddress("Yetkazib berish manzili", _address.value)
                if (addressResult.isFailure) {
                    _error.value = addressResult.exceptionOrNull()?.message ?: "Manzilni saqlashda xatolik"
                    _isSubmitting.value = false
                    return@launch
                }
                addressId = addressResult.getOrNull()
            }

            orderRepository.createOrder(
                items = cartItems.value,
                deliveryType = _deliveryType.value,
                addressId = addressId,
                paymentMethod = _paymentMethod.value,
                comment = _comment.value.ifBlank { null },
                deliveryFee = deliveryFee
            ).onSuccess { orderId ->
                cartRepository.clearCart()
                _orderPlaced.send(orderId)
            }.onFailure {
                _error.value = it.message ?: "Buyurtma berishda xatolik yuz berdi"
            }

            _isSubmitting.value = false
        }
    }
}
