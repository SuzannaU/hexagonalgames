package com.openclassrooms.hexagonal.games.screen.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.openclassrooms.hexagonal.games.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    modifier: Modifier = Modifier,
    viewModel: AccountViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    accountDeleted: () -> Unit,
    deletionError: () -> Unit,
    afterSignOut: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.my_account))
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
    ) { contentPadding ->
        Account(
            modifier = Modifier.padding(contentPadding),
            onSignOutClicked = {
                viewModel.signOut()
                afterSignOut()
            },
            onDeleteAccountClicked = {
                if (viewModel.deleteAccount()) {
                    accountDeleted()
                } else {
                    deletionError()
                }
            },
        )
    }
}

@Composable
private fun Account(
    modifier: Modifier = Modifier,
    onSignOutClicked: () -> Unit,
    onDeleteAccountClicked: () -> Unit,

    ) {

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = onSignOutClicked
        ) {
            Text(text = stringResource(R.string.sign_out))
        }
        Button(
            onClick = onDeleteAccountClicked
        ) {
            Text(text = stringResource(R.string.delete_account))
        }
    }
}