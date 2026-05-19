package com.openclassrooms.hexagonal.games.screen

import com.openclassrooms.hexagonal.games.data.repository.PostRepository
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.screen.homefeed.HomeFeedViewModel
import com.openclassrooms.hexagonal.games.screen.homefeed.HomeScreenState
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeFeedViewModelTest {

    private val postRepository: PostRepository = mockk()
    private lateinit var viewModel: HomeFeedViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())

    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun init_shouldCallRepoAndUpdateState() = runTest {
        every { postRepository.getPosts() } returns flowOf(listOf(Post()))


        viewModel = HomeFeedViewModel(postRepository)
        advanceUntilIdle()

        val result = viewModel.homeUiState.value
        assertTrue(result is HomeScreenState.PostsLoaded)

        val state = result as HomeScreenState.PostsLoaded
        assertTrue(state.posts.size == 1)
        verify { postRepository.getPosts() }
    }

    @Test
    fun init_withNoPosts_shouldCallRepoAndUpdateState() = runTest {
        every { postRepository.getPosts() } returns flowOf(emptyList())


        viewModel = HomeFeedViewModel(postRepository)
        advanceUntilIdle()

        val result = viewModel.homeUiState.value
        assertTrue(result is HomeScreenState.NoPosts)
        verify { postRepository.getPosts() }
    }
}