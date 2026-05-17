package com.openclassrooms.hexagonal.games.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.hexagonal.games.domain.model.User
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) {

    fun getCurrentUser(): User? {
        val authUser = firebaseAuth.currentUser
        val user = authUser?.let {
            User(
                id = it.uid,
                pictureUrl = it.photoUrl.toString(),
                username = it.displayName ?: "",
            )
        }
        return user
    }

    fun createUser() {
        val user = getCurrentUser()
        var uid: String?
        if (user != null) {
            uid = user.id
            firestore.collection("users").document(uid)
                .set(user)
                .addOnSuccessListener { Log.i("TAG", "user inserted in firestore") }
                .addOnFailureListener { Log.w("TAG", "user NOT inserted in firestore") }
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }

    suspend fun deleteAccount(): Boolean {
        val authUser = firebaseAuth.currentUser ?: return false

        try {
            firestore.collection("users").document(authUser.uid)
                .delete().await()
            Log.i("TAG", "user deleted from Firestore")
            authUser.delete().await()
            Log.i("TAG", "user deleted from Firebase Auth")
            return true
        } catch (e: Exception) {
            Log.w("TAG", "user not deleted from Auth: ${e.message}")
            return false
        }
    }
}