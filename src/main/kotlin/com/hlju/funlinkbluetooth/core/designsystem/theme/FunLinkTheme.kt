package com.hlju.funlinkbluetooth.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import top.yukonga.miuix.kmp.squircle.LocalSquircleEnabled
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.ThemeController
import top.yukonga.miuix.kmp.theme.defaultTextStyles

@Composable
fun FunLinkTheme(
    controller: ThemeController,
    content: @Composable () -> Unit,
) {
    val textStyles = remember { defaultTextStyles() }
    CompositionLocalProvider(LocalSquircleEnabled provides true) {
        MiuixTheme(controller, textStyles, content)
    }
}
