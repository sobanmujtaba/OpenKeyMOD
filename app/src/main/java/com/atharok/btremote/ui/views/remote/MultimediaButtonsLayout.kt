package com.atharok.btremote.ui.views.remote

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.atharok.btremote.domain.entities.remoteInput.RemoteButtonProperties
import com.atharok.btremote.ui.components.customButtons.RemoteIconSurfaceButton
import com.atharok.btremote.ui.theme.OkmGlass

@Composable
fun MultimediaButtonsLayout(
    sendRemoteKeyReport: (ByteArray) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(14.dp)
) {
    val palette = OkmGlass.palette()

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
   ) {
        RemoteIconSurfaceButton(
            properties = RemoteButtonProperties.RewindButton,
            sendKeyReport = sendRemoteKeyReport,
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            shape = shape,
            iconFillFraction = 0.36f
        )

        RemoteIconSurfaceButton(
            properties = RemoteButtonProperties.PlayPauseButton,
            sendKeyReport = sendRemoteKeyReport,
            modifier = Modifier
                .fillMaxHeight()
                .weight(1.35f),
            shape = shape,
            fill = palette.accent.copy(alpha = if(palette.dark) 0.16f else 0.10f),
            iconFillFraction = 0.36f,
            iconTint = palette.accent
        )

        RemoteIconSurfaceButton(
            properties = RemoteButtonProperties.ForwardButton,
            sendKeyReport = sendRemoteKeyReport,
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            shape = shape,
            iconFillFraction = 0.36f
        )
    }
}
