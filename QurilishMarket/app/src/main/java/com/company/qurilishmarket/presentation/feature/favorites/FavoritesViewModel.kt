package com.company.qurilishmarket.presentation.feature.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.qurilishmarket.domain.model.AuthState
import com.company.qurilishmarket.domain.model.Product
import com.company.qurilishmarket.domain.repository.AuthRepository
import com.company.qurilishmarket.domain.repository.FavoriteRepository
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
class FavoritesViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
    authRepository: AuthRepository
) : ViewModel() {

    val authState: StateFlow<AuthState> = authRepository.authState
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AuthState.Loading)

    private val _uiState = MutableStateFlow<UiState<List<Product>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Product>>> = _uiState.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            favoriteRepository.getFavoriteProducts()
                .onSuccess { _uiState.value = if (it.isEmpty()) UiState.Empty else UiState.Success(it) }
                .onFailure { e -> _uiState.value = UiState.Error(e.message ?: "Noma'lum xatolik yuz berdi") }
        }
    }
}
