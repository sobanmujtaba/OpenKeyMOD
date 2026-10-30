package com.atharok.btremote.ui.views.mouse

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atharok.btremote.R
import com.atharok.btremote.common.utils.AppIcons
import com.atharok.btremote.common.utils.Ref
import com.atharok.btremote.domain.entities.remoteInput.MouseAction
import com.atharok.btremote.ui.components.DefaultElevatedCard
import com.atharok.btremote.ui.components.customButtons.IconHoldButton
import com.atharok.btremote.ui.components.customButtons.SurfaceButton
import com.atharok.btremote.ui.theme.OkmGlass

@Composable
fun MousePadLayout(
    mouseSpeed: Float,
    scrollSpeed: Float,
    shouldInvertMouseScrollingDirection: Boolean,
    useGyroscope: Boolean,
    sendMouseInput: (MouseAction, Float, Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val mouseAction: Ref<MouseAction> = remember { Ref(MouseAction.NONE) }
    val palette = OkmGlass.palette()
    val gap = 8.dp

    if(useGyroscope) {
        MouseGyroscope(
            mouseSpeed = mouseSpeed,
            onMousePositionChange = { x: Float, y: Float ->
                sendMouseInput(mouseAction.value, x, y, 0f)
            }
        )
    }

    DefaultElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        fill = palette.content
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(gap)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .background(palette.accent, CircleShape)
                        )
                        Text(
                            text = stringResource(id = R.string.okm_trackpad).uppercase(),
                            color = palette.accent,
                            fontSize = 11.sp,
                            lineHeight = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.6.sp
                        )
                    }
                    Text(
                        text = stringResource(id = R.string.okm_trackpad_hint),
                        color = palette.secondaryText,
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.84f),
                horizontalArrangement = Arrangement.spacedBy(gap)
            ) {
                MousePad(
                    mouseSpeed = mouseSpeed,
                    scrollSpeed = scrollSpeed,
                    sendMouseAction = {
                        mouseAction.value = it
                        sendMouseInput(mouseAction.value, 0f, 0f, 0f)
                    },
                    sendMousePosition = { x: Float, y: Float ->
                        sendMouseInput(mouseAction.value, x, y, 0f)
                    },
                    sendMouseScroll = { wheel: Float ->
                        val mouseWheel = wheel * if(shouldInvertMouseScrollingDirection) -1f else 1f
                        sendMouseInput(mouseAction.value, 0f, 0f, mouseWheel)
                    },
                    shape = RoundedCornerShape(18.dp),
                    fill = palette.inset,
                    inset = true,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                )

                ScrollMouseButtonsLayout(
                    scrollSpeed = scrollSpeed,
                    sendMouseScroll = {
                        sendMouseInput(mouseAction.value, 0f, 0f, it)
                    },
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(0.16f)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.18f),
                horizontalArrangement = Arrangement.spacedBy(gap)
            ) {
                MouseActionButton(
                    contentDescription = stringResource(id = R.string.okm_left_click),
                    action = MouseAction.MOUSE_CLICK_LEFT,
                    sendMouseAction = {
                        mouseAction.value = it
                        sendMouseInput(mouseAction.value, 0f, 0f, 0f)
                    },
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                )

                MouseActionButton(
                    contentDescription = stringResource(id = R.string.okm_middle_click),
                    action = MouseAction.MOUSE_CLICK_MIDDLE,
                    sendMouseAction = {
                        mouseAction.value = it
                        sendMouseInput(mouseAction.value, 0f, 0f, 0f)
                    },
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(0.8f)
                )

                MouseActionButton(
                    contentDescription = stringResource(id = R.string.okm_right_click),
                    action = MouseAction.MOUSE_CLICK_RIGHT,
                    sendMouseAction = {
                        mouseAction.value = it
                        sendMouseInput(mouseAction.value, 0f, 0f, 0f)
                    },
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                )
            }
        }
    }
}

