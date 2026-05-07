package com.openclassrooms.hexagonal.games.domain.manager

import android.util.Log
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import javax.inject.Inject

class UserManager @Inject constructor(private val userRepository: UserRepository) {

    fun createUser() {
        Log.i("TAG", "createUser called")
    }
}