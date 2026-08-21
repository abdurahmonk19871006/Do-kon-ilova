package com.company.qurilishmarket.presentation.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.qurilishmarket.domain.model.Product
import com.company.qurilishmarket.domain.repository.ProductRepository
import com.company.qurilishmarket.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    // debounce — har bosilgan harfda emas, yozish to'xtagach 400ms so'ng qidiradi.
    // flatMapLatest — foydalanuvchi yana yozsa, oldingi (hali tugallanmagan) so'rovni
    // avtomatik bekor qiladi, shunda sekin javob tezroq so'rovni "bosib" yubormaydi.
    val uiState: StateFlow<UiState<List<Product>>> = _query
        .debounce(400)
        .distinctUntilChanged()
        .flatMapLatest { q ->
            if (q.isBlank()) {
                flowOf<UiState<List<Product>>>(UiState.Empty)
            } else {
                flow<UiState<List<Product>>> {
                    emit(UiState.Loading)
                    val result = productRepository.searchProducts(q)
                    emit(
                        result.fold(
                            onSuccess = { list -> if (list.isEmpty()) UiState.Empty else UiState.Success(list) },
                            onFailure = { e -> UiState.Error(e.message ?: "Qidirishda xatolik yuz berdi") }
                        )
                    )
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Empty)

    fun onQueryChange(value: String) {
        _query.value = value
    }
}
