package com.hlju.funlinkbluetooth.core.designsystem.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.zIndex
import com.hlju.funlinkbluetooth.core.designsystem.motion.DampedDragAnimation
import com.hlju.funlinkbluetooth.core.designsystem.motion.InteractiveHighlight
import com.hlju.funlinkbluetooth.core.designsystem.navigation.liquid.InnerShadow
import com.hlju.funlinkbluetooth.core.designsystem.navigation.liquid.innerShadow
import com.hlju.funlinkbluetooth.core.designsystem.navigation.liquid.lens
import com.hlju.funlinkbluetooth.core.designsystem.navigation.liquid.rememberCombinedBackdrop
import com.hlju.funlinkbluetooth.core.designsystem.navigation.liquid.unionLens
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
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Refresh
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

private val LocalIosTabScale = compositionLocalOf { { 1f } }

private val CompactBarScreenPadding = 16.dp
private val CompactBarTabSlotWidth = 76.dp
private val CompactBarHeight = 64.dp
private val CompactBarIndicatorHeight = 56.dp
private val CompactBarPressedIndicatorHeight = 78.dp
private val CompactBarPadding = 4.dp
private val CompactBarIconSize = 22.dp
private val CompactBarLabelSize = 11.sp
private val FocusedBarSize = 48.dp
private val FocusedBarIndicatorHeight = 40.dp
private val FocusedBarIconSize = 24.dp
private val SideActionCollapsedWidth = CompactBarHeight
private val SideActionExpandedWidth = 168.dp
private val FloatingBarShadowRadius = 10.dp
private const val FloatingBarShadowAlpha = 0.2f
private const val FloatingBarPressedShadowAlphaExtra = 0.06f
private val UnionMaxK = 4.dp
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
    val label: String? = null,
    val expanded: Boolean = false,
    val options: List<FloatingBottomBarActionOption> = emptyList(),
)

data class FloatingBottomBarActionOption(
    val label: String,
    val selected: Boolean,
    val contentDescription: String,
    val onClick: () -> Unit,
)

