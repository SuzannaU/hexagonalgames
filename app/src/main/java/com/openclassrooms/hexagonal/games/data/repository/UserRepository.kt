package com.openclassrooms.hexagonal.games.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class UserRepository (private val firebaseAuth: FirebaseAuth){

    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    suspend fun signOut() {
        firebaseAuth.signOut()
    }

//    fun getCurrentUserUid() : String? {
//        return getCurrentUser()?.uid
//    }


}