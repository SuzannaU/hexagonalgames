package com.openclassrooms.hexagonal.games.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(private val userRepository: UserRepository) :
    ViewModel() {

    private val _authState = MutableStateFlow(AuthState())
    val authState = _authState.asStateFlow()

    fun getCurrentUser() : FirebaseUser? {
        return userRepository.getCurrentUser()
    }

    fun userIsAuthenticated() {
        _authState.update {
            _authState.value.copy(isAuthenticated = true)
        }
    }

    fun userIsNotAuthenticated() {
        _authState.update {
            _authState.value.copy(isAuthenticated = false)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            userRepository.signOut()
            _authState.update {
                _authState.value.copy(isAuthenticated = false)
            }
        }
    }
}

data class AuthState(
    val isAuthenticated: Boolean = false,
)