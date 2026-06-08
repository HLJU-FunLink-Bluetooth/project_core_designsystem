package com.hlju.funlinkbluetooth.core.designsystem.navigation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.hlju.funlinkbluetooth.core.designsystem.motion.DampedDragAnimation
import com.hlju.funlinkbluetooth.core.designsystem.motion.InteractiveHighlight
import com.hlju.funlinkbluetooth.core.designsystem.navigation.liquid.InnerShadow
import com.hlju.funlinkbluetooth.core.designsystem.navigation.liquid.innerShadow
import com.hlju.funlinkbluetooth.core.designsystem.navigation.liquid.lens
import com.hlju.funlinkbluetooth.core.designsystem.navigation.liquid.rememberCombinedBackdrop
import com.hlju.funlinkbluetooth.core.designsystem.navigation.liquid.vibrancy
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.blur.LayerBackdrop
import top.yukonga.miuix.kmp.blur.blur
import top.yukonga.miuix.kmp.blur.drawBackdrop
import top.yukonga.miuix.kmp.blur.highlight.BloomStroke
import top.yukonga.miuix.kmp.blur.highlight.Highlight
import top.yukonga.miuix.kmp.blur.highlight.LightPosition
import top.yukonga.miuix.kmp.blur.highlight.LightSource
import top.yukonga.miuix.kmp.blur.layerBackdrop
import top.yukonga.miuix.kmp.blur.rememberLayerBackdrop
import top.yukonga.miuix.kmp.blur.sensor.rememberDeviceTilt
import top.yukonga.miuix.kmp.theme.LocalContentColor
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.Platform
import top.yukonga.miuix.kmp.utils.platform
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sign
import kotlin.math.sin
import kotlin.math.sqrt

private val LocalIosTabScale = staticCompositionLocalOf { { 1f } }

private val CompactBarScreenPadding = 16.dp
private val CompactBarTabSlotWidth = 76.dp
private val CompactBarHeight = 64.dp
private val CompactBarIndicatorHeight = 56.dp
private val CompactBarPadding = 4.dp
private val CompactBarIconSize = 22.dp
private val CompactBarLabelSize = 11.sp

private val IosIndicatorSpecular: Highlight = Highlight(
    width = 1.dp,
    alpha = 1f,
    style = BloomStroke(
        color = Color.White.copy(alpha = 0.12f),
        innerBlurRadius = 2.0.dp,
        primaryLight = LightSource(
            position = LightPosition(0.5f, -0.3f, -0.05f),
            color = Color.White,
            intensity = 1f,
        ),
        secondaryLight = LightSource(
            position = LightPosition(0.5f, 0.8f, -0.5f),
            color = Color.White,
            intensity = 0.4f,
        ),
        dualPeak = true,
    ),
)

private const val LightRefX = 0.5f
private const val LightRefY = 0.7f
private const val GravityDirThresholdSq = 0.01f

data class FloatingBottomBarTab(
    val icon: ImageVector,
    val label: String,
)

data class FloatingBottomBarAction(
    val icon: ImageVector,
    val contentDescription: String,
    val onClick: () -> Unit,
)

