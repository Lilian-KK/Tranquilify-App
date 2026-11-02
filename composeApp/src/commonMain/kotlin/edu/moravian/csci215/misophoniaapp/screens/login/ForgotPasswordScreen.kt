package edu.moravian.csci215.misophoniaapp.screens.login

import BadRequestException
import ForbiddenException
import TooManyRequestsException
import UnauthorizedException
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import edu.moravian.csci215.misophoniaapp.BodyText
import edu.moravian.csci215.misophoniaapp.CodeEntryPrompt
import edu.moravian.csci215.misophoniaapp.FilledTextField
import edu.moravian.csci215.misophoniaapp.FormScreenLayout
import edu.moravian.csci215.misophoniaapp.OtpCodeInput
import edu.moravian.csci215.misophoniaapp.PrimaryButton
import edu.moravian.csci215.misophoniaapp.ResendCodeRow
import edu.moravian.csci215.misophoniaapp.ScreenTitle
import edu.moravian.csci215.misophoniaapp.fredokaFontFamily
import edu.moravian.csci215.misophoniaapp.server_data.LoginResponse
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import misophoniaapp.composeapp.generated.resources.Res
import misophoniaapp.composeapp.generated.resources.create_new_password
import misophoniaapp.composeapp.generated.resources.forgot_password_title
import misophoniaapp.composeapp.generated.resources.new_code_sent
import misophoniaapp.composeapp.generated.resources.new_password
import misophoniaapp.composeapp.generated.resources.password_requirements
import misophoniaapp.composeapp.generated.resources.send_code
import misophoniaapp.composeapp.generated.resources.username
import misophoniaapp.composeapp.generated.resources.verify
import misophoniaapp.composeapp.generated.resources.verify_for_pw_creation
import org.jetbrains.compose.resources.stringResource

@Serializable
data class ForgotPassword(
    val username: String
)

/** The three steps of the forgot-password flow, matching the Figma screens. */
private enum class ForgotPasswordStep { SEND_CODE, ENTER_CODE, NEW_PASSWORD }

@Composable
fun ForgotPasswordScreen(
    username: String,
    goBack: () -> Unit,
    resetPassword: suspend (String, String, String) -> Unit,
    sendCode: suspend (String) -> Unit,
    showSnackbar: (String) -> Unit,
    storeTokens: suspend (String, String) -> Unit,
    toHub: () -> Unit = {},
    onLogin: suspend (String, String, String) -> LoginResponse
) {
    var step by remember { mutableStateOf(ForgotPasswordStep.SEND_CODE) }
    var code by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val fredoka = fredokaFontFamily()

    FormScreenLayout(
        onBack = {
            when (step) {
                ForgotPasswordStep.SEND_CODE -> goBack()
                ForgotPasswordStep.ENTER_CODE -> step = ForgotPasswordStep.SEND_CODE
                ForgotPasswordStep.NEW_PASSWORD -> step = ForgotPasswordStep.ENTER_CODE
            }
        },
        bottomAction = {
            when (step) {
                ForgotPasswordStep.SEND_CODE -> PrimaryButton(
                    text = stringResource(Res.string.send_code),
                    fontFamily = fredoka,
                    onClick = {
                        coroutineScope.launch {
                            try {
                                sendCode(username)
                                step = ForgotPasswordStep.ENTER_CODE
                            } catch (exception: BadRequestException) { //400
                                showSnackbar(exception.message ?: "BadRequestException")
                            } catch (exception: ForbiddenException) { //403--code could not be delivered
                                showSnackbar(exception.message ?: "ForbiddenException")
                            } catch (exception: TooManyRequestsException) { //429--exceeded rate limit
                                showSnackbar(exception.message ?: "Too Many Requests Exception")
                            }
                        }
                    }
                )

                ForgotPasswordStep.ENTER_CODE -> PrimaryButton(
                    text = stringResource(Res.string.verify),
                    fontFamily = fredoka,
                    // the code is checked by the server when the password is reset
                    enabled = code.length == 6,
                    onClick = { step = ForgotPasswordStep.NEW_PASSWORD },
                )

                ForgotPasswordStep.NEW_PASSWORD -> PrimaryButton(
                    text = stringResource(Res.string.create_new_password),
                    fontFamily = fredoka,
                    onClick = {
                        coroutineScope.launch {
                            try {
                                resetPassword(username, code, newPassword)
                                showSnackbar("Password reset!")
                                val tokenResponse = onLogin(username, "password", newPassword)
                                println(tokenResponse)
                                storeTokens(tokenResponse.access_token, tokenResponse.refresh_token)
                                toHub()
                            } catch (exception: UnauthorizedException) { //401--invalid username
                                showSnackbar(exception.message ?: "Unauthorized Exception")
                            } catch (exception: BadRequestException) { //400
                                showSnackbar(exception.message ?: "Bad Request Exception")
                            } catch (exception: ForbiddenException) { //403--invalid text code
                                showSnackbar(exception.message ?: "Forbidden Exception")
                            } catch (exception: TooManyRequestsException) { //429--exceeded rate limitations
                                showSnackbar(exception.message ?: "Too Many Requests Exception")
                            }
                        }
                    },
                )
            }
        },
    ) {
        if (step == ForgotPasswordStep.NEW_PASSWORD) {
            ScreenTitle(
                text = stringResource(Res.string.create_new_password),
                fontFamily = fredoka,
            )

            FilledTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = stringResource(Res.string.new_password),
                description = stringResource(Res.string.password_requirements),
                keyboardType = KeyboardType.Password,
                isPassword = true,
                fontFamily = fredoka,
            )
        } else {
            ScreenTitle(
                text = stringResource(Res.string.forgot_password_title),
                fontFamily = fredoka,
            )

            BodyText(
                text = stringResource(Res.string.verify_for_pw_creation),
                fontFamily = fredoka,
            )

            FilledTextField(
                value = username,
                label = stringResource(Res.string.username),
                keyboardType = KeyboardType.Text,
                fontFamily = fredoka,
            )

            if (step == ForgotPasswordStep.ENTER_CODE) {
                CodeEntryPrompt(fontFamily = fredoka)

                OtpCodeInput(
                    fontFamily = fredoka,
                    onCodeChange = { code = it },
                )

                val newCodeSentMessage = stringResource(Res.string.new_code_sent)
                ResendCodeRow(
                    fontFamily = fredoka,
                    onResendClick = {
                        coroutineScope.launch {
                            try {
                                sendCode(username)
                                showSnackbar(newCodeSentMessage)
                            } catch (exception: BadRequestException) {
                                showSnackbar(exception.message ?: "BadRequest Exception")
                            } catch (exception: ForbiddenException) {
                                showSnackbar(exception.message ?: "Forbidden Exception")
                            } catch (exception: TooManyRequestsException) { //429--exceeded rate limitations
                                showSnackbar(exception.message ?: "Too Many Requests Exception")
                            }
                        }
                    },
                )
            }
        }
    }
}
