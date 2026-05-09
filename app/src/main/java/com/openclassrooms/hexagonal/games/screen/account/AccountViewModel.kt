package com.openclassrooms.hexagonal.games.screen.account

import androidx.lifecycle.ViewModel
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    fun signOut() {
        return userRepository.signOut()
    }

    fun deleteAccount(): Boolean {
        return userRepository.deleteAccount()
    }
}