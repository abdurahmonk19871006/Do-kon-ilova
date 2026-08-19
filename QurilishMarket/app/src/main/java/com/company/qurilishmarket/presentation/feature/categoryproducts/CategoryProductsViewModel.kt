package com.company.qurilishmarket.presentation.feature.categoryproducts

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.company.qurilishmarket.domain.model.Product
import com.company.qurilishmarket.domain.model.ProductSort
import com.company.qurilishmarket.domain.repository.CategoryRepository
import com.company.qurilishmarket.domain.repository.ProductRepository
import com.company.qurilishmarket.presentation.common.UiState
import com.company.qurilishmarket.presentation.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryProductsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val categoryId: String = savedStateHandle.toRoute<Screen.CategoryProducts>().categoryId

    private val _categoryName = MutableStateFlow("")
    val categoryName: StateFlow<String> = _categoryName.asStateFlow()

    private val _sort = MutableStateFlow(ProductSort.NEWEST)
    val sort: StateFlow<ProductSort> = _sort.asStateFlow()

    private val _uiState = MutableStateFlow<UiState<List<Product>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Product>>> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            categoryRepository.getCategoryById(categoryId).onSuccess { _categoryName.value = it.name }
        }
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            productRepository.getProductsByCategory(categoryId, _sort.value)
                .onSuccess { _uiState.value = if (it.isEmpty()) UiState.Empty else UiState.Success(it) }
                .onFailure { e -> _uiState.value = UiState.Error(e.message ?: "Noma'lum xatolik yuz berdi") }
        }
    }

    fun setSort(newSort: ProductSort) {
        if (newSort == _sort.value) return
        _sort.value = newSort
        load()
    }
}