@Composable
fun FloatingBottomBar(
    tabs: List<FloatingBottomBarTab>,
    selectedIndex: Int,
    onSelected: (index: Int) -> Unit,
    backdrop: LayerBackdrop?,
    isBlurActive: Boolean,
    modifier: Modifier = Modifier,
    sideAction: FloatingBottomBarAction? = null,
) {
    if (tabs.isEmpty()) return

    val tabsCount = tabs.size
    val selectedTarget = selectedIndex.coerceIn(0, tabs.lastIndex)
    val isDark = isSystemInDarkTheme()
    val pillShape = CircleShape
    val accentColor = MiuixTheme.colorScheme.primary
    val tabContentColor = MiuixTheme.colorScheme.onSurface
    val surfaceContainer = MiuixTheme.colorScheme.surfaceContainer
    val activeBackdrop = if (isBlurActive) backdrop else null
    val containerColor = if (activeBackdrop != null) {
        surfaceContainer.copy(alpha = 0.4f)
    } else {
        surfaceContainer
    }

    val tabsBackdrop = rememberLayerBackdrop()
    val density = LocalDensity.current
    val isLtr = LocalLayoutDirection.current == LayoutDirection.Ltr
    val animationScope = rememberCoroutineScope()
    val onSelectedUpdated by rememberUpdatedState(onSelected)

    var tabWidthPx by remember { mutableFloatStateOf(0f) }
    var totalWidthPx by remember { mutableFloatStateOf(0f) }

    val offsetAnimation = remember { Animatable(0f) }
    val rubberBandPx = with(density) { 4.dp.toPx() }
    val panelOffset by remember(rubberBandPx) {
        derivedStateOf {
            if (totalWidthPx == 0f) {
                0f
            } else {
                val fraction = (offsetAnimation.value / totalWidthPx).coerceIn(-1f, 1f)
                rubberBandPx * fraction.sign * EaseOut.transform(abs(fraction))
            }
        }
    }

    var currentIndex by remember { mutableIntStateOf(selectedTarget) }

    class DampedDragHolder {
        var instance: DampedDragAnimation? = null
    }

    val holder = remember { DampedDragHolder() }
    val dampedDrag = remember(animationScope, tabsCount, density, isLtr) {
        DampedDragAnimation(
            animationScope = animationScope,
            initialValue = selectedTarget.toFloat(),
            valueRange = 0f..(tabsCount - 1).toFloat(),
            visibilityThreshold = 0.001f,
            initialScale = 1f,
            pressedScale = 78f / 56f,
            canDrag = { offset ->
                val anim = holder.instance ?: return@DampedDragAnimation true
                if (tabWidthPx == 0f) return@DampedDragAnimation false

                val currentValue = anim.value
                val indicatorX = currentValue * tabWidthPx
                val pad = with(density) { 4.dp.toPx() }
                val globalTouchX = if (isLtr) {
                    pad + indicatorX + offset.x
                } else {
                    totalWidthPx - pad - tabWidthPx - indicatorX + offset.x
                }
                globalTouchX in 0f..totalWidthPx
            },
            onDragStarted = {},
            onDragStopped = {
                val targetIndex = targetValue.roundToInt().coerceIn(0, tabsCount - 1)
                if (currentIndex != targetIndex) {
                    currentIndex = targetIndex
                } else {
                    animateToValue(targetIndex.toFloat())
                }
                animationScope.launch {
                    offsetAnimation.animateTo(0f, spring(1f, 300f, 0.5f))
                }
            },
            onDrag = { _, dragAmount ->
                if (tabWidthPx > 0f) {
                    updateValue(
                        (targetValue + dragAmount.x / tabWidthPx * if (isLtr) 1f else -1f)
                            .coerceIn(0f, (tabsCount - 1).toFloat()),
                    )
                    animationScope.launch {
                        offsetAnimation.snapTo(offsetAnimation.value + dragAmount.x)
                    }
                }
            },
        ).also { holder.instance = it }
    }

    LaunchedEffect(selectedTarget) {
        if (currentIndex != selectedTarget) {
            currentIndex = selectedTarget
        }
    }

    LaunchedEffect(dampedDrag) {
        snapshotFlow { currentIndex }.drop(1).collectLatest { index ->
            dampedDrag.animateToValue(index.toFloat())
            onSelectedUpdated(index)
        }
    }

    val interactiveHighlight = remember(animationScope, isLtr) {
        InteractiveHighlight(
            animationScope = animationScope,
            position = { layerSize, _ ->
                Offset(
                    x = if (isLtr) {
                        (dampedDrag.value + 0.5f) * tabWidthPx + panelOffset
                    } else {
                        layerSize.width - (dampedDrag.value + 0.5f) * tabWidthPx + panelOffset
                    },
                    y = layerSize.height / 2f,
                )
            },
        )
    }

    val combinedBackdrop = activeBackdrop?.let { rememberCombinedBackdrop(it, tabsBackdrop) }
    val baseHighlight = rememberIosIndicatorHighlight(extraDegrees = -45f)
    val pillHighlight = rememberIosIndicatorHighlight(extraDegrees = 90f)

    val navBarBottomPadding = WindowInsets.navigationBars
        .only(WindowInsetsSides.Bottom)
        .asPaddingValues()
        .calculateBottomPadding()
    val bottomPaddingValue = when (platform()) {
        Platform.IOS -> 20.dp
        else -> if (navBarBottomPadding != 0.dp) 8.dp + navBarBottomPadding else 36.dp
    }

    val tabsContent: @Composable RowScope.() -> Unit = {
        val tabScale = LocalIosTabScale.current
        tabs.forEachIndexed { index, item ->
            Column(
                modifier = Modifier
                    .clickable(
                        interactionSource = null,
                        indication = null,
                        role = Role.Tab,
                        onClick = { currentIndex = index },
                    )
                    .weight(1f)
                    .fillMaxHeight()
                    .graphicsLayer {
                        val scale = tabScale()
                        scaleX = scale
                        scaleY = scale
                    },
                verticalArrangement = Arrangement.spacedBy(1.dp, Alignment.CenterVertically),
                horizontalAlignment = CenterHorizontally,
            ) {
                Icon(
                    modifier = Modifier.size(CompactBarIconSize),
                    imageVector = item.icon,
                    contentDescription = item.label,
                )
                Text(
                    text = item.label,
                    fontSize = CompactBarLabelSize,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        BoxWithConstraints(
            modifier = Modifier
                .padding(bottom = bottomPaddingValue)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            val preferredBarWidth = CompactBarTabSlotWidth * tabsCount.toFloat() + CompactBarPadding * 2f
            val actionGap = if (sideAction != null) 8.dp else 0.dp
            val actionWidth = if (sideAction != null) CompactBarHeight else 0.dp
            val availableBarWidth = if (maxWidth > CompactBarScreenPadding * 2f) {
                maxWidth - CompactBarScreenPadding * 2f - actionGap - actionWidth
            } else {
                maxWidth
            }
            val barWidth = minOf(preferredBarWidth, availableBarWidth)

            Row(
                horizontalArrangement = Arrangement.spacedBy(actionGap),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier.width(barWidth),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    CompositionLocalProvider(LocalContentColor provides tabContentColor) {
                        Row(
                            modifier = Modifier
                                .width(barWidth)
                                .onSizeChanged { coords ->
                                    totalWidthPx = coords.width.toFloat()
                                    val contentWidthPx = totalWidthPx - with(density) { 8.dp.toPx() }
                                    tabWidthPx = (contentWidthPx / tabsCount).coerceAtLeast(0f)
                                }
                                .graphicsLayer { translationX = panelOffset }
                                .dropShadow(
                                    shape = pillShape,
                                    shadow = Shadow(
                                        radius = 10.dp,
                                        color = Color.Black,
                                        alpha = 0.2f,
                                    ),
                                )
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = {},
                                )
                                .then(
                                    if (activeBackdrop != null) {
                                        Modifier.drawBackdrop(
                                            backdrop = activeBackdrop,
                                            shape = { pillShape },
                                            effects = {
                                                vibrancy()
                                                blur(4.dp.toPx(), 4.dp.toPx())
                                                lens(
                                                    refractionHeight = 24.dp.toPx(),
                                                    refractionAmount = 24.dp.toPx(),
                                                )
                                            },
                                            highlight = { baseHighlight.copy(alpha = 0.7f) },
                                            layerBlock = {
                                                val width = size.width.coerceAtLeast(1f)
                                                val scale = lerp(1f, 1f + 16.dp.toPx() / width, dampedDrag.pressProgress)
                                                scaleX = scale
                                                scaleY = scale
                                            },
                                            onDrawSurface = { drawRect(containerColor) },
                                        )
                                    } else {
                                        Modifier.background(containerColor, pillShape)
                                    },
                                )
                                .then(if (activeBackdrop != null) interactiveHighlight.modifier else Modifier)
                                .height(CompactBarHeight)
                                .padding(CompactBarPadding),
                            verticalAlignment = Alignment.CenterVertically,
                            content = tabsContent,
                        )
                    }

                    if (activeBackdrop != null) {
                        CompositionLocalProvider(
                            LocalIosTabScale provides { lerp(1f, 1.2f, dampedDrag.pressProgress) },
                            LocalContentColor provides accentColor,
                        ) {
                            Row(
                                modifier = Modifier
                                    .width(barWidth)
                                    .clearAndSetSemantics {}
                                    .alpha(0f)
                                    .layerBackdrop(tabsBackdrop)
                                    .graphicsLayer { translationX = panelOffset }
                                    .drawBackdrop(
                                        backdrop = activeBackdrop,
                                        shape = { pillShape },
                                        effects = {
                                            vibrancy()
                                            blur(4.dp.toPx(), 4.dp.toPx())
                                            lens(
                                                refractionHeight = 24.dp.toPx(),
                                                refractionAmount = 24.dp.toPx(),
                                            )
                                        },
                                        onDrawSurface = { drawRect(containerColor) },
                                    )
                                    .then(interactiveHighlight.modifier)
                                    .height(CompactBarIndicatorHeight)
                                    .padding(horizontal = CompactBarPadding),
                                verticalAlignment = Alignment.CenterVertically,
                                content = tabsContent,
                            )
                        }
                    }

                    if (tabWidthPx > 0f) {
                        val tabWidthDp = with(density) { tabWidthPx.toDp() }
                        if (combinedBackdrop != null) {
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = CompactBarPadding)
                                    .graphicsLayer {
                                        val progressOffset = dampedDrag.value * tabWidthPx
                                        translationX = if (isLtr) progressOffset + panelOffset else -progressOffset + panelOffset
                                    }
                                    .then(interactiveHighlight.gestureModifier)
                                    .then(dampedDrag.modifier)
                                    .drawBackdrop(
                                        backdrop = combinedBackdrop,
                                        shape = { pillShape },
                                        effects = {
                                            val progress = dampedDrag.pressProgress
                                            lens(
                                                refractionHeight = 10.dp.toPx() * progress,
                                                refractionAmount = 14.dp.toPx() * progress,
                                                depthEffect = true,
                                                chromaticAberration = 0.5f,
                                            )
                                        },
                                        highlight = { pillHighlight.copy(alpha = dampedDrag.pressProgress) },
                                        layerBlock = {
                                            scaleX = dampedDrag.scaleX
                                            scaleY = dampedDrag.scaleY
                                            val velocity = dampedDrag.velocity / 10f
                                            scaleX /= 1f - (velocity * 0.75f).coerceIn(-0.2f, 0.2f)
                                            scaleY *= 1f - (velocity * 0.25f).coerceIn(-0.2f, 0.2f)
                                        },
                                        onDrawSurface = {
                                            val progress = dampedDrag.pressProgress
                                            drawRect(
                                                color = if (!isDark) Color.Black.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.1f),
                                                alpha = 1f - progress,
                                            )
                                            drawRect(Color.Black.copy(alpha = 0.03f * progress))
                                        },
                                    )
                                    .innerShadow(shape = pillShape) {
                                        InnerShadow(
                                            radius = 8.dp * dampedDrag.pressProgress,
                                            color = Color.Black.copy(alpha = 0.15f),
                                            alpha = dampedDrag.pressProgress,
                                        )
                                    }
                                    .height(CompactBarIndicatorHeight)
                                    .width(tabWidthDp),
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = CompactBarPadding)
                                    .graphicsLayer {
                                        val progressOffset = dampedDrag.value * tabWidthPx
                                        translationX = if (isLtr) progressOffset + panelOffset else -progressOffset + panelOffset
                                    }
                                    .then(dampedDrag.modifier)
                                    .clip(pillShape)
                                    .background(accentColor.copy(alpha = 0.15f), pillShape)
                                    .height(CompactBarIndicatorHeight)
                                    .width(tabWidthDp),
                                contentAlignment = Alignment.CenterStart,
                            ) {
                                CompositionLocalProvider(LocalContentColor provides accentColor) {
                                    Row(
                                        modifier = Modifier
                                            .clearAndSetSemantics {}
                                            .wrapContentWidth(align = Alignment.Start, unbounded = true)
                                            .requiredWidth(with(density) { (totalWidthPx - 8.dp.toPx()).toDp() })
                                            .height(CompactBarIndicatorHeight)
                                            .graphicsLayer {
                                                val progressOffset = dampedDrag.value * tabWidthPx
                                                translationX = if (isLtr) -progressOffset else progressOffset
                                            },
                                        verticalAlignment = Alignment.CenterVertically,
                                        content = tabsContent,
                                    )
                                }
                            }
                        }
                    }
                }

                sideAction?.let { action ->
                    FloatingBottomSideActionButton(
                        action = action,
                        activeBackdrop = activeBackdrop,
                        containerColor = containerColor,
                        contentColor = accentColor,
                    )
                }
            }
        }
    }
}

