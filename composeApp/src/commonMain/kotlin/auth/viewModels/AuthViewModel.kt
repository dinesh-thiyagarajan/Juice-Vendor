package auth.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import auth.repositories.AuthRepository
import data.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

const val COLLECTION_USERS = "Users"

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    val authUiState: StateFlow<AuthUiState> get() = _authUiState
    private val _authUiState: MutableStateFlow<AuthUiState> = MutableStateFlow(
        AuthUiState.FetchingLoginStatus
    )

    // TODO remove this from constructor and implement a better way
    init {
        viewModelScope.launch {
            isLoggedIn()
        }
    }

    suspend fun login(
        email: String,
        password: String,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val authResponse = authRepository.login(email = email, password = password)
            when (authResponse.status) {
                Status.Loading -> {}
                Status.Success -> {
                    val isAdmin = authRepository.isAdmin(COLLECTION_USERS)
                    _authUiState.value = AuthUiState.LoggedIn(isAdmin = isAdmin)
                }

                Status.Error -> {
                    _authUiState.value = AuthUiState.Error
                }
            }
        }
    }

    private fun isLoggedIn() {
        viewModelScope.launch(Dispatchers.IO) {
            val isLoggedIn = authRepository.isLoggedIn()
            if (isLoggedIn) {
                val isAdmin = authRepository.isAdmin(COLLECTION_USERS)
                _authUiState.value = AuthUiState.LoggedIn(isAdmin = isAdmin)
            } else {
                _authUiState.value = AuthUiState.NotLoggedIn
            }
        }
    }

    suspend fun updateAuthUiState(authUiState: AuthUiState) {
        viewModelScope.launch {
            _authUiState.value = authUiState
        }
    }
}

sealed interface AuthUiState {
    data object NotLoggedIn : AuthUiState
    data object FetchingLoginStatus : AuthUiState
    data object LoginInProgress : AuthUiState
    data class LoggedIn(var isAdmin: Boolean = false) : AuthUiState
    data object Error : AuthUiState
}