@Composable
private fun MouseActionButton(
    contentDescription: String,
    action: MouseAction,
    sendMouseAction: (MouseAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val container = OkmGlass.palette().contentStrong
    val tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.82f)

    SurfaceButton(
        touchDown = { sendMouseAction(action) },
        touchUp = { sendMouseAction(MouseAction.NONE) },
        modifier = modifier.semantics { this.contentDescription = contentDescription },
        shape = RoundedCornerShape(14.dp),
        fill = container
    ) {
        Box(contentAlignment = Alignment.Center) {
            MouseActionGlyph(
                action = action,
                tint = tint,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun MouseActionGlyph(
    action: MouseAction,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val strokeWidth = 1.35.dp.toPx()
        val bodyWidth = size.width * 0.56f
        val bodyHeight = size.height * 0.86f
        val left = (size.width - bodyWidth) / 2f
        val top = (size.height - bodyHeight) / 2f
        val cornerRadius = CornerRadius(bodyWidth / 2f, bodyWidth / 2f)
        val body = Rect(left, top, left + bodyWidth, top + bodyHeight)
        val splitY = top + bodyHeight * 0.42f

        val bodyPath = Path().apply {
            addRoundRect(RoundRect(body, cornerRadius, cornerRadius, cornerRadius, cornerRadius))
        }

        clipPath(bodyPath) {
            when(action) {
                MouseAction.MOUSE_CLICK_LEFT -> drawRect(
                    color = tint,
                    topLeft = Offset(left - 1f, top - 1f),
                    size = Size(bodyWidth / 2f + 1f, splitY - top + 1f)
                )
                MouseAction.MOUSE_CLICK_RIGHT -> drawRect(
                    color = tint,
                    topLeft = Offset(left + bodyWidth / 2f, top - 1f),
                    size = Size(bodyWidth / 2f + 1f, splitY - top + 1f)
                )
                MouseAction.MOUSE_CLICK_MIDDLE -> drawRoundRect(
                    color = tint,
                    topLeft = Offset(size.width / 2f - bodyWidth * 0.08f, top + bodyHeight * 0.10f),
                    size = Size(bodyWidth * 0.16f, bodyHeight * 0.26f),
                    cornerRadius = CornerRadius(bodyWidth * 0.08f, bodyWidth * 0.08f)
                )
                MouseAction.NONE -> Unit
            }
        }

        drawPath(path = bodyPath, color = tint, style = Stroke(width = strokeWidth))

        if(action != MouseAction.MOUSE_CLICK_MIDDLE) {
            drawLine(
                color = tint,
                start = Offset(left + bodyWidth * 0.12f, splitY),
                end = Offset(left + bodyWidth * 0.88f, splitY),
                strokeWidth = strokeWidth
            )
            drawLine(
                color = tint,
                start = Offset(size.width / 2f, top + bodyWidth * 0.10f),
                end = Offset(size.width / 2f, splitY),
                strokeWidth = strokeWidth
            )
        }
    }
}

@Composable
private fun ScrollMouseButtonsLayout(
    scrollSpeed: Float,
    sendMouseScroll: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconHoldButton(
            image = AppIcons.MouseScrollUp,
            contentDescription = stringResource(id = R.string.mouse_wheel_up),
            onHold = {
                sendMouseScroll(1f)
            },
            onRelease = {
                sendMouseScroll(0f)
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            durationBetweenRepeatsInMillis = getScrollDurationBetweenRepeats(scrollSpeed),
            shape = RoundedCornerShape(14.dp),
            iconFillFraction = 0.34f
        )

        IconHoldButton(
            image = AppIcons.MouseScrollDown,
            contentDescription = stringResource(id = R.string.mouse_wheel_down),
            onHold = {
                sendMouseScroll(-1f)
            },
            onRelease = {
                sendMouseScroll(0f)
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            durationBetweenRepeatsInMillis = getScrollDurationBetweenRepeats(scrollSpeed),
            shape = RoundedCornerShape(14.dp),
            iconFillFraction = 0.34f
        )
    }
}

private fun getScrollDurationBetweenRepeats(speed: Float): Long = speed.takeIf { it > 0f }?.let { (100f / it).toLong().coerceIn(16L, 200L) } ?: 100L
