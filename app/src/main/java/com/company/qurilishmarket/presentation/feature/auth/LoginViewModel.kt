package com.company.qurilishmarket.presentation.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.qurilishmarket.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface LoginStep {
    data object EnterPhone : LoginStep
    data object EnterCode : LoginStep
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _step = MutableStateFlow<LoginStep>(LoginStep.EnterPhone)
    val step: StateFlow<LoginStep> = _step.asStateFlow()

    private val _phone = MutableStateFlow("")
    val phone: StateFlow<String> = _phone.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _loginSuccess = Channel<Unit>(Channel.BUFFERED)
    val loginSuccess: Flow<Unit> = _loginSuccess.receiveAsFlow()

    fun onPhoneChange(value: String) {
        _phone.value = value
    }

    fun sendOtp() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            authRepository.sendOtp(_phone.value)
                .onSuccess { _step.value = LoginStep.EnterCode }
                .onFailure { _error.value = it.message ?: "SMS yuborishda xatolik yuz berdi" }
            _isLoading.value = false
        }
    }

    fun verifyOtp(code: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            authRepository.verifyOtp(_phone.value, code)
                .onSuccess { _loginSuccess.send(Unit) }
                .onFailure { _error.value = it.message ?: "Kod noto'g'ri yoki muddati o'tgan" }
            _isLoading.value = false
        }
    }
}
