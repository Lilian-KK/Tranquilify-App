package edu.moravian.csci215.misophoniaapp.screens.registration

import BadRequestException
import ConflictException
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import edu.moravian.csci215.misophoniaapp.AppColors
import edu.moravian.csci215.misophoniaapp.FilledTextField
import edu.moravian.csci215.misophoniaapp.PrimaryButton
import edu.moravian.csci215.misophoniaapp.ScreenTitle
import edu.moravian.csci215.misophoniaapp.fredokaFontFamily
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import misophoniaapp.composeapp.generated.resources.Res
import misophoniaapp.composeapp.generated.resources.back_arrow
import misophoniaapp.composeapp.generated.resources.create_account
import misophoniaapp.composeapp.generated.resources.go_back
import misophoniaapp.composeapp.generated.resources.password
import misophoniaapp.composeapp.generated.resources.phone_number
import misophoniaapp.composeapp.generated.resources.username
import org.jetbrains.compose.resources.painterResource
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

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(AppColors.Background),
    ) {
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            ScreenTitle(
                text = stringResource(Res.string.create_account),
                modifier = Modifier.fillMaxWidth(),
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

        Column(modifier = Modifier.padding(24.dp)) {
            IconButton(
                onClick = goBack,
                modifier =
                    Modifier
                        .size(48.dp)
                        .background(AppColors.Button, CircleShape),
            ) {
                Icon(
                    painter = painterResource(Res.drawable.back_arrow),
                    modifier = Modifier.size(24.dp),
                    contentDescription = stringResource(Res.string.go_back),
                    tint = Color.White,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            PrimaryButton(
                text = "Verify Phone Number",
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
        }
    }
}

fun String.formatPhone(): String {
    if (!this.startsWith("+1")) {
        return "+1$this"
    } else {
        return this
    }
}