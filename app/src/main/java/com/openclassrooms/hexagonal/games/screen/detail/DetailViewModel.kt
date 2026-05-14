package com.openclassrooms.hexagonal.games.screen.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.hexagonal.games.data.repository.PostRepository
import com.openclassrooms.hexagonal.games.domain.model.Comment
import com.openclassrooms.hexagonal.games.domain.model.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val postRepository: PostRepository,
) : ViewModel() {

    private val _detailUiState = MutableStateFlow<DetailScreenState>(DetailScreenState.Loading)
    val detailUiState = _detailUiState.asStateFlow()

    fun loadPost(postId: String) {
        viewModelScope.launch {
            val post = postRepository.getPostById(postId)
            if (post == null) {
                _detailUiState.value = DetailScreenState.NoPost
            } else {
                postRepository.getCommentsByPostById(post.id).collect{ comments ->
                    _detailUiState.value = DetailScreenState.PostLoaded(post, comments)
                }
            }
        }
    }
}

sealed class DetailScreenState {

    object Loading : DetailScreenState()
    object NoPost : DetailScreenState()

    data class ErrorState(
        val errorMessage: String?,
    ) : DetailScreenState()

    data class PostLoaded(
        val post: Post,
        val comments: List<Comment>,
    ) : DetailScreenState()
}