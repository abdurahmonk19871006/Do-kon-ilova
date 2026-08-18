package com.company.qurilishmarket.presentation.feature.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.qurilishmarket.domain.model.Order
import com.company.qurilishmarket.domain.model.OrderStatus
import com.company.qurilishmarket.domain.repository.AdminOrderRepository
import com.company.qurilishmarket.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminOrdersViewModel @Inject constructor(
    private val adminOrderRepository: AdminOrderRepository
) : ViewModel() {

    private var allOrders: List<Order> = emptyList()

    // null = "Hammasi" filtri
    private val _statusFilter = MutableStateFlow<OrderStatus?>(null)
    val statusFilter: StateFlow<OrderStatus?> = _statusFilter.asStateFlow()

    private val _uiState = MutableStateFlow<UiState<List<Order>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Order>>> = _uiState.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            adminOrderRepository.getAllOrders()
                .onSuccess { orders -> allOrders = orders; applyFilter() }
                .onFailure { e -> _uiState.value = UiState.Error(e.message ?: "Noma'lum xatolik yuz berdi") }
        }
    }

    fun setStatusFilter(status: OrderStatus?) {
        _statusFilter.value = status
        applyFilter()
    }

    private fun applyFilter() {
        val filtered = allOrders.filter { _statusFilter.value == null || it.status == _statusFilter.value }
        _uiState.value = if (filtered.isEmpty()) UiState.Empty else UiState.Success(filtered)
    }
}
