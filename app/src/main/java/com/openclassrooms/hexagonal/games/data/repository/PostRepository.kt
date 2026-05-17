package com.openclassrooms.hexagonal.games.data.repository

import android.net.Uri
import android.util.Log
import com.openclassrooms.hexagonal.games.data.service.PostApi
import com.openclassrooms.hexagonal.games.domain.model.Comment
import com.openclassrooms.hexagonal.games.domain.model.Post
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * This class provides a repository for accessing and managing Post data.
 * It utilizes dependency injection to retrieve a PostApi instance for interacting
 * with the data source. The class is marked as a Singleton using @Singleton annotation,
 * ensuring there's only one instance throughout the application.
 */

class PostRepository @Inject constructor(
    private val postApi: PostApi
) {

    suspend fun getPostById(postId: String): Post? {
        return postApi.getPostById(postId = postId)
    }

    fun getCommentsByPostById(postId: String): Flow<List<Comment>> {
        return postApi.getCommentByPostId(postId = postId)
    }

    /**
     * Retrieves a Flow object containing a list of Posts ordered by creation date
     * in descending order.
     *
     * @return Flow containing a list of Posts.
     */
    fun getPosts(): Flow<List<Post>> {
        return postApi.getPostsOrderByCreationDateDesc()
    }
    /**
     * Adds a new Post to the data source using the injected PostApi.
     *
     * @param post The Post object to be added.
     */
    fun addPost(post: Post, photoUri: Uri?) {
        postApi.addPost(post, photoUri)
    }

    fun addComment(comment: Comment, postId: String) {
        postApi.addComment(comment, postId)
    }
}
