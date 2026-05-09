package com.openclassrooms.hexagonal.games.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class UserRepository(private val firebaseAuth: FirebaseAuth) {

    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    fun signOut() {
        firebaseAuth.signOut()
    }

    fun deleteAccount(): Boolean {
        val user = getCurrentUser()
        var deleteTaskSuccess = false
        user?.delete()?.addOnSuccessListener { task ->
            deleteTaskSuccess = true
        }
        return deleteTaskSuccess
    }
}