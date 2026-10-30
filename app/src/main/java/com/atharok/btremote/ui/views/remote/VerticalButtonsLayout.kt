package com.atharok.btremote.ui.views.remote

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.atharok.btremote.domain.entities.remoteInput.RemoteButtonProperties
import com.atharok.btremote.ui.components.customButtons.RemoteIconSurfaceButton

@Composable
fun VerticalButtonsLayout(
    topButtonProperties: RemoteButtonProperties,
    bottomButtonProperties: RemoteButtonProperties,
    sendRemoteKeyReport: (ByteArray) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(14.dp)
) {
    Column(
        modifier = modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        RemoteIconSurfaceButton(
            properties = topButtonProperties,
            sendKeyReport = sendRemoteKeyReport,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = shape
        )

        RemoteIconSurfaceButton(
            properties = bottomButtonProperties,
            sendKeyReport = sendRemoteKeyReport,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = shape
        )
    }
}
