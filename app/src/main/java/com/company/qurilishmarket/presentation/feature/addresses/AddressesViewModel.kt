package com.company.qurilishmarket.presentation.feature.addresses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.qurilishmarket.domain.model.Address
import com.company.qurilishmarket.domain.repository.AddressRepository
import com.company.qurilishmarket.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressesViewModel @Inject constructor(
    private val addressRepository: AddressRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Address>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Address>>> = _uiState.asStateFlow()

    private val _isAdding = MutableStateFlow(false)
    val isAdding: StateFlow<Boolean> = _isAdding.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            addressRepository.getMyAddresses()
                .onSuccess { _uiState.value = if (it.isEmpty()) UiState.Empty else UiState.Success(it) }
                .onFailure { e -> _uiState.value = UiState.Error(e.message ?: "Noma'lum xatolik yuz berdi") }
        }
    }

    fun addAddress(title: String, fullAddress: String) {
        if (title.isBlank() || fullAddress.isBlank()) return
        viewModelScope.launch {
            _isAdding.value = true
            addressRepository.createAddress(title.trim(), fullAddress.trim())
                .onSuccess { load() }
            _isAdding.value = false
        }
    }

    fun deleteAddress(addressId: String) {
        viewModelScope.launch {
            addressRepository.deleteAddress(addressId).onSuccess { load() }
        }
    }
}
