package com.company.qurilishmarket.presentation.feature.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.qurilishmarket.domain.model.AuthState
import com.company.qurilishmarket.domain.model.Order
import com.company.qurilishmarket.domain.repository.AuthRepository
import com.company.qurilishmarket.domain.repository.OrderRepository
import com.company.qurilishmarket.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    authRepository: AuthRepository
) : ViewModel() {

    // Login qilinmagan bo'lsa, "bo'sh" deb noto'g'ri xabar ko'rsatmaslik uchun — ekran shu
    // holatni alohida ko'rsatadi ("kirish kerak", boshqa xabar emas).
    val authState: StateFlow<AuthState> = authRepository.authState
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AuthState.Loading)

    private val _uiState = MutableStateFlow<UiState<List<Order>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Order>>> = _uiState.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            orderRepository.getMyOrders()
                .onSuccess { _uiState.value = if (it.isEmpty()) UiState.Empty else UiState.Success(it) }
                .onFailure { e -> _uiState.value = UiState.Error(e.message ?: "Noma'lum xatolik yuz berdi") }
        }
    }
}
