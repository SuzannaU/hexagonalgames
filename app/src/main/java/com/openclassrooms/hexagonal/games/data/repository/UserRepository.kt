package com.openclassrooms.hexagonal.games.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.hexagonal.games.domain.model.User
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) {

    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    fun createUser() {
        val user = getCurrentUser()
        var uid: String?
        if (user != null) {
            uid = user.uid
            val userToCreate = User(
                id = uid,
                pictureUrl = user.photoUrl.toString(),
                username = user.displayName ?: "",
            )

            firestore.collection("users").document(uid)
                .set(userToCreate)
                .addOnSuccessListener { Log.i("TAG", "user inserted in firestore") }
                .addOnFailureListener { Log.w("TAG", "user NOT inserted in firestore") }
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }

    suspend fun deleteAccount(): Boolean {
        val user = getCurrentUser() ?: return false

        try {
            firestore.collection("users").document(user.uid)
                .delete().await()
            Log.i("TAG", "user deleted from Firestore")
            user.delete().await()
            Log.i("TAG", "user deleted from Firebase Auth")
            return true
        } catch (e: Exception) {
            Log.w("TAG", "user not deleted from Auth: ${e.message}")
            return false
        }
    }
}