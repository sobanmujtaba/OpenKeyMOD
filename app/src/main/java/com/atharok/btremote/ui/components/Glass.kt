package com.atharok.btremote.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.atharok.btremote.ui.theme.OkmGlass
import com.atharok.btremote.ui.theme.contentSurface

@Composable
fun OkmGlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    contentPadding: Dp = 16.dp,
    fill: Color = OkmGlass.palette().content,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val baseModifier = modifier.contentSurface(
        shape = shape,
        fill = fill
    )

    val clickableModifier = if (onClick != null) {
        baseModifier
            .clip(shape)
            .clickable(onClick = onClick)
    } else {
        baseModifier
    }

    Column(
        modifier = clickableModifier.padding(contentPadding),
        content = content
    )
}

@Composable
fun OkmGlassInset(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    fill: Color = OkmGlass.palette().inset,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .contentSurface(
                shape = shape,
                fill = fill,
                raised = false
            )
            .padding(14.dp),
        content = content
    )
}

@Composable
fun OkmGlassDivider(
    modifier: Modifier = Modifier
) {
    val palette = OkmGlass.palette()

    androidx.compose.material3.HorizontalDivider(
        modifier = modifier,
        color = palette.separator,
        thickness = 1.dp
    )
}

@Composable
fun OkmGlassTextColor(): androidx.compose.ui.graphics.Color =
    MaterialTheme.colorScheme.onSurface
