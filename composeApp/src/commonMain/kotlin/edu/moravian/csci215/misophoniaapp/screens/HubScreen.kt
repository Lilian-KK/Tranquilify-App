package edu.moravian.csci215.misophoniaapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.moravian.csci215.misophoniaapp.AppColors
import edu.moravian.csci215.misophoniaapp.BodyText
import edu.moravian.csci215.misophoniaapp.PrimaryButton
import edu.moravian.csci215.misophoniaapp.ScreenTitle
import edu.moravian.csci215.misophoniaapp.currentTimeMillis
import edu.moravian.csci215.misophoniaapp.fredokaFontFamily
import edu.moravian.csci215.misophoniaapp.purpleCard
import edu.moravian.csci215.misophoniaapp.survey_data.SurveyType
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import misophoniaapp.composeapp.generated.resources.Res
import misophoniaapp.composeapp.generated.resources.amisosr_survey
import misophoniaapp.composeapp.generated.resources.battery_level
import misophoniaapp.composeapp.generated.resources.connect_headphones
import misophoniaapp.composeapp.generated.resources.connect_headphones_message
import misophoniaapp.composeapp.generated.resources.duke_survey
import misophoniaapp.composeapp.generated.resources.good_afternoon
import misophoniaapp.composeapp.generated.resources.good_evening
import misophoniaapp.composeapp.generated.resources.good_morning
import misophoniaapp.composeapp.generated.resources.headphone_battery
import misophoniaapp.composeapp.generated.resources.headphones_large
import misophoniaapp.composeapp.generated.resources.ic_battery
import misophoniaapp.composeapp.generated.resources.pair_headphones
import misophoniaapp.composeapp.generated.resources.trigger_log
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Instant

@Serializable
data object Hub

/**
 * The home screen. Shows a "Connect Your Headphones" card until headphones
 * have been added, and a greeting with the headphone battery level
 * afterwards ([headphoneBatteryPercent] is null while it isn't known yet).
 */
@Composable
fun HubScreen(
    hasHeadphones: Boolean,
    headphoneBatteryPercent: Int?,
    onPairHeadphones: () -> Unit,
    onSurveySelected: (SurveyType) -> Unit,
) {
    val fredoka = fredokaFontFamily()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (!hasHeadphones) {
            Spacer(modifier = Modifier.height(107.dp))

            NewHeadphonesCard(onPairHeadphones = onPairHeadphones, fontFamily = fredoka)
        } else {
            Text(
                text = greeting(),
                fontFamily = fredoka,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                lineHeight = 36.sp,
                color = AppColors.Title,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(75.dp))

            HeadphoneBatteryCard(percent = headphoneBatteryPercent, fontFamily = fredoka)
        }

        // Not in the Figma home screen yet, but this is currently the only
        // place surveys can be started from, so keep them reachable.
        Column(
            modifier = Modifier.padding(top = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            PrimaryButton(
                text = stringResource(Res.string.amisosr_survey),
                onClick = { onSurveySelected(SurveyType.AMISOSR_SURVEY) },
                fontFamily = fredoka,
            )
            PrimaryButton(
                text = stringResource(Res.string.duke_survey),
                onClick = { onSurveySelected(SurveyType.DUKE_SURVEY) },
                fontFamily = fredoka,
            )
            PrimaryButton(
                text = stringResource(Res.string.trigger_log),
                onClick = { onSurveySelected(SurveyType.TRIGGER_LOG_SURVEY) },
                fontFamily = fredoka,
            )
        }
    }
}

@Composable
private fun NewHeadphonesCard(
    onPairHeadphones: () -> Unit,
    fontFamily: FontFamily,
) {
    Column(
        modifier = Modifier.purpleCard(),
        verticalArrangement = Arrangement.spacedBy(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(Res.drawable.headphones_large),
            contentDescription = null,
            modifier = Modifier.size(200.dp),
        )

        ScreenTitle(
            text = stringResource(Res.string.connect_headphones),
            fontFamily = fontFamily,
        )

        BodyText(
            text = stringResource(Res.string.connect_headphones_message),
            fontFamily = fontFamily,
        )

        PrimaryButton(
            text = stringResource(Res.string.pair_headphones),
            onClick = onPairHeadphones,
            fontFamily = fontFamily,
        )
    }
}

@Composable
private fun HeadphoneBatteryCard(
    percent: Int?,
    fontFamily: FontFamily,
) {
    Row(
        modifier = Modifier.purpleCard(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(Res.string.headphone_battery),
            fontFamily = fontFamily,
            fontSize = 16.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(166.dp),
        )
        Text(
            text = if (percent == null) "--%" else "$percent%",
            fontFamily = fontFamily,
            fontSize = 16.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp,
            color = Color.Black,
            textAlign = TextAlign.End,
            modifier = Modifier.width(96.dp),
        )
        Image(
            painter = painterResource(Res.drawable.ic_battery),
            contentDescription = stringResource(Res.string.battery_level),
            modifier = Modifier.size(width = 48.dp, height = 32.dp),
        )
    }
}

/** "Good morning/afternoon/evening friend," based on the local time. */
@Composable
private fun greeting(): String {
    val hour = Instant.fromEpochMilliseconds(currentTimeMillis())
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .hour
    return when (hour) {
        in 5..11 -> stringResource(Res.string.good_morning)
        in 12..16 -> stringResource(Res.string.good_afternoon)
        else -> stringResource(Res.string.good_evening)
    }
}
