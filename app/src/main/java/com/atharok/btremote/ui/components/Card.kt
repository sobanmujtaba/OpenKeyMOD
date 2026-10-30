package com.atharok.btremote.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.atharok.btremote.R
import com.atharok.btremote.ui.theme.OkmGlass
import com.atharok.btremote.ui.theme.contentSurface
import com.atharok.btremote.ui.theme.surfaceElevationMedium
import com.atharok.btremote.ui.theme.surfaceElevationShadow

@Composable
fun DefaultElevatedCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(dimensionResource(id = R.dimen.card_corner_radius)),
    elevation: Dp = surfaceElevationMedium(),
    shadowElevation: Dp = surfaceElevationShadow(),
    fill: Color = OkmGlass.palette().contentStrong,
    inset: Boolean = false,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier.contentSurface(
            shape = shape,
            fill = fill,
            raised = !inset
        )
    ) {
        content()
    }
}
