package com.company.qurilishmarket.presentation.feature.productdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.company.qurilishmarket.domain.model.Product
import com.company.qurilishmarket.domain.repository.CartRepository
import com.company.qurilishmarket.domain.repository.FavoriteRepository
import com.company.qurilishmarket.domain.repository.ProductRepository
import com.company.qurilishmarket.presentation.common.UiState
import com.company.qurilishmarket.presentation.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ProductDetailEvent {
    data object AddedToCart : ProductDetailEvent
    data class FavoriteError(val message: String) : ProductDetailEvent
}

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    // Navigation Compose'ning type-safe route'idan argumentni to'g'ridan-to'g'ri o'qiydi —
    // qo'lda "productId" satrini parse qilish shart emas (§3'dagi Screen.ProductDetail)
    private val productId: String = savedStateHandle.toRoute<Screen.ProductDetail>().productId

    private val _uiState = MutableStateFlow<UiState<Product>>(UiState.Loading)
    val uiState: StateFlow<UiState<Product>> = _uiState.asStateFlow()

    private val _quantity = MutableStateFlow(1)
    val quantity: StateFlow<Int> = _quantity.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    // Bir martalik hodisa (Snackbar) uchun Channel — StateFlow'dan farqli, ekran qayta
    // chizilganda (masalan burilishda) takror ishga tushmaydi
    private val _events = Channel<ProductDetailEvent>(Channel.BUFFERED)
    val events: Flow<ProductDetailEvent> = _events.receiveAsFlow()

    init {
        load()
        checkFavoriteStatus()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            productRepository.getProductById(productId)
                .onSuccess { _uiState.value = UiState.Success(it) }
                .onFailure { e -> _uiState.value = UiState.Error(e.message ?: "Noma'lum xatolik yuz berdi") }
        }
    }

    private fun checkFavoriteStatus() {
        viewModelScope.launch {
            favoriteRepository.isFavorite(productId).onSuccess { _isFavorite.value = it }
        }
    }

    fun increaseQuantity() {
        val stock = (uiState.value as? UiState.Success)?.data?.stock ?: return
        _quantity.value = (_quantity.value + 1).coerceAtMost(stock)
    }

    fun decreaseQuantity() {
        _quantity.value = (_quantity.value - 1).coerceAtLeast(1)
    }

    fun addToCart() {
        val product = (uiState.value as? UiState.Success)?.data ?: return
        viewModelScope.launch {
            cartRepository.addToCart(product, _quantity.value)
            _events.send(ProductDetailEvent.AddedToCart)
        }
    }

    /**
     * Checkout'dagi kabi to'liq "login'ga o'tib qaytish" oqimi bu yerda ataylab yo'q —
     * sevimlilarga qo'shish xaridni yakunlash kabi muhim emas, shuning uchun shunchaki
     * xabar ko'rsatiladi (§2'dagi minimal friction tamoyili: har bir kichik action uchun
     * to'liq login oqimini majburlash ortiqcha).
     */
    fun toggleFavorite() {
        viewModelScope.launch {
            val newValue = !_isFavorite.value
            favoriteRepository.toggleFavorite(productId, newValue)
                .onSuccess { _isFavorite.value = newValue }
                .onFailure { e ->
                    _events.send(
                        ProductDetailEvent.FavoriteError(
                            e.message ?: "Sevimlilarga qo'shish uchun tizimga kiring"
                        )
                    )
                }
        }
    }
}