@Composable
private fun FloatingBottomSideActionButton(
    action: FloatingBottomBarAction,
    activeBackdrop: LayerBackdrop?,
    containerColor: Color,
    contentColor: Color,
) {
    val shape = CircleShape
    val highlight = rememberIosIndicatorHighlight(extraDegrees = 30f)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressProgress by animateFloatAsState(
        targetValue = if (isPressed) 1f else 0f,
        animationSpec = spring(0.7f, 450f, 0.001f),
        label = "floatingSideActionPress"
    )
    val isDark = isSystemInDarkTheme()
    val animationScope = rememberCoroutineScope()
    val interactiveHighlight = remember(animationScope) {
        InteractiveHighlight(
            animationScope = animationScope,
            position = { size, _ -> Offset(size.width / 2f, size.height / 2f) }
        )
    }

    Box(
        modifier = Modifier
            .size(CompactBarHeight)
            .graphicsLayer {
                val scale = lerp(1f, 78f / 64f, pressProgress)
                scaleX = scale
                scaleY = scale
            }
            .dropShadow(
                shape = shape,
                shadow = Shadow(
                    radius = 10.dp,
                    color = Color.Black,
                    alpha = 0.2f + 0.06f * pressProgress,
                ),
            )
            .clip(shape)
            .then(if (activeBackdrop != null) interactiveHighlight.gestureModifier else Modifier)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = action.onClick,
            )
            .then(
                if (activeBackdrop != null) {
                    Modifier.drawBackdrop(
                        backdrop = activeBackdrop,
                        shape = { shape },
                        effects = {
                            vibrancy()
                            blur(4.dp.toPx(), 4.dp.toPx())
                            lens(
                                refractionHeight = (24.dp.toPx() + 8.dp.toPx() * pressProgress),
                                refractionAmount = (24.dp.toPx() + 10.dp.toPx() * pressProgress),
                                depthEffect = pressProgress > 0f,
                                chromaticAberration = 0.5f * pressProgress,
                            )
                        },
                        highlight = { highlight.copy(alpha = 0.7f + 0.25f * pressProgress) },
                        onDrawSurface = {
                            drawRect(containerColor)
                            if (pressProgress > 0f) {
                                drawRect(
                                    color = if (!isDark) {
                                        Color.Black.copy(alpha = 0.08f * pressProgress)
                                    } else {
                                        Color.White.copy(alpha = 0.10f * pressProgress)
                                    }
                                )
                            }
                        },
                    )
                } else {
                    Modifier.background(containerColor, shape)
                },
            )
            .innerShadow(shape = shape) {
                InnerShadow(
                    radius = 8.dp * pressProgress,
                    color = Color.Black.copy(alpha = 0.15f),
                    alpha = pressProgress,
                )
            }
            .then(if (activeBackdrop != null) interactiveHighlight.modifier else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            modifier = Modifier.size(28.dp),
            imageVector = action.icon,
            contentDescription = action.contentDescription,
            tint = contentColor,
        )
    }
}

