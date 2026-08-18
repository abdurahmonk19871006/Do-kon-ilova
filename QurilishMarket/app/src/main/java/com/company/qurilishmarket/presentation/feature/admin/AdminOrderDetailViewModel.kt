package com.company.qurilishmarket.presentation.feature.admin

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.company.qurilishmarket.domain.model.Order
import com.company.qurilishmarket.domain.model.OrderStatus
import com.company.qurilishmarket.domain.repository.AdminOrderRepository
import com.company.qurilishmarket.domain.repository.OrderRepository
import com.company.qurilishmarket.presentation.common.UiState
import com.company.qurilishmarket.presentation.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * O'qish uchun oddiy OrderRepository ishlatiladi — chunki RLS'da admin bo'lsa istalgan
 * buyurtmani ko'ra oladi (§6), alohida "admin o'qish" metodi yozishga hojat yo'q. Yozish
 * (status o'zgartirish) esa AdminOrderRepository orqali, chunki u update_order_status()
 * Postgres funksiyasini chaqiradi.
 */
@HiltViewModel
class AdminOrderDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val orderRepository: OrderRepository,
    private val adminOrderRepository: AdminOrderRepository
) : ViewModel() {

    private val orderId: String = savedStateHandle.toRoute<Screen.AdminOrderDetail>().orderId

    private val _uiState = MutableStateFlow<UiState<Order>>(UiState.Loading)
    val uiState: StateFlow<UiState<Order>> = _uiState.asStateFlow()

    private val _isUpdating = MutableStateFlow(false)
    val isUpdating: StateFlow<Boolean> = _isUpdating.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            orderRepository.getOrderById(orderId)
                .onSuccess { _uiState.value = UiState.Success(it) }
                .onFailure { e -> _uiState.value = UiState.Error(e.message ?: "Noma'lum xatolik yuz berdi") }
        }
    }

    fun setStatus(status: OrderStatus) {
        viewModelScope.launch {
            _isUpdating.value = true
            _error.value = null
            adminOrderRepository.updateOrderStatus(orderId, status)
                .onSuccess { load() } // §6: xaridorning ekrani Realtime orqali o'zi yangilanadi
                .onFailure { e -> _error.value = e.message ?: "Statusni o'zgartirishda xatolik yuz berdi" }
            _isUpdating.value = false
        }
    }
}
