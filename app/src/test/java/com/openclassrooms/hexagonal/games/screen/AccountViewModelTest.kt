package com.openclassrooms.hexagonal.games.screen

import android.util.Log
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import com.openclassrooms.hexagonal.games.screen.account.AccountViewModel
import com.openclassrooms.hexagonal.games.screen.account.DeleteAccountState
import com.openclassrooms.hexagonal.games.screen.account.SignOutState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AccountViewModelTest {

    private val userRepository: UserRepository = mockk()
    private lateinit var viewModel: AccountViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        viewModel = AccountViewModel(userRepository)
    }

    @Test
    fun signOut_shouldCallRepoAndUpdateState() {
        every {userRepository.signOut()} returns Unit

        viewModel.signOut()
        assertTrue(viewModel.signOutState.value is SignOutState.Success)
        verify { userRepository.signOut() }
    }

    @Test
    fun signOut_shouldCallRepoAndUpdateStateWhenFails() {
        every {userRepository.signOut()} throws RuntimeException()

        viewModel.signOut()
        assertTrue(viewModel.signOutState.value is SignOutState.Failed)
        verify { userRepository.signOut() }
    }

    @Test
    fun deleteAccount_shouldCallRepoAndUpdateStateWhenSuccess() = runTest {

        coEvery { userRepository.deleteAccount() } returns true

        viewModel.deleteAccount()
        advanceUntilIdle()

        assertTrue(viewModel.deleteAccountState.value is DeleteAccountState.Success)
        coVerify { userRepository.deleteAccount() }
    }

    @Test
    fun deleteAccount_shouldCallRepoAndUpdateStateWhenFails() = runTest {

        coEvery { userRepository.deleteAccount() } returns false

        viewModel.deleteAccount()
        advanceUntilIdle()

        assertTrue(viewModel.deleteAccountState.value is DeleteAccountState.Failed)
        coVerify { userRepository.deleteAccount() }
    }
}