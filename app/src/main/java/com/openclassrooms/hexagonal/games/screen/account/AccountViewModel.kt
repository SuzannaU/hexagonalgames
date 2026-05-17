package com.openclassrooms.hexagonal.games.screen.account

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _signOutState = MutableStateFlow<SignOutState>(SignOutState.Idle)
    val signOutState = _signOutState.asStateFlow()

    fun signOut() {
        try {
            userRepository.signOut()
            _signOutState.value = SignOutState.Success
            Log.i("TAG", "user signed out")
        } catch (e: Exception) {
            _signOutState.value = SignOutState.Failed
            Log.e("TAG", "Sign out failed: ${e.message}")
        }
    }

    private val _deleteAccountState = MutableStateFlow<DeleteAccountState>(DeleteAccountState.Idle)
    val deleteAccountState = _deleteAccountState.asStateFlow()

    fun deleteAccount() {
        viewModelScope.launch {
            _deleteAccountState.value = DeleteAccountState.OnGoing

            val success = userRepository.deleteAccount()
            _deleteAccountState.value = if (success) {
                DeleteAccountState.Success
            } else {
                DeleteAccountState.Failed
            }
        }
    }
}

sealed class SignOutState {
    object Idle : SignOutState()
    object Success : SignOutState()
    object Failed : SignOutState()
}

sealed class DeleteAccountState {
    object Idle : DeleteAccountState()
    object OnGoing : DeleteAccountState()
    object Success : DeleteAccountState()
    object Failed : DeleteAccountState()
}