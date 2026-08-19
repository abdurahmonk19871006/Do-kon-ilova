package com.company.qurilishmarket.presentation.feature.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.qurilishmarket.domain.model.CartItem
import com.company.qurilishmarket.domain.repository.CartRepository
import com.company.qurilishmarket.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    // Room'ning Flow'i o'zgarishda avtomatik push qiladi — Home/ProductDetail'dagi kabi
    // qo'lda load()/collect kerak emas, chunki manba mahalliy va reaktiv (§1).
    val uiState: StateFlow<UiState<List<CartItem>>> = cartRepository.observeCart()
        .map { items -> if (items.isEmpty()) UiState.Empty else UiState.Success(items) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)

    fun increaseQuantity(item: CartItem) {
        viewModelScope.launch { cartRepository.updateQuantity(item.productId, item.quantity + 1) }
    }

    fun decreaseQuantity(item: CartItem) {
        // 0'ga tushsa, repository o'zi o'chirib tashlaydi (data/repository/CartRepositoryImpl.kt)
        viewModelScope.launch { cartRepository.updateQuantity(item.productId, item.quantity - 1) }
    }

    fun removeItem(productId: String) {
        viewModelScope.launch { cartRepository.removeFromCart(productId) }
    }
}