@Composable
fun rememberRoleSwitchFloatingBottomBarAction(
    currentRoleLabel: String,
    isHostSelected: Boolean,
    isClientSelected: Boolean,
    onHostSelected: () -> Unit,
    onClientSelected: () -> Unit,
): FloatingBottomBarAction {
    var expanded by remember { mutableStateOf(false) }

    return FloatingBottomBarAction(
        icon = MiuixIcons.Refresh,
        label = "切换",
        contentDescription = if (expanded) "切换当前角色 $currentRoleLabel" else "展开角色切换",
        expanded = expanded,
        onClick = { expanded = !expanded },
        options = listOf(
            FloatingBottomBarActionOption(
                label = "Host",
                selected = isHostSelected,
                contentDescription = "切换到 Host",
                onClick = onHostSelected,
            ),
            FloatingBottomBarActionOption(
                label = "Client",
                selected = isClientSelected,
                contentDescription = "切换到 Client",
                onClick = onClientSelected,
            ),
        ),
    )
}

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
    val sideActionExpanded = sideAction?.expanded == true
    val sideActionProgressState = animateFloatAsState(
        targetValue = if (sideActionExpanded) 1f else 0f,
        animationSpec = spring(0.86f, 520f, 0.001f),
        label = "floatingBottomBarSideActionExpand",
    )
    val sideActionProgress by sideActionProgressState
    val fullMenuAlpha = (1f - sideActionProgress).coerceIn(0f, 1f)
    val focusedMenuAlpha = sideActionProgress.coerceIn(0f, 1f)

    val tabsBackdrop = rememberLayerBackdrop()
    val sideActionBackdrop = rememberLayerBackdrop()
    val density = LocalDensity.current
    val isLtr = LocalLayoutDirection.current == LayoutDirection.Ltr
    val animationScope = rememberCoroutineScope()
    val onSelectedUpdated by rememberUpdatedState(onSelected)
    val latestSelectedTarget by rememberUpdatedState(selectedTarget)

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
    var focusedActionClickLocked by remember { mutableStateOf(false) }
    val focusTransitionActive = sideActionExpanded || sideActionProgress > 0.01f
    val latestInteractionBlocked by rememberUpdatedState(focusTransitionActive)

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
            pressedScale = with(density) {
                CompactBarPressedIndicatorHeight.toPx() / CompactBarIndicatorHeight.toPx().coerceAtLeast(1f)
            },
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
                if (latestInteractionBlocked) return@DampedDragAnimation
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
                if (!latestInteractionBlocked && tabWidthPx > 0f) {
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
    val focusedActionPress = remember(animationScope) {
        DampedDragAnimation(
            animationScope = animationScope,
            initialValue = 0f,
            valueRange = 0f..1f,
            visibilityThreshold = 0.001f,
            initialScale = 1f,
            pressedScale = 1f,
            pressProgressAnimationSpec = spring(0.7f, 450f, 0.001f),
            onDragStarted = {},
            onDragStopped = {},
            onDrag = { _, _ -> },
        )
    }
    val sideActionPress = remember(animationScope) {
        DampedDragAnimation(
            animationScope = animationScope,
            initialValue = 0f,
            valueRange = 0f..1f,
            visibilityThreshold = 0.001f,
            initialScale = 1f,
            pressedScale = 1f,
            pressProgressAnimationSpec = spring(0.7f, 450f, 0.001f),
            onDragStarted = {},
            onDragStopped = {},
            onDrag = { _, _ -> },
        )
    }

    LaunchedEffect(selectedTarget) {
        if (!focusTransitionActive && currentIndex != selectedTarget) {
            currentIndex = selectedTarget
        }
    }

    LaunchedEffect(dampedDrag) {
        snapshotFlow { currentIndex }.drop(1).collectLatest { index ->
            if (latestInteractionBlocked) return@collectLatest
            dampedDrag.animateToValue(index.toFloat())
            onSelectedUpdated(index)
        }
    }

    LaunchedEffect(sideActionExpanded, dampedDrag) {
        focusedActionClickLocked = false
        val targetIndex = latestSelectedTarget
        if (sideActionExpanded) {
            dampedDrag.snapToValue(targetIndex.toFloat())
        } else {
            if (currentIndex != targetIndex) {
                currentIndex = targetIndex
            }
            dampedDrag.snapToValue(targetIndex.toFloat())
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

    val focusedTabBackdrop = activeBackdrop?.let { backdrop ->
        rememberCombinedBackdrop(backdrop, tabsBackdrop)
    }
    val unionControlsBackdrop = if (sideAction != null) {
        rememberCombinedBackdrop(tabsBackdrop, sideActionBackdrop)
    } else {
        tabsBackdrop
    }
    val unionBackdrop = activeBackdrop?.let { backdrop ->
        rememberCombinedBackdrop(backdrop, unionControlsBackdrop)
    }
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
                        enabled = !focusTransitionActive,
                        role = Role.Tab,
                        onClick = { currentIndex = index },
                    )
                    .alpha(fullMenuAlpha)
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
            val containerMaxWidth = maxWidth
            val preferredBarWidth = CompactBarTabSlotWidth * tabsCount.toFloat() + CompactBarPadding * 2f
            val actionGap = if (sideAction != null) 8.dp else 0.dp
            val collapsedActionWidth = if (sideAction != null) SideActionCollapsedWidth else 0.dp
            val expandedActionWidth = if (sideAction != null) SideActionExpandedWidth else 0.dp
            val availableFullBarWidth = if (maxWidth > CompactBarScreenPadding * 2f) {
                maxOf(
                    FocusedBarSize,
                    maxWidth - CompactBarScreenPadding * 2f - actionGap - collapsedActionWidth,
                )
            } else {
                maxWidth
            }
            val availableFocusedBarWidth = if (maxWidth > CompactBarScreenPadding * 2f) {
                maxOf(
                    FocusedBarSize,
                    maxWidth - CompactBarScreenPadding * 2f - actionGap - expandedActionWidth,
                )
            } else {
                maxWidth
            }
            val fullBarWidth = minOf(preferredBarWidth, availableFullBarWidth)
            val focusedBarWidth = minOf(FocusedBarSize, availableFocusedBarWidth)
            val barWidth = lerpDp(fullBarWidth, focusedBarWidth, sideActionProgress)
            val barHeight = lerpDp(CompactBarHeight, FocusedBarSize, sideActionProgress)
            val indicatorHeight = lerpDp(CompactBarIndicatorHeight, FocusedBarIndicatorHeight, sideActionProgress)
            val actionWidth = lerpDp(collapsedActionWidth, expandedActionWidth, sideActionProgress)
            val focusMorphProgress = FastOutSlowInEasing.transform(sideActionProgress.coerceIn(0f, 1f))
            val expandedFocusVisible = !focusTransitionActive
            val focusPressProgress = if (expandedFocusVisible) dampedDrag.pressProgress else 0f
            val focusedButtonPressProgress =
                (focusedActionPress.pressProgress * focusMorphProgress).coerceIn(0f, 1f)
            val focusedButtonScale = lerp(1f, 78f / 64f, focusedButtonPressProgress)
            val barShadowAlpha = FloatingBarShadowAlpha +
                FloatingBarPressedShadowAlphaExtra * maxOf(focusPressProgress, focusedButtonPressProgress)
            val sideActionPressProgress = sideActionPress.pressProgress
            val sideActionScale = sideActionButtonScale(sideActionProgress, sideActionPressProgress)
            val focusContentZ = if (focusPressProgress > 0.01f || focusedButtonPressProgress > 0.01f) 1f else 0f

            // ── Union effect ──
            // Disable only during the morphing transition (0 < sideActionProgress < 1),
            // not after the transition settles — the expanded state should still union.
            val unionMorphing = sideActionProgress > 0.01f && sideActionProgress < 0.99f
            val unionPressTarget = if (!unionMorphing && sideAction != null) {
                maxOf(focusPressProgress, sideActionPressProgress, focusedButtonPressProgress)
            } else 0f
            val unionK by animateFloatAsState(
                targetValue = unionPressTarget * with(density) { UnionMaxK.toPx() },
                animationSpec = spring(0.7f, 400f, 0.001f),
                label = "floatingBottomBarUnionK",
            )
            val unionVisible = unionK > 0.5f && sideAction != null && activeBackdrop != null

            Box(contentAlignment = Alignment.Center) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(actionGap),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .width(barWidth)
                            .height(barHeight),
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(
                            modifier = Modifier
                                .graphicsLayer { translationX = panelOffset }
                                .width(barWidth * focusedButtonScale)
                                .height(barHeight * focusedButtonScale)
                                .floatingBarShadow(pillShape, barShadowAlpha),
                        )
                    }
                    if (sideAction != null) {
                        Box(
                            modifier = Modifier
                                .width(actionWidth)
                                .height(barHeight),
                            contentAlignment = Alignment.Center,
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(actionWidth * sideActionScale)
                                    .height(barHeight * sideActionScale)
                                    .floatingBarShadow(
                                        shape = pillShape,
                                        alpha = FloatingBarShadowAlpha +
                                            FloatingBarPressedShadowAlphaExtra * sideActionPressProgress,
                                    ),
                            )
                        }
                    }
                }

                // ── Union neck overlay ──
                if (unionVisible) {
                    val totalContentWidth = barWidth + actionGap + actionWidth
                    val barWidthPx = with(density) { barWidth.toPx() }
                    val barHeightPx = with(density) { barHeight.toPx() }
                    val actionWidthPx = with(density) { actionWidth.toPx() }
                    val actionGapPx = with(density) { actionGap.toPx() }
                    // The navigation pill can be scaled by the tab press layerBlock
                    // or by the focused-button press wrapper after the side action expands.
                    val navigationLayerScale = lerp(
                        1f,
                        1f + with(density) { 16.dp.toPx() } / barWidthPx.coerceAtLeast(1f),
                        focusPressProgress,
                    )
                    val navigationVisualScale = navigationLayerScale * focusedButtonScale
                    val leftHw = barWidthPx / 2f * navigationVisualScale
                    val leftHh = barHeightPx / 2f * navigationVisualScale
                    val leftR = barHeightPx / 2f * navigationVisualScale
                    val rightHw = actionWidthPx / 2f * sideActionScale
                    val rightHh = barHeightPx / 2f * sideActionScale
                    val rightR = barHeightPx / 2f * sideActionScale
                    val overlayHorizontalOutsetPx = maxOf(
                        (leftHw - barWidthPx / 2f).coerceAtLeast(0f) + abs(panelOffset),
                        (rightHw - actionWidthPx / 2f).coerceAtLeast(0f),
                        unionK.coerceAtLeast(0f),
                    )
                    val overlayVerticalOutsetPx =
                        (maxOf(leftHh, rightHh) - barHeightPx / 2f).coerceAtLeast(0f) +
                            unionK.coerceAtLeast(0f)
                    val overlayHorizontalOutset = with(density) { overlayHorizontalOutsetPx.toDp() }
                    val overlayVerticalOutset = with(density) { overlayVerticalOutsetPx.toDp() }
                    // Coordinates in the overlay Box's local space (width = totalContentWidth)
                    // panelOffset: the rubber-band offset applied to the tab Row inside the left pill
                    val leftCx = if (isLtr) {
                        overlayHorizontalOutsetPx + barWidthPx / 2f + panelOffset
                    } else {
                        overlayHorizontalOutsetPx + actionWidthPx + actionGapPx + barWidthPx / 2f + panelOffset
                    }
                    val leftCy = overlayVerticalOutsetPx + barHeightPx / 2f
                    val rightCx = if (isLtr) {
                        overlayHorizontalOutsetPx + barWidthPx + actionGapPx + actionWidthPx / 2f
                    } else {
                        overlayHorizontalOutsetPx + actionWidthPx / 2f
                    }
                    val rightCy = overlayVerticalOutsetPx + barHeightPx / 2f
                    Box(
                        modifier = Modifier
                            .width(totalContentWidth)
                            .height(barHeight)
                            .zIndex(0.5f),
                    ) {
                        Box(
                            modifier = Modifier
                                .offset(
                                    x = -overlayHorizontalOutset,
                                    y = -overlayVerticalOutset,
                                )
                                .width(totalContentWidth + overlayHorizontalOutset * 2f)
                                .height(barHeight + overlayVerticalOutset * 2f)
                                .drawBackdrop(
                                    backdrop = unionBackdrop ?: activeBackdrop,
                                    shape = { RectangleShape },
                                    effects = {
                                        vibrancy()
                                        blur(4.dp.toPx(), 4.dp.toPx())
                                        unionLens(
                                            leftCenter = floatArrayOf(leftCx, leftCy),
                                            leftHalfSize = floatArrayOf(leftHw, leftHh),
                                            leftRadius = leftR,
                                            rightCenter = floatArrayOf(rightCx, rightCy),
                                            rightHalfSize = floatArrayOf(rightHw, rightHh),
                                            rightRadius = rightR,
                                            unionK = unionK,
                                            refractionHeight = 24.dp.toPx(),
                                            refractionAmount = 24.dp.toPx(),
                                            depthEffect = false,
                                            surfaceColor = containerColor,
                                        )
                                    },
                                    onDrawSurface = {},
                                ),
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(actionGap),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                Box(
                    modifier = Modifier
                        .zIndex(focusContentZ)
                        .width(barWidth)
                        .height(barHeight)
                        .graphicsLayer {
                            scaleX = focusedButtonScale
                            scaleY = focusedButtonScale
                        },
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
                                            highlight = { baseHighlight.copy(alpha = 0.75f) },
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
                                .height(barHeight)
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
                                    .height(indicatorHeight)
                                    .padding(horizontal = CompactBarPadding),
                                verticalAlignment = Alignment.CenterVertically,
                                content = tabsContent,
                            )
                        }
                    }

                    run {
                        val maxWidthPx = with(density) { containerMaxWidth.toPx() }
                        val fullBarWidthPx = with(density) { fullBarWidth.toPx() }
                        val barWidthPx = with(density) { barWidth.toPx() }
                        val actionGapPx = with(density) { actionGap.toPx() }
                        val actionWidthPx = with(density) { actionWidth.toPx() }
                        val collapsedActionWidthPx = with(density) { collapsedActionWidth.toPx() }
                        val compactPaddingPx = with(density) { CompactBarPadding.toPx() }
                        val fullTabWidthPx = ((fullBarWidthPx - compactPaddingPx * 2f) / tabsCount).coerceAtLeast(0f)
                        val fullTabWidthDp = with(density) { fullTabWidthPx.toDp() }
                        val focusShellHeight = CompactBarIndicatorHeight
                        val focusHeight = CompactBarIndicatorHeight
                        val focusShellWidthPx = with(density) { fullTabWidthDp.toPx() }
                        val focusWidthPx = with(density) { fullTabWidthDp.toPx() }
                        val fullRowWidthPx = fullBarWidthPx + actionGapPx + collapsedActionWidthPx
                        val currentRowWidthPx = barWidthPx + actionGapPx + actionWidthPx
                        val fullRowStartX = (maxWidthPx - fullRowWidthPx) / 2f
                        val currentRowStartX = (maxWidthPx - currentRowWidthPx) / 2f
                        val focusValue = if (focusTransitionActive) latestSelectedTarget.toFloat() else dampedDrag.value
                        val normalLocalCenterX = if (isLtr) {
                            compactPaddingPx + (focusValue + 0.5f) * fullTabWidthPx
                        } else {
                            fullBarWidthPx - compactPaddingPx - (focusValue + 0.5f) * fullTabWidthPx
                        }
                        val normalGlobalCenterX = if (isLtr) {
                            fullRowStartX + normalLocalCenterX
                        } else {
                            fullRowStartX + fullRowWidthPx - fullBarWidthPx + normalLocalCenterX
                        }
                        val focusCenterX = normalGlobalCenterX - currentRowStartX + panelOffset
                        val focusBaseCenterX = if (isLtr) focusShellWidthPx / 2f else barWidthPx - focusShellWidthPx / 2f
                        val focusTranslationX = focusCenterX - focusBaseCenterX
                        val focusLensBaseCenterX = if (isLtr) focusWidthPx / 2f else barWidthPx - focusWidthPx / 2f
                        val focusLensTranslationX = focusCenterX - focusLensBaseCenterX
                        val focusVelocity = if (expandedFocusVisible) dampedDrag.velocity / 10f else 0f
                        val focusScaleX =
                            dampedDrag.scaleX /
                                (1f - (focusVelocity * 0.75f).coerceIn(-0.2f, 0.2f))
                        val focusScaleY =
                            dampedDrag.scaleY *
                                (1f - (focusVelocity * 0.25f).coerceIn(-0.2f, 0.2f))

                        if (focusedTabBackdrop != null) {
                            if (expandedFocusVisible) {
                                Box(
                                    modifier = Modifier
                                        .graphicsLayer { translationX = focusLensTranslationX }
                                        .then(interactiveHighlight.gestureModifier)
                                        .then(dampedDrag.modifier)
                                        .drawBackdrop(
                                            backdrop = focusedTabBackdrop,
                                            shape = { pillShape },
                                            effects = {
                                                lens(
                                                    refractionHeight = 10.dp.toPx() * focusPressProgress,
                                                    refractionAmount = 14.dp.toPx() * focusPressProgress,
                                                    depthEffect = true,
                                                    chromaticAberration = 0.5f,
                                                )
                                            },
                                            highlight = { pillHighlight.copy(alpha = focusPressProgress) },
                                            layerBlock = {
                                                scaleX = focusScaleX
                                                scaleY = focusScaleY
                                            },
                                            onDrawSurface = {
                                                drawRect(
                                                    color = if (!isDark) Color.Black.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.1f),
                                                    alpha = 1f - focusPressProgress,
                                                )
                                                drawRect(Color.Black.copy(alpha = 0.03f * focusPressProgress))
                                            },
                                        )
                                        .innerShadow(shape = pillShape) {
                                            InnerShadow(
                                                radius = 8.dp * focusPressProgress,
                                                color = Color.Black.copy(alpha = 0.15f),
                                                alpha = focusPressProgress,
                                            )
                                        }
                                        .height(focusHeight)
                                        .width(fullTabWidthDp),
                                )
                            }
                        } else {
                            if (expandedFocusVisible) {
                                Box(
                                    modifier = Modifier
                                        .offset { IntOffset(focusTranslationX.roundToInt(), 0) }
                                        .then(dampedDrag.modifier)
                                        .height(focusShellHeight)
                                        .width(fullTabWidthDp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .graphicsLayer {
                                                scaleX = focusScaleX
                                                scaleY = focusScaleY
                                            }
                                            .clip(pillShape)
                                            .background(accentColor.copy(alpha = 0.15f), pillShape)
                                            .height(focusHeight)
                                            .width(fullTabWidthDp),
                                        contentAlignment = Alignment.CenterStart,
                                    ) {
                                        CompositionLocalProvider(LocalContentColor provides accentColor) {
                                            Row(
                                                modifier = Modifier
                                                    .clearAndSetSemantics {}
                                                    .wrapContentWidth(align = Alignment.Start, unbounded = true)
                                                    .requiredWidth(fullBarWidth - CompactBarPadding * 2f)
                                                    .height(CompactBarIndicatorHeight)
                                                    .graphicsLayer {
                                                        val progressOffset = focusValue * fullTabWidthPx
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
                    }

                    if (focusedMenuAlpha > 0.01f) {
                        Box(
                            modifier = Modifier
                                .width(barWidth)
                                .height(barHeight)
                                .alpha(focusedMenuAlpha)
                                .then(if (sideActionExpanded) focusedActionPress.modifier else Modifier)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    enabled = sideActionExpanded && !focusedActionClickLocked,
                                    role = Role.Button,
                                    onClick = {
                                        focusedActionClickLocked = true
                                        sideAction?.onClick?.invoke()
                                    },
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            AnimatedContent(
                                targetState = selectedTarget,
                                transitionSpec = {
                                    val direction = when {
                                        targetState > initialState -> 1
                                        targetState < initialState -> -1
                                        else -> 0
                                    }
                                    val visualDirection = if (isLtr) direction else -direction
                                    val enter = slideInHorizontally(
                                        animationSpec = tween(
                                            durationMillis = 180,
                                            easing = FastOutSlowInEasing,
                                        ),
                                    ) { width -> width / 2 * visualDirection } + fadeIn(
                                        animationSpec = tween(durationMillis = 120, delayMillis = 40),
                                    )
                                    val exit = slideOutHorizontally(
                                        animationSpec = tween(
                                            durationMillis = 180,
                                            easing = FastOutSlowInEasing,
                                        ),
                                    ) { width -> -width / 2 * visualDirection } + fadeOut(
                                        animationSpec = tween(durationMillis = 120),
                                    )
                                    enter togetherWith exit
                                },
                                label = "floatingBottomBarFocusedIcon",
                            ) { targetIndex ->
                                Icon(
                                    modifier = Modifier.size(FocusedBarIconSize),
                                    imageVector = tabs[targetIndex].icon,
                                    contentDescription = tabs[targetIndex].label,
                                    tint = accentColor,
                                )
                            }
                        }
                    }
                }

                sideAction?.let { action ->
                    Box(
                        modifier = Modifier
                            .width(actionWidth)
                            .height(barHeight),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (activeBackdrop != null) {
                            FloatingBottomSideActionBackdropLayer(
                                action = action,
                                backdrop = sideActionBackdrop,
                                activeBackdrop = activeBackdrop,
                                containerColor = containerColor,
                                contentColor = tabContentColor,
                                width = actionWidth,
                                height = barHeight,
                                expansionProgress = sideActionProgress,
                                pressProgress = sideActionPress.pressProgress,
                            )
                        }
                        FloatingBottomSideActionButton(
                            action = action,
                            activeBackdrop = activeBackdrop,
                            containerColor = containerColor,
                            contentColor = tabContentColor,
                            width = actionWidth,
                            height = barHeight,
                            expansionProgress = sideActionProgress,
                            sideActionPress = sideActionPress,
                        )
                    }
                }
                }
            }
        }
    }
}

@Composable
private fun FloatingBottomSideActionBackdropLayer(
    action: FloatingBottomBarAction,
    backdrop: LayerBackdrop,
    activeBackdrop: LayerBackdrop,
    containerColor: Color,
    contentColor: Color,
    width: Dp,
    height: Dp,
    expansionProgress: Float,
    pressProgress: Float,
) {
    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .clearAndSetSemantics {}
            .alpha(0f)
            .layerBackdrop(backdrop),
        contentAlignment = Alignment.Center,
    ) {
        FloatingBottomSideActionSurface(
            action = action,
            activeBackdrop = activeBackdrop,
            containerColor = containerColor,
            contentColor = contentColor,
            expansionProgress = expansionProgress,
            pressProgress = pressProgress,
        )
    }
}

