package com.company.qurilishmarket.presentation.feature.orderdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.company.qurilishmarket.domain.model.Order
import com.company.qurilishmarket.domain.repository.OrderRepository
import com.company.qurilishmarket.presentation.common.UiState
import com.company.qurilishmarket.presentation.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val orderId: String = savedStateHandle.toRoute<Screen.OrderDetail>().orderId

    private val _uiState = MutableStateFlow<UiState<Order>>(UiState.Loading)
    val uiState: StateFlow<UiState<Order>> = _uiState.asStateFlow()

    init {
        load()
        observeStatus()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            orderRepository.getOrderById(orderId)
                .onSuccess { _uiState.value = UiState.Success(it) }
                .onFailure { e -> _uiState.value = UiState.Error(e.message ?: "Noma'lum xatolik yuz berdi") }
        }
    }

    /**
     * §6: admin buyurtma statusini o'zgartirganda, bu ekran qayta so'ramasdan avtomatik
     * yangilanadi — Realtime orqali. Agar ulanish biror sababga ko'ra ishlamasa (masalan,
     * tarmoq muammosi), foydalanuvchi baribir pastga tortib (pull-to-refresh yoki qayta
     * kirish) yangilay oladi — bu shunchaki qulaylik, yagona yo'l emas.
     */
    private fun observeStatus() {
        orderRepository.observeOrderStatus(orderId)
            .onEach { newStatus ->
                val current = (_uiState.value as? UiState.Success)?.data ?: return@onEach
                _uiState.value = UiState.Success(current.copy(status = newStatus))
            }
            .launchIn(viewModelScope)
    }
}
