package com.hlju.funlinkbluetooth.core.designsystem.token

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object Corners {
    val Outer: Dp = 12.dp
    val NestedGapTight: Dp = 2.dp

    val PageShape: Shape = RoundedCornerShape(Outer)

    fun nestedShape(gap: Dp = NestedGapTight): Shape {
        return RoundedCornerShape((Outer - gap).coerceAtLeast(0.dp))
    }
}
