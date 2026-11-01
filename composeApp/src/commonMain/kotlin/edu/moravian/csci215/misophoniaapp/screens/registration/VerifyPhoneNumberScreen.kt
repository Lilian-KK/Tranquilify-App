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
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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
import androidx.compose.ui.unit.dp
import edu.moravian.csci215.misophoniaapp.AppColors
import edu.moravian.csci215.misophoniaapp.CodeEntryPrompt
import edu.moravian.csci215.misophoniaapp.OtpCodeInput
import edu.moravian.csci215.misophoniaapp.PrimaryButton
import edu.moravian.csci215.misophoniaapp.ResendCodeRow
import edu.moravian.csci215.misophoniaapp.ScreenTitle
import edu.moravian.csci215.misophoniaapp.fredokaFontFamily
import edu.moravian.csci215.misophoniaapp.server_data.UserResponse
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import misophoniaapp.composeapp.generated.resources.Res
import misophoniaapp.composeapp.generated.resources.back_arrow
import misophoniaapp.composeapp.generated.resources.go_back
import org.jetbrains.compose.resources.painterResource
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            ScreenTitle(
                text = "Verify Phone Number",
                modifier = Modifier.fillMaxWidth()
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

        Column(modifier = Modifier.padding(24.dp)) {
            IconButton(
                onClick = goBack,
                modifier = Modifier
                    .size(48.dp)
                    .background(AppColors.Button, CircleShape)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.back_arrow),
                    contentDescription = stringResource(Res.string.go_back),
                    modifier = Modifier.size(24.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            PrimaryButton(
                text = "Create Account",
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
        }
    }
}