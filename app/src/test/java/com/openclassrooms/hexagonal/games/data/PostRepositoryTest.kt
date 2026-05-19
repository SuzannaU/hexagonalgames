package com.openclassrooms.hexagonal.games.data

import android.net.Uri
import com.openclassrooms.hexagonal.games.data.repository.PostRepository
import com.openclassrooms.hexagonal.games.data.service.PostApi
import com.openclassrooms.hexagonal.games.domain.model.Comment
import com.openclassrooms.hexagonal.games.domain.model.Post
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class PostRepositoryTest {

    private val postApi: PostApi = mockk()
    private val uri: Uri = mockk()
    private val postRepository = PostRepository(postApi)
    private lateinit var post: Post
    private lateinit var comment: Comment
    private lateinit var posts: List<Post>
    private lateinit var comments: List<Comment>

    @BeforeEach
    fun setup() {

        post = Post(id = "postId")
        posts = listOf(
            Post(),
            Post(),
        )
        comment = Comment()
        comments = listOf(
            Comment(),
            Comment(),
        )
        coEvery { postApi.getPostsOrderByCreationDateDesc() } returns flowOf(posts)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun getPostById_shouldCallApiAndReturnPost() = runTest {

        coEvery { postApi.getPostById(any()) } returns post

        val result = postRepository.getPostById("postId")
        assertNotNull(result)
        assertEquals(post.id, result.id)
        coVerify { postApi.getPostById(any()) }
    }

    @Test
    fun getCommentsByPostId_shouldCallApiAndReturnComments() = runTest {

        coEvery { postApi.getCommentByPostId(any()) } returns flowOf(comments)

        val result = postRepository.getCommentsByPostById("postId").first()
        assertFalse(result.isEmpty())
        assertTrue(result.size == 2)
        coVerify { postApi.getCommentByPostId(any()) }
    }

    @Test
    fun getPosts_shouldCallApiAndReturnPosts() = runTest {

        coEvery { postApi.getPostsOrderByCreationDateDesc() } returns flowOf(posts)

        val result = postRepository.getPosts().first()
        assertFalse(result.isEmpty())
        assertTrue(result.size == 2)
        coVerify { postApi.getPostsOrderByCreationDateDesc() }
    }

    @Test
    fun addPost_shouldCallApi() {

        every { postApi.addPost(any(), any()) } returns Unit

        postRepository.addPost(post, uri)
        verify { postApi.addPost(any(), any()) }
    }

    @Test
    fun addComment_shouldCallApi() {

        every { postApi.addComment(any(), any()) } returns Unit

        postRepository.addComment(comment, "postId")
        verify { postApi.addComment(any(), any()) }
    }

}