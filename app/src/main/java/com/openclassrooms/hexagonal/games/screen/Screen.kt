package com.openclassrooms.hexagonal.games.screen

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.openclassrooms.hexagonal.games.domain.model.Post

sealed class Screen(
  val route: String,
  val navArguments: List<NamedNavArgument> = emptyList()
) {
  data object HomeFeed : Screen("homefeed")
  
  data object AddPost : Screen("addPost")
  
  data object Settings : Screen("settings")

  data object Account : Screen("account")

  data object Details : Screen(
    route = "post/{postId}",
    navArguments = listOf(
      navArgument("postId") {
        type = NavType.StringType
      }
    )
  ) {
    fun createRoute(postId: String): String {
      return "post/$postId"
    }
  }

  data object AddComment : Screen(
    route = "post/{postId}/addComment",
    navArguments = listOf(
      navArgument("postId") {
        type = NavType.StringType
      }
    )
  ) {
    fun createRoute(postId: String): String {
      return "post/$postId/addComment"
    }
  }
}