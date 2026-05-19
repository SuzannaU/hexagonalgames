package com.openclassrooms.hexagonal.games.screen

import android.text.TextUtils
import android.util.Log
import com.google.firebase.FirebaseNetworkException
import com.openclassrooms.hexagonal.games.data.repository.PostRepository
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import com.openclassrooms.hexagonal.games.domain.model.User
import com.openclassrooms.hexagonal.games.screen.addComment.AddCommentViewModel
import com.openclassrooms.hexagonal.games.screen.addPost.FormEvent
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AddCommentViewModelTest {

    private val postRepository: PostRepository = mockk()
    private val userRepository: UserRepository = mockk()
    private lateinit var viewModel: AddCommentViewModel

    @BeforeEach
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0


        mockkStatic(TextUtils::class)
        every { TextUtils.isEmpty(any()) } returns false

        every { userRepository.getCurrentUser() } returns User()
        viewModel = AddCommentViewModel(postRepository, userRepository)
    }

    @Test
    fun onAction_shouldUpdateState() {
        val formEvent = FormEvent.CommentChanged("newComment")

        viewModel.onAction(formEvent)
        assertEquals("newComment", viewModel.comment.value.content)
    }

    @Test
    fun addComment_shouldCallRepoAndUpdateState() {
        every { postRepository.addComment(any(), any()) } returns Unit

        viewModel.addComment("postId")
        assertTrue(viewModel.saveState.value is AddCommentViewModel.SaveState.CommentSaved)
        verify { postRepository.addComment(any(), any()) }
    }

    @Test
    fun addComment_withNetworkException_shouldCallRepoAndUpdateState() {
        every { postRepository.addComment(any(), any()) } throws FirebaseNetworkException("")

        viewModel.addComment("postId")
        assertTrue(viewModel.saveState.value is AddCommentViewModel.SaveState.NetworkError)
        verify { postRepository.addComment(any(), any()) }
    }

    @Test
    fun addComment_withException_shouldCallRepoAndUpdateState() {
        every { postRepository.addComment(any(), any()) } throws RuntimeException("")

        viewModel.addComment("postId")
        assertTrue(viewModel.saveState.value is AddCommentViewModel.SaveState.UnknownError)
        verify { postRepository.addComment(any(), any()) }
    }
}