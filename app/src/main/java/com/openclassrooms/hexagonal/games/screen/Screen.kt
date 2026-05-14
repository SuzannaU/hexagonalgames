package com.openclassrooms.hexagonal.games.screen

import androidx.navigation.NamedNavArgument
import com.openclassrooms.hexagonal.games.domain.model.Post

sealed class Screen(
  val route: String,
  val navArguments: List<NamedNavArgument> = emptyList()
) {
  data object HomeFeed : Screen("homefeed")
  
  data object AddPost : Screen("addPost")
  
  data object Settings : Screen("settings")

  data object Account : Screen("account")

  data class Details(val postId: String) : Screen("details/$postId")
  // TODO look how to use this thing in navigation
}