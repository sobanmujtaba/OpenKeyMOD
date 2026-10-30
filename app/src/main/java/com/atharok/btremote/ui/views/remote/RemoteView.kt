package com.atharok.btremote.ui.views.remote

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.atharok.btremote.R
import com.atharok.btremote.common.utils.AppIcons
import com.atharok.btremote.domain.entities.remoteInput.ChannelButtonProperties
import com.atharok.btremote.domain.entities.remoteInput.RemoteButtonProperties
import com.atharok.btremote.ui.components.DefaultElevatedCard
import com.atharok.btremote.ui.components.customButtons.ChannelSurfaceButton
import com.atharok.btremote.ui.components.customButtons.IconSurfaceButton
import com.atharok.btremote.ui.components.customButtons.RemoteIconSurfaceButton
import com.atharok.btremote.ui.theme.OkmGlass

@Composable
fun RemoteView(
    sendRemoteKeyReport: (ByteArray) -> Unit,
    sendKeyboardKeyReport: (ByteArray) -> Unit,
    modifier: Modifier = Modifier,
    buttonShape: Shape = RoundedCornerShape(14.dp)
) {
    val palette = OkmGlass.palette()
    val gap = 8.dp

    DefaultElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        fill = palette.content
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(gap)
        ) {
            MultimediaButtonsLayout(
                sendRemoteKeyReport = sendRemoteKeyReport,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = buttonShape
            )

            GroupDivider()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(3f),
                horizontalArrangement = Arrangement.spacedBy(gap)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(gap)
                ) {
                    RemoteIconSurfaceButton(
                        properties = RemoteButtonProperties.VolumeUpButton,
                        sendKeyReport = sendRemoteKeyReport,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        shape = buttonShape
                    )

                    RemoteIconSurfaceButton(
                        properties = RemoteButtonProperties.VolumeDownButton,
                        sendKeyReport = sendRemoteKeyReport,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        shape = buttonShape
                    )

                    RemoteIconSurfaceButton(
                        properties = RemoteButtonProperties.VolumeMuteButton,
                        sendKeyReport = sendRemoteKeyReport,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        shape = buttonShape
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(3f),
                    verticalArrangement = Arrangement.spacedBy(gap)
                ) {
                    RemoteKeyRow(
                        sendKeyboardKeyReport = sendKeyboardKeyReport,
                        properties = listOf(
                            ChannelButtonProperties.Channel1Button,
                            ChannelButtonProperties.Channel2Button,
                            ChannelButtonProperties.Channel3Button
                        ),
                        modifier = Modifier.weight(1f),
                        shape = buttonShape
                    )
                    RemoteKeyRow(
                        sendKeyboardKeyReport = sendKeyboardKeyReport,
                        properties = listOf(
                            ChannelButtonProperties.Channel4Button,
                            ChannelButtonProperties.Channel5Button,
                            ChannelButtonProperties.Channel6Button
                        ),
                        modifier = Modifier.weight(1f),
                        shape = buttonShape
                    )
                    RemoteKeyRow(
                        sendKeyboardKeyReport = sendKeyboardKeyReport,
                        properties = listOf(
                            ChannelButtonProperties.Channel7Button,
                            ChannelButtonProperties.Channel8Button,
                            ChannelButtonProperties.Channel9Button
                        ),
                        modifier = Modifier.weight(1f),
                        shape = buttonShape
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(gap)
                ) {
                    RemoteIconSurfaceButton(
                        properties = RemoteButtonProperties.ChannelUpButton,
                        sendKeyReport = sendRemoteKeyReport,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        shape = buttonShape
                    )

                    RemoteIconSurfaceButton(
                        properties = RemoteButtonProperties.ChannelDownButton,
                        sendKeyReport = sendRemoteKeyReport,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        shape = buttonShape
                    )

                    RemoteIconSurfaceButton(
                        properties = RemoteButtonProperties.ClosedCaptionsButton,
                        sendKeyReport = sendRemoteKeyReport,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        shape = buttonShape
                    )
                }
            }

            GroupDivider()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.05f),
                horizontalArrangement = Arrangement.spacedBy(gap)
            ) {
                RemoteIconSurfaceButton(
                    properties = RemoteButtonProperties.BackButton,
                    sendKeyReport = sendRemoteKeyReport,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    shape = buttonShape
                )

                RemoteIconSurfaceButton(
                    properties = RemoteButtonProperties.HomeButton,
                    sendKeyReport = sendRemoteKeyReport,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    shape = buttonShape
                )

                ChannelSurfaceButton(
                    properties = ChannelButtonProperties.Channel0Button,
                    sendKeyReport = sendKeyboardKeyReport,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    shape = buttonShape
                )

                RemoteIconSurfaceButton(
                    properties = RemoteButtonProperties.MenuButton,
                    sendKeyReport = sendRemoteKeyReport,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    shape = buttonShape
                )

                RemoteIconSurfaceButton(
                    properties = RemoteButtonProperties.PowerButton,
                    sendKeyReport = sendRemoteKeyReport,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    shape = buttonShape,
                    fill = palette.danger.copy(alpha = if(palette.dark) 0.14f else 0.08f),
                    iconTint = palette.danger
                )
            }
        }
    }
}

