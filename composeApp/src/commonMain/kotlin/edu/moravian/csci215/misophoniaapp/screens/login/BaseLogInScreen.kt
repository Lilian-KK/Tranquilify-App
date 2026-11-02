package edu.moravian.csci215.misophoniaapp.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import edu.moravian.csci215.misophoniaapp.FilledTextField
import edu.moravian.csci215.misophoniaapp.FormScreenLayout
import edu.moravian.csci215.misophoniaapp.PrimaryButton
import edu.moravian.csci215.misophoniaapp.ScreenTitle
import edu.moravian.csci215.misophoniaapp.fredokaFontFamily
import kotlinx.serialization.Serializable
import misophoniaapp.composeapp.generated.resources.Res
import misophoniaapp.composeapp.generated.resources.log_in_with_code
import misophoniaapp.composeapp.generated.resources.log_in_with_password
import misophoniaapp.composeapp.generated.resources.login
import misophoniaapp.composeapp.generated.resources.username
import org.jetbrains.compose.resources.stringResource

@Serializable
data object BaseLogin

@Composable
fun BaseLoginScreen(
    showSnackbar: (String) -> Unit,
    toCodeLogin: (String) -> Unit,
    toPasswordLogin: (String) -> Unit,
    goBack: () -> Unit,
) {
    val (username, setUsername) = remember { mutableStateOf("") }
    val fredoka = fredokaFontFamily()

    FormScreenLayout(onBack = goBack) {
        ScreenTitle(
            text = stringResource(Res.string.login),
            fontFamily = fredoka,
        )

        FilledTextField(
            value = username,
            onValueChange = setUsername,
            label = stringResource(Res.string.username),
            fontFamily = fredoka,
        )

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            PrimaryButton(
                text = stringResource(Res.string.log_in_with_code),
                onClick = {
                    if (username.isEmpty()) {
                        showSnackbar("Field left blank")
                    } else {
                        toCodeLogin(username)
                    }
                },
                fontFamily = fredoka,
            )

            PrimaryButton(
                text = stringResource(Res.string.log_in_with_password),
                onClick = {
                    if (username.isEmpty()) {
                        showSnackbar("Field left blank")
                    } else {
                        toPasswordLogin(username)
                    }
                },
                fontFamily = fredoka,
            )
        }
    }
}