@Composable
private fun FloatingBottomSideActionButton(
    action: FloatingBottomBarAction,
    activeBackdrop: LayerBackdrop?,
    containerColor: Color,
    contentColor: Color,
    width: Dp,
    height: Dp,
    expansionProgress: Float,
    sideActionPress: DampedDragAnimation,
) {
    val pressProgress = sideActionPress.pressProgress
    val parentClick = if (action.expanded && action.options.isNotEmpty()) {
        action.options.firstOrNull { !it.selected }?.onClick ?: action.onClick
    } else {
        action.onClick
    }

    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .then(sideActionPress.modifier)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Button,
                onClick = parentClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        FloatingBottomSideActionSurface(
            action = action,
            activeBackdrop = activeBackdrop,
            containerColor = containerColor,
            contentColor = contentColor,
            expansionProgress = expansionProgress,
            pressProgress = pressProgress,
        )
    }
}

@Composable
private fun FloatingBottomSideActionSurface(
    action: FloatingBottomBarAction,
    activeBackdrop: LayerBackdrop?,
    containerColor: Color,
    contentColor: Color,
    expansionProgress: Float,
    pressProgress: Float,
) {
    val shape = CircleShape
    val baseHighlight = rememberIosIndicatorHighlight(extraDegrees = -45f)
    val density = LocalDensity.current
    val collapsedContentAlpha = (1f - expansionProgress).coerceIn(0f, 1f)
    val expandedContentAlpha = expansionProgress.coerceIn(0f, 1f)
    val collapsedContentOffsetX = with(density) { (-8).dp.toPx() * expansionProgress }
    val expandedContentOffsetX = with(density) { 8.dp.toPx() * (1f - expansionProgress) }
    val selectedOption = action.options.firstOrNull { it.selected } ?: action.options.firstOrNull()
    val expandedText = selectedOption?.label ?: action.label
    val buttonScale = sideActionButtonScale(expansionProgress, pressProgress)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .graphicsLayer {
                scaleX = buttonScale
                scaleY = buttonScale
            }
            .clip(shape)
            .then(
                if (activeBackdrop != null) {
                    Modifier.drawBackdrop(
                        backdrop = activeBackdrop,
                        shape = { shape },
                        effects = {
                            vibrancy()
                            blur(4.dp.toPx(), 4.dp.toPx())
                            lens(
                                refractionHeight = 24.dp.toPx(),
                                refractionAmount = 24.dp.toPx(),
                            )
                        },
                        highlight = { baseHighlight.copy(alpha = 0.75f) },
                        onDrawSurface = { drawRect(containerColor) },
                    )
                } else {
                    Modifier.background(containerColor, shape)
                },
            )
            .then(
                if (activeBackdrop == null) {
                    Modifier.innerShadow(shape = shape) {
                        InnerShadow(
                            radius = 8.dp * pressProgress,
                            color = Color.Black.copy(alpha = 0.15f),
                            alpha = pressProgress,
                        )
                    }
                } else {
                    Modifier
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .graphicsLayer {
                    alpha = collapsedContentAlpha
                    translationX = collapsedContentOffsetX
                    val contentScale = lerp(1f, 0.96f, expansionProgress)
                    scaleX = contentScale
                    scaleY = contentScale
                },
            verticalArrangement = Arrangement.spacedBy(1.dp, Alignment.CenterVertically),
            horizontalAlignment = CenterHorizontally,
        ) {
            Icon(
                modifier = Modifier.size(CompactBarIconSize),
                imageVector = action.icon,
                contentDescription = action.contentDescription,
                tint = contentColor,
            )
            action.label?.let { label ->
                Text(
                    text = label,
                    color = contentColor,
                    fontSize = CompactBarLabelSize,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        if (expandedText != null && expandedContentAlpha > 0.01f) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .graphicsLayer {
                        alpha = expandedContentAlpha
                        translationX = expandedContentOffsetX
                    }
                    .padding(horizontal = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Text(
                        text = expandedText,
                        color = contentColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Icon(
                    modifier = Modifier.size(CompactBarIconSize),
                    imageVector = action.icon,
                    contentDescription = null,
                    tint = contentColor,
                )
            }
        }
    }
}

private fun lerpDp(start: Dp, stop: Dp, fraction: Float): Dp {
    val coerced = fraction.coerceIn(0f, 1f)
    return start + (stop - start) * coerced
}

private fun sideActionButtonScale(
    expansionProgress: Float,
    pressProgress: Float,
): Float {
    val pressScale = lerp(78f / 64f, 1.06f, expansionProgress)
    return lerp(1f, pressScale, pressProgress)
}

private fun Modifier.floatingBarShadow(
    shape: Shape,
    alpha: Float = FloatingBarShadowAlpha,
): Modifier = dropShadow(
    shape = shape,
    shadow = Shadow(
        radius = FloatingBarShadowRadius,
        color = Color.Black,
        alpha = alpha.coerceIn(0f, 1f),
    ),
)

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
