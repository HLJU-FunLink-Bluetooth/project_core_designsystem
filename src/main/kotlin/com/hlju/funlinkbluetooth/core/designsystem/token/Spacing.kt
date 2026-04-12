package com.hlju.funlinkbluetooth.core.designsystem.token

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object Spacing {
    val Zero = 0.dp
    val ExtraSmall = 2.dp
    val Small = 4.dp
    val SmallPlus = 6.dp
    val Medium = 8.dp
    val MediumPlus = 10.dp
    val Large = 12.dp
    val LargePlus = 14.dp
    val ExtraLarge = 16.dp
    val ExtraExtraLarge = 20.dp

    val IconMedium = 18.dp
    val IconExtraLarge = 36.dp

    val DividerInsetStart = 60.dp
    val BottomTabMinWidth = 76.dp
    val DashboardReferenceWidth = 180.dp
    val NavigationRailWidth = 92.dp
    val NavigationRailItemHeight = 68.dp
    val FloatingInset = 12.dp
    val TopBarInset = 12.dp
    val TopBarVerticalInset = 6.dp
    val PageOuterInset = 12.dp
    val PageSectionGap = 12.dp
    val PageCardPadding = 12.dp
    val PageCardPaddingLarge = 16.dp

    val PageBase10 = MediumPlus
    val PageBase12 = Large
    val MediumExtra = Small
    val ExpandedExtra = Medium
}

@Composable
fun defaultPageWindowInsets(): WindowInsets {
    return WindowInsets.systemBars
        .add(WindowInsets.displayCutout)
        .only(WindowInsetsSides.Horizontal)
}

@Composable
fun adaptivePageHorizontalPadding(base: Dp): Dp {
    val density = LocalDensity.current
    val containerWidthPx = LocalWindowInfo.current.containerSize.width
    val expandedThresholdPx = with(density) { 840.dp.roundToPx() }
    val mediumThresholdPx = with(density) { 600.dp.roundToPx() }

    return when {
        containerWidthPx >= expandedThresholdPx -> base + Spacing.ExpandedExtra
        containerWidthPx >= mediumThresholdPx -> base + Spacing.MediumExtra
        else -> base
    }
}
