package edu.moravian.csci215.misophoniaapp.screens.registration

import BadRequestException
import ConflictException
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import edu.moravian.csci215.misophoniaapp.FilledTextField
import edu.moravian.csci215.misophoniaapp.FormScreenLayout
import edu.moravian.csci215.misophoniaapp.PrimaryButton
import edu.moravian.csci215.misophoniaapp.ScreenTitle
import edu.moravian.csci215.misophoniaapp.fredokaFontFamily
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import misophoniaapp.composeapp.generated.resources.Res
import misophoniaapp.composeapp.generated.resources.create_account_title
import misophoniaapp.composeapp.generated.resources.password
import misophoniaapp.composeapp.generated.resources.phone_number
import misophoniaapp.composeapp.generated.resources.username
import misophoniaapp.composeapp.generated.resources.verify_phone_number
import org.jetbrains.compose.resources.stringResource

@Serializable
data object CreateAccount

@Composable
fun CreateAccountScreen(
    showSnackbar: (String) -> Unit,
    goBack: () -> Unit,
    sendCode: suspend (String) -> Unit,
    toVerifyPhoneNumber: (String, String, String) -> Unit,
) {
    var username by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val fredoka = fredokaFontFamily()

    FormScreenLayout(
        onBack = goBack,
        bottomAction = {
            PrimaryButton(
                text = stringResource(Res.string.verify_phone_number),
                fontFamily = fredoka,
                enabled = phoneNumber.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty(),
                onClick = {
                    coroutineScope.launch {
                        try {
                            //block off this section
                            //val regexPhoneNumber = Regex("^\+[1-9]\d{1,14}$")
                            //val phoneNumberIsValid = regexPhoneNumber.matches(phoneNumber)
                            sendCode(phoneNumber.formatPhone())
//                            if (password.length < 8) {
//                                showSnackbar("Password should be at least 8 characters.")
//                            } else if (username.length < 3 || username.length > 32) {
//                                showSnackbar("Username should be between 3 and 32 characters.")
//                            } else if (phoneNumberIsValid) {
//                                showSnackbar("PhoneNumber")
//                            }
                            toVerifyPhoneNumber(phoneNumber.formatPhone(), username, password)
                        } catch (exception: BadRequestException) {
                            showSnackbar(exception.message ?: "Bad Request Exception")
                            println("exception: " + exception)
                            println("message: " + exception.message)
                            println("response: " + exception.response)
                            println("status: " + exception.status)
                        } catch (exception: ConflictException) {
                            // 409--that username is already taken
                            showSnackbar(exception.message ?: "Conflict Exception")
                        }
                    }
                },
            )
        },
    ) {
        ScreenTitle(
            text = stringResource(Res.string.create_account_title),
            fontFamily = fredoka,
        )

        FilledTextField(
            value = username,
            onValueChange = { username = it },
            label = stringResource(Res.string.username),
            fontFamily = fredoka,
        )

        FilledTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = stringResource(Res.string.phone_number),
            keyboardType = KeyboardType.Phone,
            fontFamily = fredoka,
        )

        FilledTextField(
            value = password,
            onValueChange = { password = it },
            label = stringResource(Res.string.password),
            keyboardType = KeyboardType.Password,
            isPassword = true,
            fontFamily = fredoka,
        )
    }
}

fun String.formatPhone(): String {
    if (!this.startsWith("+1")) {
        return "+1$this"
    } else {
        return this
    }
}
