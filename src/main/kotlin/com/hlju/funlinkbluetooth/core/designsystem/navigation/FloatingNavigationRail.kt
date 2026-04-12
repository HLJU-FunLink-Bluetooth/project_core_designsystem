package com.hlju.funlinkbluetooth.core.designsystem.navigation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.backdrop.highlight.Highlight
import com.kyant.backdrop.shadow.Shadow
import com.kyant.capsule.ContinuousCapsule
import com.hlju.funlinkbluetooth.core.designsystem.token.Spacing
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

data class FloatingNavigationRailTab(
    val icon: ImageVector,
    val label: String
)

@Composable
fun FloatingNavigationRail(
    tabs: List<FloatingNavigationRailTab>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
    isBlurEnabled: Boolean = true,
) {
    val isLight = !isSystemInDarkTheme()
    val containerColor = if (isBlurEnabled) {
        MiuixTheme.colorScheme.surfaceContainer.copy(alpha = 0.4f)
    } else {
        MiuixTheme.colorScheme.surfaceContainer
    }

    Column(
        modifier = modifier
            .width(Spacing.NavigationRailWidth)
            .fillMaxHeight()
            .drawBackdrop(
                backdrop = backdrop,
                shape = { ContinuousCapsule },
                effects = {
                    if (isBlurEnabled) {
                        vibrancy()
                        blur(8f.dp.toPx())
                        lens(24f.dp.toPx(), 24f.dp.toPx())
                    }
                },
                highlight = {
                    Highlight.Default.copy(alpha = if (isBlurEnabled) 1f else 0f)
                },
                shadow = {
                    Shadow.Default.copy(
                        color = Color.Black.copy(alpha = if (isLight) 0.1f else 0.2f)
                    )
                },
                onDrawSurface = {
                    drawRect(containerColor)
                }
            )
            .padding(horizontal = Spacing.Small, vertical = Spacing.PageBase10),
        verticalArrangement = Arrangement.spacedBy(Spacing.SmallPlus),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        tabs.forEachIndexed { index, tab ->
            val selected = selectedIndex == index
            val accent = MiuixTheme.colorScheme.primary
            val itemTone = if (selected) accent else MiuixTheme.colorScheme.onBackgroundVariant

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(ContinuousCapsule)
                    .background(
                        if (selected) accent.copy(alpha = 0.14f) else Color.Transparent
                    )
                    .clickable { onSelected(index) }
                    .padding(vertical = Spacing.PageBase10, horizontal = Spacing.SmallPlus),
                verticalArrangement = Arrangement.spacedBy(Spacing.Small, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            if (selected) accent.copy(alpha = 0.18f) else Color.Transparent
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label,
                        tint = itemTone
                    )
                }
                Text(
                    text = tab.label,
                    fontSize = 11.sp,
                    lineHeight = 13.sp,
                    color = itemTone,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
