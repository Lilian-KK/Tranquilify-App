package edu.moravian.csci215.misophoniaapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import misophoniaapp.composeapp.generated.resources.Res
import misophoniaapp.composeapp.generated.resources.arrow_left
import misophoniaapp.composeapp.generated.resources.cancel_symbol
import misophoniaapp.composeapp.generated.resources.clear
import misophoniaapp.composeapp.generated.resources.closed_eye
import misophoniaapp.composeapp.generated.resources.didnt_get_code
import misophoniaapp.composeapp.generated.resources.enter_code
import misophoniaapp.composeapp.generated.resources.eye
import misophoniaapp.composeapp.generated.resources.fredoka_regular
import misophoniaapp.composeapp.generated.resources.fredoka_semibold
import misophoniaapp.composeapp.generated.resources.go_back
import misophoniaapp.composeapp.generated.resources.hide_password
import misophoniaapp.composeapp.generated.resources.send_another_code
import misophoniaapp.composeapp.generated.resources.show_password
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

// -----------------------------------------------------------------------
// COLORS
// Shared across every screen. Update here once and it propagates
// everywhere these are referenced. Values match the Tranquilify Figma file.
// -----------------------------------------------------------------------
object AppColors {
    val Background = Color(0xFFFDFDFB)
    val Button = Color(0xFF9263AA)
    val ButtonStroke = Color(0x59000000)   // #000000 at 35% opacity
    val ButtonText = Color(0xFFF5F5F5)
    val Title = Color(0xFF9263AA)
    val BodyText = Color(0xFF000000)
    val FieldBorder = Color(0xFFD9D9D9)
    val FieldLabel = Color(0xFF1E1E1E)
    val FieldDescription = Color(0xFF757575)
    val FieldPlaceholder = Color(0xFFB3B3B3)
}

// -----------------------------------------------------------------------
// FONT
// Requires fredoka_regular.ttf and fredoka_semibold.ttf under
// commonMain/composeResources/font/
// -----------------------------------------------------------------------
@Composable
fun fredokaFontFamily(): FontFamily = FontFamily(
    Font(Res.font.fredoka_regular, FontWeight.Normal),
    Font(Res.font.fredoka_semibold, FontWeight.SemiBold),
    Font(Res.font.fredoka_semibold, FontWeight.Bold)
)

// -----------------------------------------------------------------------
// REUSABLE COMPONENTS
// -----------------------------------------------------------------------

/**
 * The standard full-width purple button used across Setup and the
 * Login/Registration screens: 4dp corners, 35%-opacity black stroke,
 * 12dp padding, off-white Fredoka label.
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    fontFamily: FontFamily = fredokaFontFamily(),
    fontSize: TextUnit = 20.sp,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 40.dp),
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AppColors.Button),
        border = BorderStroke(1.dp, AppColors.ButtonStroke),
        contentPadding = PaddingValues(12.dp),
    ) {
        Text(
            text = text,
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = fontSize,
            lineHeight = fontSize,
            color = AppColors.ButtonText,
        )
    }
}

/**
 * A round purple button with a 20dp icon, e.g. the back, plus, Wi-Fi and
 * trash buttons. 35dp on the Login/Registration screens, 48dp elsewhere.
 */
@Composable
fun RoundIconButton(
    icon: DrawableResource,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(AppColors.Button, CircleShape)
            .border(1.dp, AppColors.ButtonStroke, CircleShape),
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = contentDescription,
            modifier = Modifier.size(20.dp),
            tint = Color.Unspecified,
        )
    }
}

/**
 * The small round purple back button (35dp, left-arrow icon) shown at the
 * bottom of the Login/Registration screens.
 */
@Composable
fun BackIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    RoundIconButton(
        icon = Res.drawable.arrow_left,
        contentDescription = stringResource(Res.string.go_back),
        onClick = onClick,
        modifier = modifier,
        size = 35.dp,
    )
}

