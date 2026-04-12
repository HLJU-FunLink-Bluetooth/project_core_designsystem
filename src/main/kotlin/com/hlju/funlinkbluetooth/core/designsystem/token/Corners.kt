package com.hlju.funlinkbluetooth.core.designsystem.token

import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.shapes.SmoothRoundedCornerShape

object Corners {
    val Outer: Dp = 12.dp
    val NestedGapTight: Dp = 2.dp

    val PageShape: Shape = SmoothRoundedCornerShape(Outer)

    fun nestedShape(gap: Dp = NestedGapTight): Shape {
        return SmoothRoundedCornerShape((Outer - gap).coerceAtLeast(0.dp))
    }
}
