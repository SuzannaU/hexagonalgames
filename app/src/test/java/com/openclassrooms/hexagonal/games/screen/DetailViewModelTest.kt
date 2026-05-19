package com.openclassrooms.hexagonal.games.screen

import com.openclassrooms.hexagonal.games.data.repository.PostRepository
import com.openclassrooms.hexagonal.games.domain.model.Comment
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.screen.detail.DetailScreenState
import com.openclassrooms.hexagonal.games.screen.detail.DetailViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    private val postRepository: PostRepository = mockk()
    private lateinit var viewModel: DetailViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        viewModel = DetailViewModel(postRepository)

    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadPost_shouldCallRepoAndUpdateState() = runTest {
        coEvery { postRepository.getPostById(any()) } returns Post()
        every { postRepository.getCommentsByPostById(any()) } returns flowOf(listOf(Comment()))

        viewModel.loadPost("postId")
        advanceUntilIdle()

        val result = viewModel.detailUiState.value
        assertTrue(result is DetailScreenState.PostLoaded)

        val state = result as DetailScreenState.PostLoaded
        assertNotNull(state.post)
        assertTrue(state.comments.size == 1)
        coVerify {
            postRepository.getPostById(any())
            postRepository.getCommentsByPostById(any())
        }
    }

    @Test
    fun loadPost_withNoPost_shouldCallRepoAndUpdateState() = runTest {
        coEvery { postRepository.getPostById(any()) } returns null

        viewModel.loadPost("postId")
        advanceUntilIdle()

        val result = viewModel.detailUiState.value
        assertTrue(result is DetailScreenState.NoPost)
        coVerify { postRepository.getPostById(any()) }
    }
}