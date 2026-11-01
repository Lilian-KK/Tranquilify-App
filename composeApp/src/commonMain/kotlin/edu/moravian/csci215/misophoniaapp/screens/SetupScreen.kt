package edu.moravian.csci215.misophoniaapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.juul.kable.Bluetooth
import com.juul.kable.Filter
import com.juul.kable.Peripheral
import com.juul.kable.Scanner
import com.juul.kable.characteristicOf
import com.juul.kable.logs.Logging
import com.juul.kable.logs.SystemLogEngine
import edu.moravian.csci215.misophoniaapp.AppColors
import edu.moravian.csci215.misophoniaapp.PrimaryButton
import edu.moravian.csci215.misophoniaapp.ScreenTitle
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import misophoniaapp.composeapp.generated.resources.Res
import misophoniaapp.composeapp.generated.resources.create_account
import misophoniaapp.composeapp.generated.resources.login
import misophoniaapp.composeapp.generated.resources.logo
import misophoniaapp.composeapp.generated.resources.welcome_message
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Serializable
data object Setup

@Composable
fun SetupScreen(
    onLogin: () -> Unit,
    onCreateAccount: () -> Unit,
) {
//    // add check that ble permissions have been granted
    LaunchedEffect(Unit) {
        scanKable()
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(AppColors.Background)
                .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(64.dp))

        ScreenTitle(text = stringResource(Res.string.welcome_message))

        Spacer(modifier = Modifier.height(48.dp))

        Image(
            painter = painterResource(Res.drawable.logo),
            contentDescription = "Tranquilify logo",
            modifier = Modifier.size(280.dp),
        )

        Spacer(modifier = Modifier.weight(1f))

        PrimaryButton(
            text = stringResource(Res.string.login),
            onClick = onLogin,
        )

        Spacer(modifier = Modifier.height(12.dp))

        PrimaryButton(
            text = stringResource(Res.string.create_account),
            onClick = onCreateAccount,
        )

        Spacer(modifier = Modifier.height(48.dp))
    }
}

//// todo:
//// filter out unnamed devices, show the user a list of all available peripherals to select
//// nimBLE
//// let them connect to one, once they connect to it display information (characteristics?) about it
//// also put this in a separate bluetooth/wifi folder
suspend fun scanKable() {
    println("proof that testingkable is running")
    val advertisement =
        Scanner {
            filters {
                match {
                    println("before services created")
                    services = listOf(Bluetooth.BaseUuid + 0x180F) // battery service
                    println("these are the services" + services)
                }
                match {
                    Filter.Name.Exact("nimBLE")
                }
            }
            logging {
                engine = SystemLogEngine
                level = Logging.Level.Warnings
                format = Logging.Format.Multiline
            }
        }.advertisements.first()
//    val advertisement = Scanner().advertisements.first()
    // note for future nora: it pauses here
    println("the advertisement is: " + advertisement)

    println("before peripheral created")
    val peripheral = Peripheral(advertisement) { }
    peripheral.connect()
    println("peripheral connected")

    println("before batterydata connected")
    val batteryData = peripheral.read(characteristicOf("0x180F", "0x2A19")) // the battery level characteristic
    println("Hey this is the battery data allegedly:" + batteryData)
}
