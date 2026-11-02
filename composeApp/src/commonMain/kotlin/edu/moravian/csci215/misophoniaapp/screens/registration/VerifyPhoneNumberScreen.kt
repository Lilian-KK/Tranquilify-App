package edu.moravian.csci215.misophoniaapp.screens.registration

import BadRequestException
import ConflictException
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import edu.moravian.csci215.misophoniaapp.CodeEntryPrompt
import edu.moravian.csci215.misophoniaapp.FormScreenLayout
import edu.moravian.csci215.misophoniaapp.OtpCodeInput
import edu.moravian.csci215.misophoniaapp.PrimaryButton
import edu.moravian.csci215.misophoniaapp.ResendCodeRow
import edu.moravian.csci215.misophoniaapp.ScreenTitle
import edu.moravian.csci215.misophoniaapp.fredokaFontFamily
import edu.moravian.csci215.misophoniaapp.server_data.UserResponse
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import misophoniaapp.composeapp.generated.resources.Res
import misophoniaapp.composeapp.generated.resources.create_account_title
import misophoniaapp.composeapp.generated.resources.verify_phone_number
import org.jetbrains.compose.resources.stringResource

@Serializable
data class VerifyPhoneNumber(
    val phoneNumber: String,
    val username: String,
    val password: String
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun VerifyPhoneNumberScreen(
    phoneNumber: String,
    username: String,
    showSnackbar: (String) -> Unit,
    goBack: () -> Unit,
    toHub: () -> Unit,
    sendCode: suspend (String) -> Unit,
    createAccount: suspend (String) -> UserResponse,
) {
    val coroutineScope = rememberCoroutineScope()
    var code by remember { mutableStateOf("") }
    val fredoka = fredokaFontFamily()

    FormScreenLayout(
        onBack = goBack,
        bottomAction = {
            PrimaryButton(
                text = stringResource(Res.string.create_account_title),
                fontFamily = fredoka,
                onClick = {
                    coroutineScope.launch {
                        try {
                            println("trying to send code")
                            println(code)
                            createAccount(code)
                            toHub()
                            println("code sent")
                        } catch (exception: BadRequestException) { //400
                            showSnackbar(exception.message ?: "Bad Request Exception")
                        } catch (exception: ConflictException) { //409
                            showSnackbar(exception.message ?: "Conflict Exception")
                        }
                    }
                },
            )
        },
    ) {
        ScreenTitle(
            text = stringResource(Res.string.verify_phone_number),
            fontFamily = fredoka,
        )

        CodeEntryPrompt(fontFamily = fredoka)

        OtpCodeInput(
            fontFamily = fredoka,
            onCodeChange = { code = it },
        )

        ResendCodeRow(
            fontFamily = fredoka,
            onResendClick = {
                coroutineScope.launch {
                    try {
                        sendCode(phoneNumber)
                        showSnackbar("A new code was sent to your phone number")
                    } catch (exception: BadRequestException) {
                        showSnackbar(exception.message ?: "Bad Request Exception")
                    } catch (exception: ConflictException) {
                        showSnackbar(exception.message ?: "Conflict Exception")
                    }
                }
            },
        )
    }
}