@Composable
private fun rememberIosIndicatorHighlight(
    extraDegrees: Float = 0f,
): Highlight {
    val baseStyle = IosIndicatorSpecular.style as BloomStroke
    val tilt by rememberDeviceTilt()
    val rotatedPrimary = remember(tilt, baseStyle.primaryLight, extraDegrees) {
        val basePrimary = baseStyle.primaryLight
        val gx = tilt.gravityX
        val gy = tilt.gravityY
        val gMagSq = gx * gx + gy * gy
        val (lx0, ly0) = if (gMagSq > GravityDirThresholdSq) {
            val invMag = 1f / sqrt(gMagSq)
            (gx * invMag) to (gy * invMag)
        } else {
            0f to -1f
        }
        val rad = extraDegrees * PI / 180.0
        val c = cos(rad).toFloat()
        val s = sin(rad).toFloat()
        val lx = c * lx0 - s * ly0
        val ly = s * lx0 + c * ly0
        basePrimary.copy(
            position = LightPosition(
                x = LightRefX + lx,
                y = LightRefY + ly,
                z = basePrimary.position.z,
            ),
        )
    }
    return remember(rotatedPrimary) {
        IosIndicatorSpecular.copy(style = baseStyle.copy(primaryLight = rotatedPrimary))
    }
}
