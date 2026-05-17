package com.hlju.funlinkbluetooth.core.designsystem.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hlju.funlinkbluetooth.core.designsystem.navigation.liquid.lens
import com.hlju.funlinkbluetooth.core.designsystem.navigation.liquid.vibrancy
import com.hlju.funlinkbluetooth.core.designsystem.token.Spacing
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.blur.LayerBackdrop
import top.yukonga.miuix.kmp.blur.blur
import top.yukonga.miuix.kmp.blur.drawBackdrop
import top.yukonga.miuix.kmp.blur.highlight.Highlight
import top.yukonga.miuix.kmp.theme.MiuixTheme

data class FloatingNavigationRailTab(
    val icon: ImageVector,
    val label: String,
)

@Composable
fun FloatingNavigationRail(
    tabs: List<FloatingNavigationRailTab>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    backdrop: LayerBackdrop?,
    modifier: Modifier = Modifier,
    isBlurActive: Boolean = backdrop != null,
) {
    val isLight = !isSystemInDarkTheme()
    val activeBackdrop = if (isBlurActive) backdrop else null
    val containerShape = remember { RoundedCornerShape(30.dp) }
    val itemShape = remember { RoundedCornerShape(22.dp) }
    val containerColor = if (activeBackdrop != null) {
        MiuixTheme.colorScheme.surfaceContainer.copy(alpha = 0.44f)
    } else {
        MiuixTheme.colorScheme.surfaceContainer
    }
    val highlight = remember(isLight) {
        if (isLight) Highlight.GlassStrokeMiddleLight else Highlight.GlassStrokeMiddleDark
    }

    Column(
        modifier = modifier
            .width(Spacing.NavigationRailWidth)
            .fillMaxHeight()
            .shadow(elevation = 8.dp, shape = containerShape, clip = false)
            .then(
                if (activeBackdrop != null) {
                    Modifier.drawBackdrop(
                        backdrop = activeBackdrop,
                        shape = { containerShape },
                        effects = {
                            vibrancy()
                            blur(4.dp.toPx(), 4.dp.toPx())
                            lens(
                                refractionHeight = 16.dp.toPx(),
                                refractionAmount = 16.dp.toPx(),
                            )
                        },
                        highlight = { highlight.copy(alpha = 0.7f) },
                        onDrawSurface = { drawRect(containerColor) },
                    )
                } else {
                    Modifier.background(containerColor, containerShape)
                },
            )
            .padding(horizontal = Spacing.Small, vertical = Spacing.PageBase10),
        verticalArrangement = Arrangement.spacedBy(Spacing.SmallPlus),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        tabs.forEachIndexed { index, tab ->
            val selected = selectedIndex == index
            val accent = MiuixTheme.colorScheme.primary
            val itemTone = if (selected) accent else MiuixTheme.colorScheme.onBackgroundVariant

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(itemShape)
                    .background(
                        if (selected) accent.copy(alpha = 0.14f) else Color.Transparent,
                        itemShape,
                    )
                    .clickable { onSelected(index) }
                    .padding(vertical = Spacing.PageBase10, horizontal = Spacing.SmallPlus),
                verticalArrangement = Arrangement.spacedBy(Spacing.Small, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            if (selected) accent.copy(alpha = 0.18f) else Color.Transparent,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label,
                        tint = itemTone,
                    )
                }
                Text(
                    text = tab.label,
                    fontSize = 11.sp,
                    lineHeight = 13.sp,
                    color = itemTone,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
