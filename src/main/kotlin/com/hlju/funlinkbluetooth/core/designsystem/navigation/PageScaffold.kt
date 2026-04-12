package com.hlju.funlinkbluetooth.core.designsystem.navigation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.hlju.funlinkbluetooth.core.designsystem.token.defaultPageWindowInsets
import com.hlju.funlinkbluetooth.core.designsystem.token.rememberTopBarSurfaceShape
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.blur.Backdrop
import top.yukonga.miuix.kmp.blur.BlendColorEntry
import top.yukonga.miuix.kmp.blur.BlurDefaults
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
    val topBarBackdrop = rememberLayerBackdrop(
        onDraw = {
            // Record an opaque page background first so transparent content
            // inside the capture layer does not smear card colors across the blur.
            drawRect(topBarBackdropColor)
            drawContent()
        }
    )

    Scaffold(
        modifier = modifier,
        topBar = {
            PageTopBarSurface(
                backdrop = topBarBackdrop,
                scrollBehavior = scrollBehavior,
                topBarStyle = topBarStyle
            ) {
                if (topBarStyle == PageTopBarStyle.Compact) {
                    SmallTopAppBar(
                        title = title,
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.Transparent,
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
                        color = Color.Transparent,
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
                .layerBackdrop(topBarBackdrop)
        ) {
            content(innerPadding, Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun PageTopBarSurface(
    backdrop: Backdrop,
    scrollBehavior: ScrollBehavior,
    topBarStyle: PageTopBarStyle,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val isLight = !isSystemInDarkTheme()
    val collapsedFraction by remember(scrollBehavior, topBarStyle) {
        derivedStateOf {
            if (topBarStyle == PageTopBarStyle.Large) {
                scrollBehavior.state.collapsedFraction.coerceIn(0f, 1f)
            } else {
                1f
            }
        }
    }
    val surfaceShape = rememberTopBarSurfaceShape()
    val blurRadiusPx = with(density) {
        if (topBarStyle == PageTopBarStyle.Large) {
            lerp(16.dp, 10.dp, collapsedFraction).toPx()
        } else {
            11.dp.toPx()
        }
    }
    val primaryTintAlpha = if (topBarStyle == PageTopBarStyle.Large) {
        0.26f + (0.16f * collapsedFraction)
    } else {
        0.42f
    }
    val secondaryTintAlpha = if (topBarStyle == PageTopBarStyle.Large) {
        0.06f + (0.05f * collapsedFraction)
    } else {
        0.1f
    }
    val shadowElevation = if (topBarStyle == PageTopBarStyle.Large) {
        lerp(14.dp, 8.dp, collapsedFraction)
    } else {
        8.dp
    }
    val shadowColor = Color.Black.copy(
        alpha = if (topBarStyle == PageTopBarStyle.Large) {
            if (isLight) 0.08f + (0.08f * collapsedFraction) else 0.18f + (0.08f * collapsedFraction)
        } else {
            if (isLight) 0.16f else 0.26f
        }
    )
    val blurColors = BlurDefaults.blurColors(
        blendColors = listOf(
            BlendColorEntry(MiuixTheme.colorScheme.surfaceContainer.copy(alpha = primaryTintAlpha)),
            BlendColorEntry(MiuixTheme.colorScheme.surface.copy(alpha = secondaryTintAlpha))
        ),
        brightness = if (isLight) 0.01f else -0.03f,
        contrast = if (isLight) 1.04f + (0.03f * collapsedFraction) else 1.02f + (0.02f * collapsedFraction),
        saturation = if (isLight) 1.02f else 0.96f + (0.02f * collapsedFraction)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = shadowElevation,
                shape = surfaceShape,
                clip = false,
                ambientColor = shadowColor,
                spotColor = shadowColor
            )
            .clip(surfaceShape)
            .textureBlur(
                backdrop = backdrop,
                shape = surfaceShape,
                blurRadius = blurRadiusPx,
                colors = blurColors
            )
    ) {
        content()
    }
}
