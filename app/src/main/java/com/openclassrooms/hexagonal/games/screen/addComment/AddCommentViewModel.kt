package com.openclassrooms.hexagonal.games.screen.addComment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.hexagonal.games.data.repository.PostRepository
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import com.openclassrooms.hexagonal.games.domain.model.Comment
import com.openclassrooms.hexagonal.games.domain.model.User
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

    private val firebaseUser = userRepository.getCurrentUser()

    private var _comment = firebaseUser?.let {
        MutableStateFlow(
            Comment(
                content = "",
                author = User(
                    id = it.uid,
                    pictureUrl = it.photoUrl.toString(),
                    username = it.displayName ?: "",
                ),
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

    fun addCommentSuccessful(postId: String): Boolean {
        val user = userRepository.getCurrentUser()
        user?.let {
            try {
                postRepository.addComment(
                    comment = _comment.value,
                    postId = postId,
                )
            } catch (e: Exception) {
                return false
            }
        }
        return true
    }

    private fun verifyComment(): FormError? {
        if (_comment.value.content.isEmpty()) {
            return FormError.CommentError
        }
        return null
    }
}