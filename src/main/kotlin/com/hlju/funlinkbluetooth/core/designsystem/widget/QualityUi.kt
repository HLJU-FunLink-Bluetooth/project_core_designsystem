package com.hlju.funlinkbluetooth.core.designsystem.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import top.yukonga.miuix.kmp.theme.MiuixTheme

fun qualityLabel(quality: Int): String = when (quality) {
    3 -> "高"
    2 -> "中"
    1 -> "低"
    else -> "未知"
}

@Composable
fun qualityColor(quality: Int): Color {
    val primary = MiuixTheme.colorScheme.primary
    return when (quality) {
        3 -> primary
        2 -> primary.copy(alpha = 0.72f)
        1 -> MiuixTheme.colorScheme.error
        else -> MiuixTheme.colorScheme.onBackgroundVariant
    }
}
