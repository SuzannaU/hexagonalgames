package com.openclassrooms.hexagonal.games.data.service

import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.dataObjects
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import com.openclassrooms.hexagonal.games.domain.model.Comment
import com.openclassrooms.hexagonal.games.domain.model.Post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class PostApiImpl(
    private val firestore: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage,
) : PostApi {

    override suspend fun getPostById(postId: String): Post? {
        return firestore
            .collection("posts")
            .document(postId)
            .get()
            .await()
            .toObject<Post>()
    }

    override fun getCommentByPostId(postId: String): Flow<List<Comment>> {
        return firestore
            .collection("posts")
            .document(postId)
            .collection("comments")
            .orderBy("dateCreated", Query.Direction.DESCENDING)
            .dataObjects<Comment>()
    }

    override fun getPostsOrderByCreationDateDesc(): Flow<List<Post>> {
        val posts = firestore
            .collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .dataObjects<Post>()
        return posts
    }

    override fun addPost(post: Post, photoUri: Uri?) {

        if (photoUri != null) {
            uploadPhoto(photoUri).addOnSuccessListener { uri ->
                val postToSave = post.copy(
                    photoUrl = uri.toString()
                )
                firestore.collection("posts").document(post.id)
                    .set(postToSave)
            }
        }
    }

    private fun uploadPhoto(photoUri: Uri): Task<Uri> {

        val uuid = UUID.randomUUID().toString()
        val storageRef = firebaseStorage.reference
        val photoRef = storageRef.child("photos").child(uuid)

        val uploadTask = photoRef.putFile(photoUri)
        val urlTask = uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            photoRef.downloadUrl
        }
        return urlTask
    }

    override fun addComment(comment: Comment, postId: String) {

        firestore.collection("posts")
            .document(postId)
            .collection("comments")
            .add(comment)
    }
}