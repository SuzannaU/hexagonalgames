package com.openclassrooms.hexagonal.games.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.ui.theme.HexagonalGamesTheme

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginClicked : () -> Unit,
) {
    Column (
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        Image(
            painter = painterResource(R.drawable.logo_hexagonal),
            contentDescription = null,
            modifier = Modifier.size(200.dp)
        )
        Button(
            onClick = onLoginClicked,
        ) {
            Text(
                stringResource(R.string.sign_in_with_email)
            )
        }
    }
}

@Composable
fun MailLogin(
    modifier: Modifier = Modifier,
) {

}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    HexagonalGamesTheme {
        LoginScreen(
            modifier = Modifier.fillMaxSize().padding(top = 50.dp),
            onLoginClicked = {}
        )
    }
}