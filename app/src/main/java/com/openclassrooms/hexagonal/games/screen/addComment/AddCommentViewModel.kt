package com.openclassrooms.hexagonal.games.screen.addComment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import com.openclassrooms.hexagonal.games.data.repository.PostRepository
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import com.openclassrooms.hexagonal.games.domain.model.Comment
import com.openclassrooms.hexagonal.games.domain.model.User
import com.openclassrooms.hexagonal.games.screen.addPost.AddViewModel
import com.openclassrooms.hexagonal.games.screen.addPost.FormError
import com.openclassrooms.hexagonal.games.screen.addPost.FormEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AddCommentViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val user = userRepository.getCurrentUser()
    private val _saveState = MutableStateFlow<SaveState>(SaveState.Idle)
    val saveState = _saveState.asStateFlow()

    private var _comment = user?.let {
        MutableStateFlow(
            Comment(
                content = "",
                author = user,
            )
        )
    } ?: MutableStateFlow(
        Comment()
    )

    val comment = _comment.asStateFlow()

    val error = comment.map {
        verifyComment()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    fun onAction(formEvent: FormEvent) {
        when (formEvent) {
            is FormEvent.CommentChanged -> {
                _comment.value = _comment.value.copy(
                    content = formEvent.commentContent
                )
            }

            else -> {}
        }
    }

    fun addComment(postId: String) {
        val user = userRepository.getCurrentUser()
        user?.let {
            try {
                postRepository.addComment(
                    comment = _comment.value,
                    postId = postId,
                )
                _saveState.value = SaveState.CommentSaved
                Log.i("TAG", "Comment added successfully")
            } catch (e: FirebaseNetworkException) {
                _saveState.value = SaveState.NetworkError
                Log.e("TAG", "Network Error while adding comment: ${e.message}")
            } catch (e: Exception) {
                _saveState.value = SaveState.UnknownError
                Log.e("TAG", "Error while adding comment: ${e.message}")
            }
        }
    }

    private fun verifyComment(): FormError? {
        if (_comment.value.content.isEmpty()) {
            return FormError.CommentError
        }
        return null
    }
    sealed class SaveState {

        object Idle: SaveState()
        object CommentSaved: SaveState()
        object NetworkError: SaveState()
        object UnknownError: SaveState()
    }
}