package com.openclassrooms.hexagonal.games.screen.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.imageLoader
import coil.util.DebugLogger
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.domain.model.Comment
import com.openclassrooms.hexagonal.games.domain.model.Post

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel = hiltViewModel(),
    postId: String,
    onBackClick: () -> Unit,
    onFABClick: () -> Unit,
) {

    LaunchedEffect(postId) {
        viewModel.loadPost(postId)
    }
    val uiState = viewModel.detailUiState.collectAsStateWithLifecycle()

    when (uiState.value) {
        DetailScreenState.NoPost -> {
            Text(
                modifier = Modifier.fillMaxSize(),
                text = stringResource(R.string.no_posts),
                style = MaterialTheme.typography.titleLarge
            )
        }

        is DetailScreenState.PostLoaded -> {
            val post = (uiState.value as DetailScreenState.PostLoaded).post
            val comments = (uiState.value as DetailScreenState.PostLoaded).comments
            Scaffold(
                modifier = modifier,
                topBar = {
                    TopAppBar(
                        title = {
                            Text(post.title)
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
                },
                floatingActionButtonPosition = FabPosition.End,
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            onFABClick()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = stringResource(id = R.string.description_button_add)
                        )
                    }
                }
            ) { contentPadding ->
                Details(
                    modifier = Modifier.padding(contentPadding),
                    post = post,
                    comments = comments,
                )
            }
        }

        else -> {}
    }

}

@Composable
private fun Details(
    modifier: Modifier = Modifier,
    post: Post,
    comments: List<Comment>,
) {
    Column(
        modifier = modifier
    ) {
        PostContent(
            post = post,
        )
        CommentList(
            comments = comments,
        )
    }
}

@Composable
private fun PostContent(
    modifier: Modifier = Modifier,
    post: Post,
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
        ) {
            Text(
                text = stringResource(
                    id = R.string.by,
                    post.author?.username ?: "",
                ),
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = post.title,
                style = MaterialTheme.typography.titleLarge
            )
            if (post.description.isNotEmpty()) {
                Text(
                    text = post.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            if (post.photoUrl.isNotEmpty()) {
                AsyncImage(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                        .aspectRatio(ratio = 16 / 9f),
                    model = post.photoUrl,
                    imageLoader = LocalContext.current.imageLoader.newBuilder()
                        .logger(DebugLogger())
                        .build(),
                    placeholder = ColorPainter(Color.DarkGray),
                    contentDescription = "image",
                    contentScale = ContentScale.Crop,
                )
            }
        }
    }
}

@Composable
private fun CommentList(
    modifier: Modifier = Modifier,
    comments: List<Comment>,
) {
    LazyColumn(
        modifier = modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(comments) { comment ->
            CommentCell(
                comment = comment
            )
        }
    }
}

@Composable
private fun CommentCell(
    comment: Comment,
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
        ) {
            Text(
                text = stringResource(
                    id = R.string.by,
                    comment.author?.username ?: "",
                ),
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}