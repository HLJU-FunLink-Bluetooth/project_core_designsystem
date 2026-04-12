package com.hlju.funlinkbluetooth.core.designsystem.token

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import top.yukonga.miuix.kmp.shapes.AbsoluteSmoothUnevenRoundedCornerShape
import top.yukonga.miuix.kmp.utils.getRoundedCorner

@Composable
internal fun rememberDeviceRoundedCorner(): Dp = getRoundedCorner()

@Composable
internal fun rememberTopBarSurfaceShape(): Shape {
    val roundedCorner = rememberDeviceRoundedCorner()
    return remember(roundedCorner) {
        AbsoluteSmoothUnevenRoundedCornerShape(
            bottomRight = roundedCorner,
            bottomLeft = roundedCorner,
        )
    }
}
