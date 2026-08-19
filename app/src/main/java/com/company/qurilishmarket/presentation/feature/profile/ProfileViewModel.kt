package com.company.qurilishmarket.presentation.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.qurilishmarket.domain.model.AuthState
import com.company.qurilishmarket.domain.model.User
import com.company.qurilishmarket.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val authState: StateFlow<AuthState> = authRepository.authState
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AuthState.Loading)

    private val _profile = MutableStateFlow<User?>(null)
    val profile: StateFlow<User?> = _profile.asStateFlow()

    private val _isSavingName = MutableStateFlow(false)
    val isSavingName: StateFlow<Boolean> = _isSavingName.asStateFlow()

    fun loadProfile() {
        viewModelScope.launch {
            authRepository.getCurrentUserProfile().onSuccess { _profile.value = it }
        }
    }

    fun saveName(newName: String) {
        val trimmed = newName.trim()
        if (trimmed.isBlank()) return
        viewModelScope.launch {
            _isSavingName.value = true
            authRepository.updateProfileName(trimmed).onSuccess { loadProfile() }
            _isSavingName.value = false
        }
    }

    fun signOut() {
        viewModelScope.launch { authRepository.signOut() }
    }
}
