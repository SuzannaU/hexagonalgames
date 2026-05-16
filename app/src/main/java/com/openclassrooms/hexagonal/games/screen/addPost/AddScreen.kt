package com.openclassrooms.hexagonal.games.screen.addPost

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.ui.theme.HexagonalGamesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    modifier: Modifier = Modifier,
    viewModel: AddViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onSaveSuccessful: () -> Unit,
    onNetworkError: () -> Unit,
    onUnknownError: () -> Unit,
    onSelectPhotoClick: (ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>) -> Unit,
) {

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(id = R.string.add_fragment_label))
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
        val post by viewModel.post.collectAsStateWithLifecycle()
        val error by viewModel.error.collectAsStateWithLifecycle()
        val saveState = viewModel.saveState.collectAsStateWithLifecycle()

        when (saveState.value) {
            AddViewModel.SaveState.PostSaved -> {
                onSaveSuccessful()
            }

            AddViewModel.SaveState.NetworkError -> {
                onNetworkError()
            }

            AddViewModel.SaveState.UnknownError -> {
                onUnknownError()
            }

            else -> {}
        }

        CreatePost(
            modifier = Modifier.padding(contentPadding),
            error = error,
            title = post.title,
            onTitleChanged = { viewModel.onAction(FormEvent.TitleChanged(it)) },
            description = post.description,
            onDescriptionChanged = { viewModel.onAction(FormEvent.DescriptionChanged(it)) },
            onSaveClicked = { viewModel.addPost() },
            onSelectPhotoClick = onSelectPhotoClick,
            onPhotoPicked = { uri ->
                if (uri != null) {
                    viewModel.onAction(FormEvent.PhotoChanged(uri))
                }
            }
        )
    }
}

@Composable
private fun CreatePost(
    modifier: Modifier = Modifier,
    title: String,
    onTitleChanged: (String) -> Unit,
    description: String,
    onDescriptionChanged: (String) -> Unit,
    onSaveClicked: () -> Unit,
    onSelectPhotoClick: (ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>) -> Unit,
    onPhotoPicked: (Uri?) -> Unit,
    error: FormError?
) {
    val scrollState = rememberScrollState()

    val selectedPhotoUri = rememberSaveable { mutableStateOf<Uri?>(null) }

    val photoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            selectedPhotoUri.value = uri
            onPhotoPicked(uri)
        }
    )

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
                value = title,
                isError = error is FormError.TitleError,
                onValueChange = { onTitleChanged(it) },
                label = { Text(stringResource(id = R.string.hint_title)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                singleLine = true
            )
            if (error is FormError.TitleError) {
                Text(
                    text = stringResource(id = error.messageRes),
                    color = MaterialTheme.colorScheme.error,
                )
            }

            OutlinedTextField(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
                value = description,
                isError = error is FormError.DescriptionError,
                onValueChange = { onDescriptionChanged(it) },
                label = { Text(stringResource(id = R.string.hint_description)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
            if (error is FormError.DescriptionError) {
                Text(
                    text = stringResource(id = error.messageRes),
                    color = MaterialTheme.colorScheme.error,
                )
            }

            AsyncImage(
                model = selectedPhotoUri.value ?: R.drawable.placeholder,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(top = 16.dp),
                contentScale = ContentScale.Fit,
            )
            if (error is FormError.PhotoError) {
                Text(
                    text = stringResource(id = error.messageRes),
                    color = MaterialTheme.colorScheme.error,
                )
            }

            Button(
                onClick = { onSelectPhotoClick(photoPicker) },
            ) {
                Text(text = stringResource(R.string.select_photo))
            }

        }
        Button(
            enabled = error == null,
            onClick = { onSaveClicked() }
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = stringResource(id = R.string.action_save)
            )
        }
    }
}

@PreviewLightDark
@PreviewScreenSizes
@Composable
private fun CreatePostPreview() {
    HexagonalGamesTheme {
        CreatePost(
            title = "test",
            onTitleChanged = { },
            description = "description",
            onDescriptionChanged = { },
            onSaveClicked = { },
            error = null,
            onSelectPhotoClick = {},
            onPhotoPicked = {},
        )
    }
}

@PreviewLightDark
@PreviewScreenSizes
@Composable
private fun CreatePostErrorPreview() {
    HexagonalGamesTheme {
        CreatePost(
            title = "test",
            onTitleChanged = { },
            description = "description",
            onDescriptionChanged = { },
            onSaveClicked = { },
            error = FormError.TitleError,
            onSelectPhotoClick = {},
            onPhotoPicked = {},
        )
    }
}