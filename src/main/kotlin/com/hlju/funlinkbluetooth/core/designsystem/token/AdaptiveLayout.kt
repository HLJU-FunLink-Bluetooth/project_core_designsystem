package com.hlju.funlinkbluetooth.core.designsystem.token

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp

@Composable
fun shouldUseNavigationRail(): Boolean {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val containerWidthDp = with(density) {
        LocalWindowInfo.current.containerSize.width.toDp()
    }

    return containerWidthDp >= 840.dp ||
        (containerWidthDp >= 600.dp && configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
}
