package com.company.qurilishmarket.presentation.feature.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.qurilishmarket.domain.model.Category
import com.company.qurilishmarket.domain.repository.CategoryRepository
import com.company.qurilishmarket.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Category>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Category>>> = _uiState.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            categoryRepository.getCategories()
                .onSuccess { _uiState.value = if (it.isEmpty()) UiState.Empty else UiState.Success(it) }
                .onFailure { e -> _uiState.value = UiState.Error(e.message ?: "Noma'lum xatolik yuz berdi") }
        }
    }
}
