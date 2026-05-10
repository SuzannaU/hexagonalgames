package com.openclassrooms.hexagonal.games.data.service

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.dataObjects
import com.openclassrooms.hexagonal.games.domain.model.Post
import kotlinx.coroutines.flow.Flow

class PostApiImpl(
    private val firestore: FirebaseFirestore,
) : PostApi {


    override fun getPostsOrderByCreationDateDesc(): Flow<List<Post>> {
        val posts = firestore
            .collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .dataObjects<Post>()
        return posts
    }

    override fun addPost(post: Post) {
        firestore.collection("posts").document(post.id)
            .set(post)
    }
}