package com.example.foodordering.viewModel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodordering.Repository.AuthRepository
import com.example.foodordering.domain.UserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    data object Idle : AuthUiState
    data object Loading : AuthUiState
    data class Authenticated(val user: UserModel) : AuthUiState
    data object Unauthenticated : AuthUiState
    data class Error(val message: String) : AuthUiState
}

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthUiState>(
        if (repository.isUserLoggedIn && repository.currentUser != null) {
            AuthUiState.Authenticated(repository.currentUser!!)
        } else {
            AuthUiState.Unauthenticated
        }
    )
    val authState: StateFlow<AuthUiState> = _authState.asStateFlow()

    val currentUser: UserModel?
        get() = repository.currentUser

    fun signIn(email: String, password: String) {
        val cleanEmail = email.trim()
        if (cleanEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            _authState.value = AuthUiState.Error("Please enter a valid email address")
            return
        }
        if (password.length < 6) {
            _authState.value = AuthUiState.Error("Password must be at least 6 characters")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthUiState.Loading
            repository.signIn(cleanEmail, password).fold(
                onSuccess = { user ->
                    _authState.value = AuthUiState.Authenticated(user)
                },
                onFailure = { error ->
                    _authState.value = AuthUiState.Error(
                        error.localizedMessage ?: "Sign in failed. Please check your credentials."
                    )
                }
            )
        }
    }

    fun signUp(email: String, password: String, confirmPassword: String) {
        val cleanEmail = email.trim()
        if (cleanEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            _authState.value = AuthUiState.Error("Please enter a valid email address")
            return
        }
        if (password.length < 6) {
            _authState.value = AuthUiState.Error("Password must be at least 6 characters")
            return
        }
        if (password != confirmPassword) {
            _authState.value = AuthUiState.Error("Passwords do not match")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthUiState.Loading
            repository.signUp(cleanEmail, password).fold(
                onSuccess = { user ->
                    _authState.value = AuthUiState.Authenticated(user)
                },
                onFailure = { error ->
                    _authState.value = AuthUiState.Error(
                        error.localizedMessage ?: "Sign up failed. Please try again."
                    )
                }
            )
        }
    }

    fun signOut() {
        repository.signOut()
        _authState.value = AuthUiState.Unauthenticated
    }

    fun clearError() {
        if (_authState.value is AuthUiState.Error) {
            _authState.value = AuthUiState.Idle
        }
    }
}
