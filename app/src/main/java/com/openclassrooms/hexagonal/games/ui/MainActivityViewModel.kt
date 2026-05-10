package com.openclassrooms.hexagonal.games.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val userRepository: UserRepository
) :
    ViewModel() {

    private val _authState = MutableStateFlow(AuthState())
    val authState = _authState.asStateFlow()

    fun getCurrentUser(): FirebaseUser? {
        return userRepository.getCurrentUser()
    }

    fun userIsAuthenticated() {
        Log.i("TAG", "user is authenticated")
        _authState.update {
            _authState.value.copy(isAuthenticated = true)
        }
    }

    fun userIsNotAuthenticated() {
        Log.i("TAG", "user is NOT authenticated")
        _authState.update {
            _authState.value.copy(isAuthenticated = false)
        }
    }

    fun createUser() {
        userRepository.createUser()
    }
}

data class AuthState(
    val isAuthenticated: Boolean = false,
)