package com.hlju.funlinkbluetooth.core.designsystem.token

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.squircle.squircleClip

object Corners {
    val Outer: Dp = 12.dp
    val NestedGapTight: Dp = 2.dp

}

@Composable
fun Modifier.clipPageShape(): Modifier {
    return squircleClip(Corners.Outer)
}

@Composable
fun Modifier.clipNestedShape(gap: Dp = Corners.NestedGapTight): Modifier {
    return squircleClip((Corners.Outer - gap).coerceAtLeast(0.dp))
}
