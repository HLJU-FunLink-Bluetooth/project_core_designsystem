package com.hlju.funlinkbluetooth.core.designsystem.icon

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import top.yukonga.miuix.kmp.shapes.SmoothRoundedCornerShape
import com.hlju.funlinkbluetooth.core.designsystem.token.Corners

@Composable
fun ResourceAppIcon(@DrawableRes iconResId: Int, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(iconResId),
        contentDescription = null,
        modifier = modifier
            .clip(SmoothRoundedCornerShape(Corners.Outer))
            .fillMaxSize(),
        contentScale = ContentScale.Fit
    )
}
