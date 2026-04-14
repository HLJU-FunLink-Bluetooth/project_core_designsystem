package com.hlju.funlinkbluetooth.core.designsystem.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import com.hlju.funlinkbluetooth.core.designsystem.token.defaultPageWindowInsets
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.blur.BlendColorEntry
import top.yukonga.miuix.kmp.blur.BlurColors
import top.yukonga.miuix.kmp.blur.isRenderEffectSupported
import top.yukonga.miuix.kmp.blur.layerBackdrop
import top.yukonga.miuix.kmp.blur.rememberLayerBackdrop
import top.yukonga.miuix.kmp.blur.textureBlur
import top.yukonga.miuix.kmp.theme.MiuixTheme

enum class PageTopBarStyle {
    Large,
    Compact
}

@Composable
fun PageScaffold(
    title: String,
    modifier: Modifier = Modifier,
    scrollBehavior: ScrollBehavior,
    topBarStyle: PageTopBarStyle = PageTopBarStyle.Large,
    contentWindowInsets: WindowInsets = defaultPageWindowInsets(),
    navigationIcon: (@Composable () -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable (PaddingValues, Modifier) -> Unit
) {
    val topBarBackdropColor = MiuixTheme.colorScheme.surface
    val blurSupported = isRenderEffectSupported()
    val topBarBackdrop = if (blurSupported) {
        rememberLayerBackdrop(
            onDraw = {
                drawRect(topBarBackdropColor)
                drawContent()
            }
        )
    } else {
        null
    }
    val blurActive = topBarBackdrop != null
    val topBarColor = if (blurActive) {
        Color.Transparent
    } else {
        topBarBackdropColor
    }

    val topBarModifier = Modifier
        .fillMaxWidth()
        .then(
            if (blurActive) {
                Modifier.textureBlur(
                    backdrop = topBarBackdrop,
                    shape = RectangleShape,
                    blurRadius = 25f,
                    colors = BlurColors(
                        blendColors = listOf(
                            BlendColorEntry(
                                color = MiuixTheme.colorScheme.surface.copy(alpha = 0.8f)
                            )
                        )
                    )
                )
            } else {
                Modifier
            }
        )

    Scaffold(
        modifier = modifier,
        topBar = {
            Box(
                modifier = topBarModifier
            ) {
                if (topBarStyle == PageTopBarStyle.Compact) {
                    SmallTopAppBar(
                        title = title,
                        modifier = Modifier.fillMaxWidth(),
                        color = topBarColor,
                        navigationIcon = {
                            navigationIcon?.invoke()
                        },
                        actions = actions,
                        scrollBehavior = scrollBehavior
                    )
                } else {
                    TopAppBar(
                        title = title,
                        modifier = Modifier.fillMaxWidth(),
                        color = topBarColor,
                        navigationIcon = {
                            navigationIcon?.invoke()
                        },
                        actions = actions,
                        scrollBehavior = scrollBehavior
                    )
                }
            }
        },
        contentWindowInsets = contentWindowInsets
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(if (topBarBackdrop != null) Modifier.layerBackdrop(topBarBackdrop) else Modifier)
        ) {
            content(innerPadding, Modifier.fillMaxSize())
        }
    }
}
