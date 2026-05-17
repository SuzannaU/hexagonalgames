package com.openclassrooms.hexagonal.games.screen

import com.openclassrooms.hexagonal.games.data.repository.PostRepository
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import com.openclassrooms.hexagonal.games.screen.addComment.AddCommentViewModel
import com.openclassrooms.hexagonal.games.screen.addPost.FormEvent
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AddCommentViewModelTest {

    private val postRepository: PostRepository = mockk()
    private val userRepository: UserRepository = mockk()
    private lateinit var viewModel: AddCommentViewModel

    @BeforeEach
    fun setup() {
        every { userRepository.getCurrentUser()} returns null
        viewModel = AddCommentViewModel(postRepository, userRepository)
    }

    @Test
    fun onAction_shouldUpdateState() {
        val formEvent = FormEvent.CommentChanged("newComment")

        viewModel.onAction(formEvent)
        assertEquals("newComment", viewModel.comment.value.content)
    }
}