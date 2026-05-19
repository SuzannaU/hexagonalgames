package com.openclassrooms.hexagonal.games.screen

import android.net.Uri
import android.text.TextUtils
import android.util.Log
import com.google.firebase.FirebaseNetworkException
import com.openclassrooms.hexagonal.games.data.repository.PostRepository
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import com.openclassrooms.hexagonal.games.domain.model.User
import com.openclassrooms.hexagonal.games.screen.addPost.AddViewModel
import com.openclassrooms.hexagonal.games.screen.addPost.FormEvent
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AddViewModelTest {

    private val uri: Uri = mockk()
    private val postRepository: PostRepository = mockk()
    private val userRepository: UserRepository = mockk()
    private lateinit var viewModel: AddViewModel


    @BeforeEach
    fun setup() {
        mockkStatic(Log::class)
        every { Log.e(any(), any()) } returns 0

        mockkStatic(TextUtils::class)
        every { TextUtils.isEmpty(any()) } returns false

        every { userRepository.getCurrentUser() } returns User()
        viewModel = AddViewModel(postRepository, userRepository)
    }

    @Test
    fun onAction_shouldUpdateState() {
        val formEvent1 = FormEvent.PhotoChanged(uri)
        val formEvent2 = FormEvent.TitleChanged("newTitle")
        val formEvent3 = FormEvent.DescriptionChanged("newDescription")

        viewModel.onAction(formEvent1)
        viewModel.onAction(formEvent2)
        viewModel.onAction(formEvent3)

        assertEquals(uri, viewModel.uriState.value)
        assertEquals("newTitle", viewModel.post.value.title)
        assertEquals("newDescription", viewModel.post.value.description)
    }

    @Test
    fun addPost_shouldCallRepoAndUpdateState() {
        every { postRepository.addPost(any(), any()) } returns Unit

        viewModel.addPost()
        assertTrue(viewModel.saveState.value is AddViewModel.SaveState.PostSaved)
        verify { postRepository.addPost(any(), any()) }
    }

    @Test
    fun addPost_withNetworkException_shouldCallRepoAndUpdateState() {
        every { postRepository.addPost(any(), any()) } throws FirebaseNetworkException("")

        viewModel.addPost()
        assertTrue(viewModel.saveState.value is AddViewModel.SaveState.NetworkError)
        verify { postRepository.addPost(any(), any()) }
    }

    @Test
    fun addPost_withException_shouldCallRepoAndUpdateState() {
        every { postRepository.addPost(any(), any()) } throws RuntimeException("")

        viewModel.addPost()
        assertTrue(viewModel.saveState.value is AddViewModel.SaveState.UnknownError)
        verify { postRepository.addPost(any(), any()) }
    }
}