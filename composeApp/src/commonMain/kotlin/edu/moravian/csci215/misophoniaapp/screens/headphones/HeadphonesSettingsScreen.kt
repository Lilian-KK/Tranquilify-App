package edu.moravian.csci215.misophoniaapp.screens.headphones

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.moravian.csci215.misophoniaapp.AppColors
import edu.moravian.csci215.misophoniaapp.RoundIconButton
import edu.moravian.csci215.misophoniaapp.fredokaFontFamily
import edu.moravian.csci215.misophoniaapp.headphones_data.Headphones
import edu.moravian.csci215.misophoniaapp.purpleCard
import kotlinx.serialization.Serializable
import misophoniaapp.composeapp.generated.resources.Res
import misophoniaapp.composeapp.generated.resources.cancel
import misophoniaapp.composeapp.generated.resources.delete
import misophoniaapp.composeapp.generated.resources.delete_headphones
import misophoniaapp.composeapp.generated.resources.delete_headphones_confirm
import misophoniaapp.composeapp.generated.resources.go_back
import misophoniaapp.composeapp.generated.resources.headphone_settings
import misophoniaapp.composeapp.generated.resources.hp_battery_label
import misophoniaapp.composeapp.generated.resources.hp_last_phone_label
import misophoniaapp.composeapp.generated.resources.hp_last_server_label
import misophoniaapp.composeapp.generated.resources.hp_mac_label
import misophoniaapp.composeapp.generated.resources.hp_name_label
import misophoniaapp.composeapp.generated.resources.hp_uuid_label
import misophoniaapp.composeapp.generated.resources.hp_version_label
import misophoniaapp.composeapp.generated.resources.ic_arrow_left_thin
import misophoniaapp.composeapp.generated.resources.ic_trash
import misophoniaapp.composeapp.generated.resources.ic_wifi
import misophoniaapp.composeapp.generated.resources.unknown_value
import misophoniaapp.composeapp.generated.resources.wifi_settings_button
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Serializable
data class HeadphonesSettings(
    val headphonesId: String,
)

/**
 * Details for one pair of headphones. Only the name can be edited; the
 * rest comes from the headphones themselves.
 */
@Composable
fun HeadphonesSettingsScreen(
    headphones: Headphones?,
    onRename: (String) -> Unit,
    onWifiSettings: () -> Unit,
    onDelete: () -> Unit,
    goBack: () -> Unit,
) {
    // Just deleted (this screen is animating out), so there's nothing to show.
    if (headphones == null) return

    val fredoka = fredokaFontFamily()
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(75.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.headphone_settings),
            fontFamily = fredoka,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            lineHeight = 36.sp,
            color = AppColors.Title,
            modifier = Modifier.fillMaxWidth(),
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier.purpleCard(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                InfoRow(label = Res.string.hp_name_label, fontFamily = fredoka) {
                    EditableValue(
                        value = headphones.name,
                        onValueChange = onRename,
                        fontFamily = fredoka,
                    )
                }
                ReadOnlyRow(Res.string.hp_uuid_label, headphones.uuid, fredoka)
                ReadOnlyRow(Res.string.hp_version_label, headphones.version, fredoka)
                ReadOnlyRow(Res.string.hp_battery_label, headphones.batteryPercent?.let { "$it%" }, fredoka)
                ReadOnlyRow(Res.string.hp_mac_label, headphones.macAddress, fredoka)
                ReadOnlyRow(Res.string.hp_last_server_label, headphones.lastServerConnection, fredoka)
                ReadOnlyRow(Res.string.hp_last_phone_label, headphones.lastPhoneConnection, fredoka)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                RoundIconButton(
                    icon = Res.drawable.ic_wifi,
                    contentDescription = stringResource(Res.string.wifi_settings_button),
                    onClick = onWifiSettings,
                )
                RoundIconButton(
                    icon = Res.drawable.ic_trash,
                    contentDescription = stringResource(Res.string.delete_headphones),
                    onClick = { showDeleteConfirmation = true },
                )
                RoundIconButton(
                    icon = Res.drawable.ic_arrow_left_thin,
                    contentDescription = stringResource(Res.string.go_back),
                    onClick = goBack,
                )
            }
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = {
                Text(
                    text = stringResource(Res.string.delete_headphones),
                    fontFamily = fredoka,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Title,
                )
            },
            text = {
                Text(
                    text = stringResource(Res.string.delete_headphones_confirm, headphones.name),
                    fontFamily = fredoka,
                    fontSize = 16.sp,
                    color = AppColors.BodyText,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirmation = false
                    onDelete()
                }) {
                    Text(stringResource(Res.string.delete), fontFamily = fredoka, color = AppColors.Button)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text(stringResource(Res.string.cancel), fontFamily = fredoka, color = AppColors.Button)
                }
            },
            containerColor = AppColors.Background,
        )
    }
}

/** One "Label:   value" line of the details card. */
@Composable
private fun InfoRow(
    label: StringResource,
    fontFamily: FontFamily,
    value: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 32.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(label),
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp,
            color = Color.Black,
            modifier = Modifier.weight(1f),
        )
        Column(modifier = Modifier.width(120.dp)) {
            value()
        }
    }
}

@Composable
private fun ReadOnlyRow(
    label: StringResource,
    value: String?,
    fontFamily: FontFamily,
) {
    InfoRow(label = label, fontFamily = fontFamily) {
        Text(
            text = value ?: stringResource(Res.string.unknown_value),
            style = valueTextStyle(fontFamily),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/** The name value: same text style, with an underline showing it can be edited. */
@Composable
private fun EditableValue(
    value: String,
    onValueChange: (String) -> Unit,
    fontFamily: FontFamily,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = valueTextStyle(fontFamily),
        cursorBrush = SolidColor(AppColors.Button),
        modifier = Modifier.fillMaxWidth(),
    )
    HorizontalDivider(
        modifier = Modifier.padding(top = 2.dp),
        thickness = 1.dp,
        color = AppColors.Button,
    )
}

private fun valueTextStyle(fontFamily: FontFamily) = TextStyle(
    fontFamily = fontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.25.sp,
    color = Color.Black,
    textAlign = TextAlign.Center,
)