/**
 * The light purple card with a thick purple border used on the home and
 * headphone screens. Apply [Modifier.clickable] before this to make the
 * whole card tappable.
 */
fun Modifier.purpleCard(): Modifier =
    this
        .fillMaxWidth()
        .clip(RoundedCornerShape(4.dp))
        .background(AppColors.Button.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
        .border(5.dp, AppColors.Button, RoundedCornerShape(4.dp))
        .padding(16.dp)

/**
 * Shared layout for the Login/Registration screens: a scrollable content
 * column (16dp padding, 32dp spacing) on the app background, with the back
 * button and an optional primary action pinned to the bottom.
 */
@Composable
fun FormScreenLayout(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    bottomAction: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding(),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp),
            content = content,
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            BackIconButton(onClick = onBack)
            bottomAction?.invoke()
        }
    }
}

/**
 * The standard screen title style: Fredoka, purple, centered.
 * Headline size (28sp) by default; the Welcome screen uses 36sp.
 */
@Composable
fun ScreenTitle(
    text: String,
    modifier: Modifier = Modifier,
    fontFamily: FontFamily = fredokaFontFamily(),
    fontSize: TextUnit = 28.sp,
    lineHeight: TextUnit = 36.sp,
) {
    Text(
        text = text,
        fontFamily = fontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = fontSize,
        lineHeight = lineHeight,
        color = AppColors.Title,
        textAlign = TextAlign.Center,
        modifier = modifier.fillMaxWidth()
    )
}

/**
 * Centered 16sp body text used for short instructions on form screens
 * (e.g. "We need to verify it's you to create a new password.").
 */
@Composable
fun BodyText(
    text: String,
    modifier: Modifier = Modifier,
    fontFamily: FontFamily = fredokaFontFamily(),
) {
    Text(
        text = text,
        fontFamily = fontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
        textAlign = TextAlign.Center,
        color = AppColors.BodyText,
        modifier = modifier.fillMaxWidth(),
    )
}

/**
 * The standard bordered text field used across Login and other form
 * screens: 20sp label above, optional gray description, white 4dp-rounded
 * box with a light-gray border, gray placeholder shown inside when empty,
 * optional password-reveal toggle, a clear ("x") button that appears once
 * there's input, and an optional underlined link below the box.
 */
@Composable
fun FilledTextField(
    value: String,
    onValueChange: (String) -> Unit = {},
    label: String,
    modifier: Modifier = Modifier,
    fontFamily: FontFamily = fredokaFontFamily(),
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    description: String? = null,
    linkText: String? = null,
    onLinkClick: () -> Unit = {},
) {
    var isCensored by remember { mutableStateOf(true) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = label,
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 20.sp,
            lineHeight = 28.sp,
            color = AppColors.FieldLabel,
        )

        if (description != null) {
            Text(
                text = description,
                fontFamily = fontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 22.4.sp,
                color = AppColors.FieldDescription,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(4.dp))
                .border(1.dp, AppColors.FieldBorder, shape = RoundedCornerShape(4.dp))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 40.dp)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(
                            text = label,
                            fontFamily = fontFamily,
                            fontSize = 16.sp,
                            lineHeight = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = AppColors.FieldPlaceholder,
                        )
                    }
                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        singleLine = true,
                        textStyle = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 16.sp,
                            lineHeight = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = AppColors.FieldLabel,
                        ),
                        cursorBrush = SolidColor(AppColors.Button),
                        visualTransformation = if (isPassword && isCensored) PasswordVisualTransformation() else VisualTransformation.None,
                        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                if (value.isNotEmpty()) {
                    IconButton(
                        onClick = { onValueChange("") },
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.cancel_symbol),
                            contentDescription = stringResource(Res.string.clear),
                            modifier = Modifier.size(16.dp),
                            tint = AppColors.FieldPlaceholder
                        )
                    }
                }
                if (isPassword) {
                    IconButton(
                        onClick = { isCensored = !isCensored },
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(20.dp)
                    ) {
                        Icon(
                            painter = if (isCensored) painterResource(Res.drawable.eye) else painterResource(Res.drawable.closed_eye),
                            contentDescription = if (isCensored) stringResource(Res.string.show_password) else stringResource(Res.string.hide_password),
                            modifier = Modifier.size(20.dp),
                            tint = AppColors.Button
                        )
                    }
                }
            }
        }

        if (linkText != null) {
            Text(
                text = linkText,
                fontFamily = fontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 22.4.sp,
                color = AppColors.FieldLabel,
                textDecoration = TextDecoration.Underline,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable(onClick = onLinkClick),
            )
        }
    }
}

