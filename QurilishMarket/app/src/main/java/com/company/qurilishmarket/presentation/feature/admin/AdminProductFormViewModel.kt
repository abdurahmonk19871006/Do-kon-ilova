package com.company.qurilishmarket.presentation.feature.admin

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.company.qurilishmarket.domain.model.Category
import com.company.qurilishmarket.domain.repository.AdminProductRepository
import com.company.qurilishmarket.domain.repository.CategoryRepository
import com.company.qurilishmarket.domain.repository.ProductRepository
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

@HiltViewModel
class AdminProductFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    // O'qish uchun oddiy ProductRepository yetarli — RLS'da o'qish hammaga ochiq (§6),
    // faqat yozish uchun AdminProductRepository kerak.
    private val productRepository: ProductRepository,
    private val adminProductRepository: AdminProductRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val editingProductId: String? = savedStateHandle.toRoute<Screen.AdminProductForm>().productId
    val isEditing: Boolean get() = editingProductId != null

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _form = MutableStateFlow(ProductFormState())
    val form: StateFlow<ProductFormState> = _form.asStateFlow()

    private val _isLoading = MutableStateFlow(editingProductId != null)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _saved = Channel<Unit>(Channel.BUFFERED)
    val saved: Flow<Unit> = _saved.receiveAsFlow()

    init {
        viewModelScope.launch {
            categoryRepository.getCategories().onSuccess { _categories.value = it }
        }
        editingProductId?.let { id ->
            viewModelScope.launch {
                productRepository.getProductById(id)
                    .onSuccess { _form.value = ProductFormState.fromProduct(it) }
                    .onFailure { e -> _error.value = e.message ?: "Mahsulotni yuklashda xatolik" }
                _isLoading.value = false
            }
        }
    }

    fun updateForm(transform: (ProductFormState) -> ProductFormState) {
        _form.value = transform(_form.value)
    }

    fun save() {
        if (!_form.value.isValid) {
            _error.value = "Kod, nomi, kategoriya, narx va qoldiqni to'g'ri kiriting"
            return
        }
        viewModelScope.launch {
            _isSaving.value = true
            _error.value = null

            val product = _form.value.toProduct(id = editingProductId ?: "")
            val result = if (editingProductId != null) {
                adminProductRepository.updateProduct(product)
            } else {
                adminProductRepository.createProduct(product).map { }
            }

            result.onSuccess { _saved.send(Unit) }
                .onFailure { e -> _error.value = e.message ?: "Saqlashda xatolik yuz berdi" }
            _isSaving.value = false
        }
    }

    fun deactivate() {
        val id = editingProductId ?: return
        viewModelScope.launch {
            _isSaving.value = true
            adminProductRepository.deactivateProduct(id)
                .onSuccess { _saved.send(Unit) }
                .onFailure { e -> _error.value = e.message ?: "O'chirishda xatolik yuz berdi" }
            _isSaving.value = false
        }
    }
}
