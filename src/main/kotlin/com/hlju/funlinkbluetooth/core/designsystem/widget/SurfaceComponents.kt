package com.hlju.funlinkbluetooth.core.designsystem.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.hlju.funlinkbluetooth.core.designsystem.token.Corners
import com.hlju.funlinkbluetooth.core.designsystem.token.Spacing
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

enum class SurfaceTone {
    Neutral,
    Primary,
    Positive,
    Warning,
    Danger
}

@Composable
private fun toneColor(tone: SurfaceTone): Color {
    val primary = MiuixTheme.colorScheme.primary
    return when (tone) {
        SurfaceTone.Neutral -> MiuixTheme.colorScheme.onBackgroundVariant
        SurfaceTone.Primary -> primary
        SurfaceTone.Positive -> primary
        SurfaceTone.Warning -> MiuixTheme.colorScheme.error.copy(alpha = 0.78f)
        SurfaceTone.Danger -> MiuixTheme.colorScheme.error
    }
}

@Composable
private fun toneContainerColor(tone: SurfaceTone): Color {
    val primary = MiuixTheme.colorScheme.primary
    return when (tone) {
        SurfaceTone.Neutral -> MiuixTheme.colorScheme.surfaceContainer
        SurfaceTone.Primary -> primary.copy(alpha = 0.12f)
        SurfaceTone.Positive -> primary.copy(alpha = 0.12f)
        SurfaceTone.Warning -> MiuixTheme.colorScheme.error.copy(alpha = 0.12f)
        SurfaceTone.Danger -> MiuixTheme.colorScheme.error.copy(alpha = 0.15f)
    }
}

@Composable
fun StatusBadge(
    text: String,
    modifier: Modifier = Modifier,
    tone: SurfaceTone = SurfaceTone.Primary
) {
    Box(
        modifier = modifier
            .clip(Corners.nestedShape())
            .background(toneContainerColor(tone))
            .padding(horizontal = Spacing.MediumPlus, vertical = Spacing.SmallPlus),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MiuixTheme.textStyles.footnote1,
            color = toneColor(tone)
        )
    }
}

@Composable
fun SectionTitle(
    title: String,
    modifier: Modifier = Modifier,
    summary: String? = null,
    trailingContent: (@Composable RowScope.() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.ExtraLarge, vertical = Spacing.SmallPlus),
        horizontalArrangement = Arrangement.spacedBy(Spacing.Medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.ExtraSmall)
        ) {
            Text(
                text = title,
                style = MiuixTheme.textStyles.subtitle,
                color = MiuixTheme.colorScheme.onSurface
            )
            if (!summary.isNullOrBlank()) {
                Text(
                    text = summary,
                    style = MiuixTheme.textStyles.footnote2,
                    color = MiuixTheme.colorScheme.onBackgroundVariant
                )
            }
        }
        trailingContent?.invoke(this)
    }
}

@Composable
fun StateMessageCard(
    title: String,
    summary: String,
    modifier: Modifier = Modifier,
    tone: SurfaceTone = SurfaceTone.Neutral,
    insidePadding: PaddingValues = PaddingValues(
        horizontal = Spacing.PageCardPaddingLarge,
        vertical = Spacing.PageCardPadding
    ),
    footer: (@Composable () -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(Corners.PageShape)
    ) {
        Column(
            modifier = Modifier.padding(insidePadding),
            verticalArrangement = Arrangement.spacedBy(Spacing.SmallPlus)
        ) {
            StatusBadge(text = title, tone = tone)
            Text(
                text = summary,
                style = MiuixTheme.textStyles.body2,
                color = MiuixTheme.colorScheme.onSurface
            )
            footer?.invoke()
        }
    }
}
