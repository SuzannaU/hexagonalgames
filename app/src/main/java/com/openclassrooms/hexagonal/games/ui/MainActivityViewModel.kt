package com.openclassrooms.hexagonal.games.ui

import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val firebaseAuth: FirebaseAuth,
) : ViewModel() {

    private val _authState = MutableStateFlow(AuthState())
    val authState = _authState.asStateFlow()

    private lateinit var listener: FirebaseAuth.AuthStateListener

    private val _authNetworkState = MutableStateFlow(NetworkState())
    val authNetworkState = _authNetworkState.asStateFlow()

    init {
        observeAuthState()
    }

    private fun observeAuthState() {
        try {
            listener = FirebaseAuth.AuthStateListener {
                _authState.value = AuthState(isAuthenticated = it.currentUser != null)
            }
            firebaseAuth.addAuthStateListener(listener)
            _authNetworkState.update {
                _authNetworkState.value.copy(isAuthConnected = true)
            }
        } catch (e: FirebaseNetworkException) {
            _authNetworkState.update {
                _authNetworkState.value.copy(isAuthConnected = false)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        firebaseAuth.removeAuthStateListener(listener)
    }

    fun createUser() {
        userRepository.createUser()
    }
}

data class AuthState(
    val isAuthenticated: Boolean = false,
)

data class NetworkState(
    val isAuthConnected: Boolean = false,
)