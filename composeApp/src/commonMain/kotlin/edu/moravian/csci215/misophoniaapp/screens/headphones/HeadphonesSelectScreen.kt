package edu.moravian.csci215.misophoniaapp.screens.headphones

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.moravian.csci215.misophoniaapp.AppColors
import edu.moravian.csci215.misophoniaapp.RoundIconButton
import edu.moravian.csci215.misophoniaapp.fredokaFontFamily
import edu.moravian.csci215.misophoniaapp.headphones_data.Headphones
import edu.moravian.csci215.misophoniaapp.purpleCard
import kotlinx.serialization.Serializable
import misophoniaapp.composeapp.generated.resources.Res
import misophoniaapp.composeapp.generated.resources.add_headphones
import misophoniaapp.composeapp.generated.resources.ic_headphones_small
import misophoniaapp.composeapp.generated.resources.ic_plus
import misophoniaapp.composeapp.generated.resources.select_headphones
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Serializable
data object HeadphonesSelect

/**
 * Lists the user's headphones. Tapping one opens its settings; the plus
 * button adds new headphones.
 */
@Composable
fun HeadphonesSelectScreen(
    headphones: List<Headphones>,
    onHeadphonesSelected: (Headphones) -> Unit,
    onAddHeadphones: () -> Unit,
) {
    val fredoka = fredokaFontFamily()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(75.dp),
    ) {
        Text(
            text = stringResource(Res.string.select_headphones),
            fontFamily = fredoka,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            lineHeight = 36.sp,
            color = AppColors.Title,
            modifier = Modifier.fillMaxWidth(),
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(32.dp),
            horizontalAlignment = Alignment.End,
        ) {
            headphones.forEach { item ->
                HeadphonesButton(
                    headphones = item,
                    onClick = { onHeadphonesSelected(item) },
                    fontFamily = fredoka,
                )
            }

            // TODO: the + button should eventually go through Bluetooth to pair real
            //  headphones (e.g. via HeadphonesNearby) instead of just adding placeholder ones.
            RoundIconButton(
                icon = Res.drawable.ic_plus,
                contentDescription = stringResource(Res.string.add_headphones),
                onClick = onAddHeadphones,
            )
        }
    }
}

@Composable
private fun HeadphonesButton(
    headphones: Headphones,
    onClick: () -> Unit,
    fontFamily: FontFamily,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .clickable(onClick = onClick)
            .purpleCard(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = headphones.name,
            fontFamily = fontFamily,
            fontSize = 16.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(166.dp),
        )
        Image(
            painter = painterResource(Res.drawable.ic_headphones_small),
            contentDescription = null,
            modifier = Modifier.size(32.dp),
        )
    }
}
