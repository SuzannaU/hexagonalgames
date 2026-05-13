package com.openclassrooms.hexagonal.games.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.screen.Screen
import com.openclassrooms.hexagonal.games.screen.account.AccountScreen
import com.openclassrooms.hexagonal.games.screen.ad.AddScreen
import com.openclassrooms.hexagonal.games.screen.homefeed.HomeFeedScreen
import com.openclassrooms.hexagonal.games.screen.settings.SettingsScreen
import com.openclassrooms.hexagonal.games.ui.theme.HexagonalGamesTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity for the application. This activity serves as the entry point and container for the navigation
 * fragment. It handles setting up the toolbar, navigation controller, and action bar behavior.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract(),
        ::onSignInResult,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()
            HexagonalGamesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    val authState = viewModel.authState.collectAsStateWithLifecycle()

                    HexagonalGamesNavHost(
                        modifier = Modifier.padding(innerPadding),
                        isUserAuthenticated = authState.value.isAuthenticated,
                        navHostController = navController,
                        onSignInClicked = { startSignInActivity() },
                        onSelectPhotoClicked = ::launchPhotoPicker,
                        afterSignOut = { viewModel.userIsNotAuthenticated() },
                        onNotificationDisabledClicked = { launchAppSettings() },
                        showNoPostsToast = { showNoPostsToast() },
                        showNotAuthentifiedToast = { showNotAuthentifiedToast() },
                        showUnknownErrorToast = { showUnknownErrorToast() },
                        showNotificationsEnabledToast = { showNotificationsEnabledToast() },
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = viewModel.getCurrentUser()
        if (currentUser != null) {
            viewModel.userIsAuthenticated()
        } else {
            viewModel.userIsNotAuthenticated()
        }
    }

    private fun startSignInActivity() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build()
        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setTheme(R.style.Theme_HexagonalGames)
            .setAvailableProviders(providers)
            .setAlwaysShowSignInMethodScreen(true)
            .setLogo(R.drawable.logo_hexagonal)
            .build()

        signInLauncher.launch(signInIntent)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            viewModel.userIsAuthenticated()
            viewModel.createUser()
        } else if (response?.error != null) {
            viewModel.userIsNotAuthenticated()
        }
    }

    private fun launchPhotoPicker(pickMediaLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>) {
        pickMediaLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun launchAppSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val intent = Intent().apply {
                action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            }
            startActivity(intent)
        }
    }

    private fun showNoPostsToast() {
        Toast.makeText(this, getString(R.string.no_posts), Toast.LENGTH_SHORT).show()
    }

    private fun showNotAuthentifiedToast() {
        Toast.makeText(this, getString(R.string.need_authentication), Toast.LENGTH_SHORT).show()
    }

    private fun showUnknownErrorToast() {
        Toast.makeText(this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show()
    }

    private fun showNotificationsEnabledToast() {
        Toast.makeText(this, getString(R.string.notifications_enabled), Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun HexagonalGamesNavHost(
    modifier: Modifier = Modifier,
    isUserAuthenticated: Boolean,
    navHostController: NavHostController,
    onSignInClicked: () -> Unit,
    onSelectPhotoClicked: ((ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>)) -> Unit,
    afterSignOut: () -> Unit,
    onNotificationDisabledClicked: () -> Unit,
    showNoPostsToast: () -> Unit,
    showNotAuthentifiedToast: () -> Unit,
    showUnknownErrorToast: () -> Unit,
    showNotificationsEnabledToast: () -> Unit,
) {
    NavHost(
        navController = navHostController,
        startDestination = Screen.HomeFeed.route,
        modifier = modifier,
    ) {
        composable(route = Screen.HomeFeed.route) {
            HomeFeedScreen(
                onPostClick = {
                    //TODO
                },
                onSettingsClick = {
                    navHostController.navigate(Screen.Settings.route)
                },
                onFABClick = {
                    if (isUserAuthenticated) {
                        navHostController.navigate(Screen.AddPost.route)
                    } else {
                        showNotAuthentifiedToast()
                    }

                },
                onAccountClick = {
                    if (isUserAuthenticated) {
                        navHostController.navigate(Screen.Account.route)
                    } else {
                        onSignInClicked()
                    }
                },
                showNoPostsToast = showNoPostsToast
            )
        }
        composable(route = Screen.AddPost.route) {
            AddScreen(
                onBackClick = { navHostController.navigateUp() },
                onSaveSuccessful = { navHostController.navigateUp() },
                onSaveFailed = showUnknownErrorToast,
                onSelectPhotoClick = onSelectPhotoClicked,
            )
        }
        composable(route = Screen.Settings.route) {
            SettingsScreen(
                onBackClick = { navHostController.navigateUp() },
                onNotificationEnabledClicked = showNotificationsEnabledToast,
                onNotificationDisabledClicked = onNotificationDisabledClicked
            )
        }
        composable(route = Screen.Account.route) {
            AccountScreen(
                onBackClick = { navHostController.navigateUp() },
                accountDeleted = { navHostController.navigate(Screen.HomeFeed.route) },
                unknownError = showUnknownErrorToast,
                afterSignOut = {
                    navHostController.navigate(Screen.HomeFeed.route)
                    afterSignOut()
                }
            )
        }
    }
}
