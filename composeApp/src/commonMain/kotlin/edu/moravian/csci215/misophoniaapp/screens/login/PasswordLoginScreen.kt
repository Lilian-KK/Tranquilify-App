package edu.moravian.csci215.misophoniaapp.screens.login

import BadRequestException
import TooManyRequestsException
import UnauthorizedException
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.input.KeyboardType
import edu.moravian.csci215.misophoniaapp.FilledTextField
import edu.moravian.csci215.misophoniaapp.FormScreenLayout
import edu.moravian.csci215.misophoniaapp.PrimaryButton
import edu.moravian.csci215.misophoniaapp.ScreenTitle
import edu.moravian.csci215.misophoniaapp.fredokaFontFamily
import edu.moravian.csci215.misophoniaapp.server_data.LoginResponse
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import misophoniaapp.composeapp.generated.resources.Res
import misophoniaapp.composeapp.generated.resources.forgot_password
import misophoniaapp.composeapp.generated.resources.login
import misophoniaapp.composeapp.generated.resources.password
import misophoniaapp.composeapp.generated.resources.username
import org.jetbrains.compose.resources.stringResource

@Serializable
data class PasswordLogin(
    val username: String,
)

@Composable
fun PasswordLoginScreen(
    username: String,
    goBack: () -> Unit,
    storeTokens: suspend (String, String) -> Unit,
    showSnackbar: (String) -> Unit,
    toHub: () -> Unit,
    toForgotPassword: (String) -> Unit,
    onLogin: suspend (String, String, String) -> LoginResponse,
) {
    val (password, setPassword) = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val fredoka = fredokaFontFamily()

    FormScreenLayout(
        onBack = goBack,
        bottomAction = {
            PrimaryButton(
                text = stringResource(Res.string.login),
                fontFamily = fredoka,
                onClick = {
                    coroutineScope.launch {
                        try {
                            val tokenResponse = onLogin(username, "password", password)
                            println(tokenResponse)
                            storeTokens(tokenResponse.access_token, tokenResponse.refresh_token)
                            toHub()
                        } catch (exception: UnauthorizedException) {
                            // 401
                            showSnackbar(exception.message ?: "UnauthorizedException")
                        } catch (exception: BadRequestException) {
                            // 400
                            showSnackbar(exception.message ?: "BadRequestException")
                        } catch (exception: TooManyRequestsException) {
                            // 429--exceeded rate limitations
                            showSnackbar(exception.message ?: "Too Many Requests Exception")
                        }
                    }
                },
            )
        },
    ) {
        ScreenTitle(
            text = stringResource(Res.string.login),
            fontFamily = fredoka,
        )

        FilledTextField(
            value = username,
            label = stringResource(Res.string.username),
            keyboardType = KeyboardType.Text,
            fontFamily = fredoka,
        )

        FilledTextField(
            value = password,
            onValueChange = setPassword,
            label = stringResource(Res.string.password),
            keyboardType = KeyboardType.Password,
            isPassword = true,
            fontFamily = fredoka,
            linkText = stringResource(Res.string.forgot_password),
            onLinkClick = { toForgotPassword(username) },
        )
    }
}