/**
 * The "Enter the 6-digit code sent to your phone number" style prompt
 * used above the OTP boxes on code-login/verification screens.
 */
@Composable
fun CodeEntryPrompt(
    modifier: Modifier = Modifier,
    fontFamily: FontFamily = fredokaFontFamily(),
) {
    BodyText(
        text = stringResource(Res.string.enter_code),
        fontFamily = fontFamily,
        modifier = modifier,
    )
}

/**
 * A single digit box within the OTP row. Always used as part of
 * [OtpCodeInput], never on its own.
 */
@Composable
fun CodeChip(
    value: String,
    focusRequester: FocusRequester,
    onValueChange: (String) -> Unit,
    fontFamily: FontFamily,
) {
    Box(
        modifier = Modifier
            .width(42.dp)
            .height(56.dp)
            .border(
                width = 1.dp,
                color = AppColors.Button,
                shape = RoundedCornerShape(16.dp),
            )
            .background(Color.Transparent, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center,
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            cursorBrush = SolidColor(AppColors.Button),
            textStyle = TextStyle(
                fontFamily = fontFamily,
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 0.1.sp,
                textAlign = TextAlign.Center,
                color = AppColors.Button,
            ),
            decorationBox = { innerTextField ->
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                    innerTextField()
                }
            },
        )
    }
}

/**
 * The row of digit boxes used for one-time-code entry. Manages its own
 * per-digit state and focus-advance behavior internally; call sites just
 * read the assembled code via [onCodeChange].
 */
@Composable
fun OtpCodeInput(
    modifier: Modifier = Modifier,
    length: Int = 6,
    fontFamily: FontFamily = fredokaFontFamily(),
    onCodeChange: (String) -> Unit = {},
) {
    var code by remember { mutableStateOf(List(length) { "" }) }
    val focusRequesters = remember { List(length) { FocusRequester() } }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.5.dp),
        horizontalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterHorizontally),
    ) {
        repeat(length) { index ->
            CodeChip(
                value = code[index],
                focusRequester = focusRequesters[index],
                fontFamily = fontFamily,
                onValueChange = { newValue ->
                    if (newValue.length <= 1 && (newValue.isEmpty() || newValue.all { it.isDigit() })) {
                        code = code.toMutableList().apply { this[index] = newValue }
                        onCodeChange(code.joinToString(""))
                        if (newValue.isNotEmpty() && index < length - 1) {
                            focusRequesters[index + 1].requestFocus()
                        }
                    }
                },
            )
        }
    }
}

/**
 * The "Didn't get the code? Send another code" line used below the OTP
 * boxes on code-login/verification screens.
 */
@Composable
fun ResendCodeRow(
    onResendClick: () -> Unit,
    modifier: Modifier = Modifier,
    fontFamily: FontFamily = fredokaFontFamily(),
) {
    val prompt = stringResource(Res.string.didnt_get_code)
    val action = stringResource(Res.string.send_another_code)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = buildAnnotatedString {
                append(prompt)
                withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                    append(action)
                }
            },
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp,
            color = AppColors.BodyText,
            textAlign = TextAlign.Center,
            modifier = Modifier.clickable(onClick = onResendClick),
        )
    }
}