@Composable
private fun GroupDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .padding(horizontal = 4.dp)
            .background(OkmGlass.palette().separator.copy(alpha = 0.6f))
    )
}

@Composable
private fun RemoteKeyRow(
    sendKeyboardKeyReport: (ByteArray) -> Unit,
    properties: List<ChannelButtonProperties>,
    modifier: Modifier = Modifier,
    shape: Shape
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        properties.forEach { property ->
            ChannelSurfaceButton(
                properties = property,
                sendKeyReport = sendKeyboardKeyReport,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                shape = shape
            )
        }
    }
}

@Composable
fun MinimalistRemoteView(
    sendRemoteKeyReport: (ByteArray) -> Unit,
    onTVChannelButtonsChanged: () -> Unit,
    onMoreButtonsVisibleChanged: () -> Unit,
    modifier: Modifier = Modifier,
    buttonShape: Shape = RoundedCornerShape(14.dp)
) {
    val padding: Dp = 8.dp
    val palette = OkmGlass.palette()

    DefaultElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        fill = palette.content
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(padding)
        ) {
            MultimediaButtonsLayout(
                sendRemoteKeyReport = sendRemoteKeyReport,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = buttonShape
            )

            GroupDivider()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2f),
                horizontalArrangement = Arrangement.spacedBy(padding)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    VerticalButtonsLayout(
                        topButtonProperties = RemoteButtonProperties.VolumeUpButton,
                        bottomButtonProperties = RemoteButtonProperties.VolumeDownButton,
                        sendRemoteKeyReport = sendRemoteKeyReport,
                        modifier = Modifier.fillMaxHeight(),
                        shape = buttonShape
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(2f),
                    verticalArrangement = Arrangement.spacedBy(padding)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(padding)
                    ) {
                        RemoteIconSurfaceButton(
                            properties = RemoteButtonProperties.VolumeMuteButton,
                            sendKeyReport = sendRemoteKeyReport,
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f),
                            shape = buttonShape
                        )

                        RemoteIconSurfaceButton(
                            properties = RemoteButtonProperties.ClosedCaptionsButton,
                            sendKeyReport = sendRemoteKeyReport,
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f),
                            shape = buttonShape
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(padding)
                    ) {
                        IconSurfaceButton(
                            image = AppIcons.TVChannel,
                            contentDescription = stringResource(id = R.string.tv),
                            touchDown = {},
                            touchUp = onTVChannelButtonsChanged,
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f),
                            shape = buttonShape
                        )

                        IconSurfaceButton(
                            image = AppIcons.MoreHoriz,
                            contentDescription = stringResource(id = R.string.more_buttons),
                            touchDown = {},
                            touchUp = onMoreButtonsVisibleChanged,
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f),
                            shape = buttonShape
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    VerticalButtonsLayout(
                        topButtonProperties = RemoteButtonProperties.BrightnessUpButton,
                        bottomButtonProperties = RemoteButtonProperties.BrightnessDownButton,
                        sendRemoteKeyReport = sendRemoteKeyReport,
                        modifier = Modifier.fillMaxHeight(),
                        shape = buttonShape
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(padding)
            ) {
                RemoteIconSurfaceButton(
                    properties = RemoteButtonProperties.BackButton,
                    sendKeyReport = sendRemoteKeyReport,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    shape = buttonShape
                )

                RemoteIconSurfaceButton(
                    properties = RemoteButtonProperties.HomeButton,
                    sendKeyReport = sendRemoteKeyReport,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    shape = buttonShape
                )

                RemoteIconSurfaceButton(
                    properties = RemoteButtonProperties.MenuButton,
                    sendKeyReport = sendRemoteKeyReport,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    shape = buttonShape
                )

                RemoteIconSurfaceButton(
                    properties = RemoteButtonProperties.PowerButton,
                    sendKeyReport = sendRemoteKeyReport,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    shape = buttonShape,
                    fill = palette.danger.copy(alpha = if(palette.dark) 0.14f else 0.08f),
                    iconTint = palette.danger
                )
            }
        }
    }
}
