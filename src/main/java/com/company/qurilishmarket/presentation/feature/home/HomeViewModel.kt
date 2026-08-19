package com.company.qurilishmarket.presentation.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.qurilishmarket.domain.usecase.GetHomeDataUseCase
import com.company.qurilishmarket.domain.usecase.HomeData
import com.company.qurilishmarket.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeDataUseCase: GetHomeDataUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<HomeData>>(UiState.Loading)
    val uiState: StateFlow<UiState<HomeData>> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            getHomeDataUseCase()
                .onSuccess { data ->
                    _uiState.value = if (data.isEmpty) UiState.Empty else UiState.Success(data)
                }
                .onFailure { e ->
                    _uiState.value = UiState.Error(e.message ?: "Noma'lum xatolik yuz berdi")
                }
        }
    }
}
