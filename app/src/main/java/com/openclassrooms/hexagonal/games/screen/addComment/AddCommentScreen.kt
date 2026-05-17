package com.openclassrooms.hexagonal.games.screen.addComment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.domain.model.Comment
import com.openclassrooms.hexagonal.games.screen.addPost.AddViewModel
import com.openclassrooms.hexagonal.games.screen.addPost.FormError
import com.openclassrooms.hexagonal.games.screen.addPost.FormEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCommentScreen(
    postId: String,
    modifier: Modifier = Modifier,
    viewModel: AddCommentViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onSaveSuccessful: () -> Unit,
    onNetworkError: () -> Unit,
    onUnknownError: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(id = R.string.add_comment))
                },
                navigationIcon = {
                    IconButton(onClick = {
                        onBackClick()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.contentDescription_go_back)
                        )
                    }
                }
            )
        }
    ) { contentPadding ->
        val comment by viewModel.comment.collectAsStateWithLifecycle()
        val error by viewModel.error.collectAsStateWithLifecycle()
        val saveState = viewModel.saveState.collectAsStateWithLifecycle()

        when (saveState.value) {
            AddCommentViewModel.SaveState.CommentSaved -> {
                onSaveSuccessful()
            }

            AddCommentViewModel.SaveState.NetworkError -> {
                onNetworkError()
            }

            AddCommentViewModel.SaveState.UnknownError -> {
                onUnknownError()
            }

            else -> {}
        }

        AddComment(
            modifier = Modifier.padding(contentPadding),
            comment = comment,
            onCommentChanged = { viewModel.onAction(FormEvent.CommentChanged(it))},
            onSaveCommentClicked = { viewModel.addComment(postId) },
            error = error,
        )
    }
}


@Composable
private fun AddComment(
    modifier: Modifier = Modifier,
    comment: Comment,
    onCommentChanged: (String) -> Unit,
    onSaveCommentClicked: () -> Unit,
    error: FormError?
) {

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
            OutlinedTextField(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
                value = comment.content,
                isError = error is FormError.CommentError,
                onValueChange = { onCommentChanged(it) },
                label = { Text(stringResource(id = R.string.hint_comment)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
            if (error is FormError.CommentError) {
                Text(
                    text = stringResource(id = error.messageRes),
                    color = MaterialTheme.colorScheme.error,
                )
            }


        Button(
            enabled = error == null,
            onClick = { onSaveCommentClicked() }
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = stringResource(id = R.string.action_save)
            )
        }
    }
}