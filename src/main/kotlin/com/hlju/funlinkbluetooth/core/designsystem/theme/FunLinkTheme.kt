package com.hlju.funlinkbluetooth.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.hlju.funlinkbluetooth.core.designsystem.R
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.TextStyles
import top.yukonga.miuix.kmp.theme.ThemeController
import top.yukonga.miuix.kmp.theme.defaultTextStyles

val MiSansFontFamily = FontFamily(
    Font(resId = R.font.misans_regular, weight = FontWeight.Normal),
    Font(resId = R.font.misans_medium, weight = FontWeight.Medium),
    Font(resId = R.font.misans_semibold, weight = FontWeight.SemiBold),
    Font(resId = R.font.misans_bold, weight = FontWeight.Bold),
)

fun miSansTextStyles(base: TextStyles = defaultTextStyles()): TextStyles = base.copy(
    main = base.main.withMiSans(),
    paragraph = base.paragraph.withMiSans(),
    body1 = base.body1.withMiSans(),
    body2 = base.body2.withMiSans(),
    button = base.button.withMiSans(),
    footnote1 = base.footnote1.withMiSans(),
    footnote2 = base.footnote2.withMiSans(),
    headline1 = base.headline1.withMiSans(),
    headline2 = base.headline2.withMiSans(),
    subtitle = base.subtitle.withMiSans(),
    title1 = base.title1.withMiSans(),
    title2 = base.title2.withMiSans(),
    title3 = base.title3.withMiSans(),
    title4 = base.title4.withMiSans(),
)

@Composable
fun FunLinkTheme(
    controller: ThemeController,
    smoothRounding: Boolean = true,
    content: @Composable () -> Unit,
) {
    MiuixTheme(
        controller = controller,
        textStyles = miSansTextStyles(),
        smoothRounding = smoothRounding,
        content = content,
    )
}

private fun TextStyle.withMiSans(): TextStyle = copy(fontFamily = MiSansFontFamily)
