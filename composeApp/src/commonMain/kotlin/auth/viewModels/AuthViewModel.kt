package auth.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import auth.repositories.AuthRepository
import data.Status
import data.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    val authUiState: StateFlow<AuthUiState> get() = _authUiState
    private val _authUiState: MutableStateFlow<AuthUiState> = MutableStateFlow(
        AuthUiState.FetchingLoginStatus
    )

    val getUsersUiState: StateFlow<GetUsersUiState> get() = _getUsersUiState
    private val _getUsersUiState: MutableStateFlow<GetUsersUiState> = MutableStateFlow(
        GetUsersUiState.Loading
    )

    private var usersList: MutableList<User> = mutableListOf()

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
        _authUiState.value = AuthUiState.LoginInProgress
        viewModelScope.launch(Dispatchers.IO) {
            val authResponse = authRepository.login(email = email, password = password)
            when (authResponse.status) {
                Status.Loading -> {}
                Status.Success -> {
                    val isAdmin = authRepository.hasAdminAccess()
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
                val isAdmin = authRepository.hasAdminAccess()
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

    suspend fun getUsersList() {
        viewModelScope.launch(Dispatchers.IO) {
            val usersResponse = authRepository.getUsersList()
            when (usersResponse.status) {
                Status.Loading -> {
                    _getUsersUiState.value = GetUsersUiState.Loading
                }

                Status.Success -> {
                    _getUsersUiState.value =
                        GetUsersUiState.Success(usersList = usersResponse.data ?: emptyList())
                    usersList = usersResponse.data?.toMutableList() ?: mutableListOf()
                }

                Status.Error -> {
                    _getUsersUiState.value = GetUsersUiState.Error
                }
            }
        }
    }

    suspend fun addNewUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.addUser(user = user)
        }
    }

    suspend fun deleteUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            // Do not allow deletion of current logged in user and deletion of service account
            if (!authRepository.allowDeletion(userEmail = user.email)) return@launch
            authRepository.deleteUser(user = user)
            usersList.removeIf { it.id == user.id }
            _getUsersUiState.value = GetUsersUiState.Success(usersList = usersList)
        }
    }
}

sealed interface GetUsersUiState {
    data object Loading : GetUsersUiState
    data class Success(val usersList: List<User>) : GetUsersUiState
    data object Error : GetUsersUiState
}

sealed interface AuthUiState {
    data object NotLoggedIn : AuthUiState
    data object FetchingLoginStatus : AuthUiState
    data object LoginInProgress : AuthUiState
    data class LoggedIn(var isAdmin: Boolean = false) : AuthUiState
    data object Error : AuthUiState
}