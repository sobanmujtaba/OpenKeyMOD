package com.atharok.btremote.ui.views.mouse

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import com.atharok.btremote.common.utils.Ref
import com.atharok.btremote.common.utils.DOUBLE_TAP_DURATION_MAX
import com.atharok.btremote.common.utils.TAP_DRIFT_MAX_PX
import com.atharok.btremote.common.utils.TAP_DURATION_MAX
import com.atharok.btremote.domain.entities.remoteInput.MouseAction
import com.atharok.btremote.ui.components.DefaultElevatedCard
import com.atharok.btremote.ui.theme.OkmGlass

@Composable
fun MousePad(
    mouseSpeed: Float,
    scrollSpeed: Float,
    sendMouseAction: (input: MouseAction) -> Unit,
    sendMousePosition: (Float, Float) -> Unit,
    sendMouseScroll: (Float) -> Unit,
    shape: Shape,
    modifier: Modifier = Modifier,
    fill: Color = OkmGlass.palette().contentStrong,
    inset: Boolean = false
) {
    val tapStartTimestamp = remember { Ref(0L) }
    val tapFingerCount = remember { Ref(0) }
    val tapAccumulatedDrift = remember { Ref(0f) }
    val scrollAccumulator = remember { Ref(0f) }
    val lastTapTimestamp = remember { Ref(0L) }
    val isDoubleTapDrag = remember { Ref(false) }

    DefaultElevatedCard(
        modifier = modifier,
        shape = shape,
        fill = fill,
        inset = inset
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    awaitEachGesture {
                        awaitFirstDown()
                        while (true) {
                            val event = awaitPointerEvent()
                            val inputChanges = event.changes

                            doMove(
                                mouseSpeed = mouseSpeed,
                                inputChanges = inputChanges,
                                sendMousePosition = sendMousePosition
                            )

                            doTap(
                                inputChanges = inputChanges,
                                tapStartTimestamp = tapStartTimestamp.value,
                                onTapStartTimestampChange = { tapStartTimestamp.value = it },
                                tapFingerCount = tapFingerCount.value,
                                onTapFingerCountChange = { tapFingerCount.value = it },
                                tapAccumulatedDrift = tapAccumulatedDrift.value,
                                onTapAccumulatedDriftChange = { tapAccumulatedDrift.value = it },
                                lastTapTimestamp = lastTapTimestamp.value,
                                onLastTapTimestampChange = { lastTapTimestamp.value = it },
                                isDoubleTapDrag = isDoubleTapDrag.value,
                                onDoubleTapDragChange = { isDoubleTapDrag.value = it },
                                sendMouseAction = sendMouseAction
                            )

                            doWheel(
                                inputChanges = inputChanges,
                                scrollSpeed = scrollSpeed,
                                scrollAccumulator = scrollAccumulator,
                                sendMouseScroll = sendMouseScroll
                            )

                            inputChanges.forEach { it.consume() }

                        }
                    }
                }
        )
    }
}

// ---- Actions ----

private fun doMove(
    mouseSpeed: Float,
    inputChanges: List<PointerInputChange>,
    sendMousePosition: (Float, Float) -> Unit
) {
    if(inputChanges.size != 1) return
    val delta: Offset = inputChanges.first().positionChange()
    sendMousePosition(delta.x * mouseSpeed, delta.y * mouseSpeed)
}

private fun doTap(
    inputChanges: List<PointerInputChange>,
    tapStartTimestamp: Long,
    onTapStartTimestampChange: (Long) -> Unit,
    tapFingerCount: Int,
    onTapFingerCountChange: (Int) -> Unit,
    tapAccumulatedDrift: Float,
    onTapAccumulatedDriftChange: (Float) -> Unit,
    lastTapTimestamp: Long,
    onLastTapTimestampChange: (Long) -> Unit,
    isDoubleTapDrag: Boolean,
    onDoubleTapDragChange: (Boolean) -> Unit,
    sendMouseAction: (input: MouseAction) -> Unit
) {
    // Store the maximum number of fingers that simultaneously touched the pad, used to determine
    // the tap action (1 finger = left click, 2 fingers = right click, 3 fingers = middle click).
    if (inputChanges.size > tapFingerCount) {
        onTapFingerCountChange(inputChanges.size)
    }

    // If a first finger touches the pad, store the timestamp of the initial contact.
    val isFingerDown = inputChanges.any { it.changedToDown() }
    if (isFingerDown && tapStartTimestamp == 0L) {
        val now = System.currentTimeMillis()
        onTapStartTimestampChange(now)

        // A second one-finger tap that follows the first one quickly starts a drag. The left
        // button stays pressed until the finger is lifted, exactly like a laptop touchpad.
        if (
            !isDoubleTapDrag &&
            inputChanges.size == 1 &&
            lastTapTimestamp > 0L &&
            now - lastTapTimestamp <= DOUBLE_TAP_DURATION_MAX
        ) {
            onDoubleTapDragChange(true)
            onLastTapTimestampChange(0L)
            sendMouseAction(MouseAction.MOUSE_CLICK_LEFT)
        }
        return
    }

    // Track how much the fingers have moved on the pad. If they moved too much, the gesture will
    // not be considered a tap.
    onTapAccumulatedDriftChange(
        tapAccumulatedDrift + inputChanges.maxOf { it.positionChange().getDistance() }
    )

    // If all fingers are lifted, check if the gesture qualifies as a tap
    val isAllFingersUp = inputChanges.none { it.pressed }
    if (isAllFingersUp) {
        val now = System.currentTimeMillis()
        val tapDuration = now - tapStartTimestamp

        if (isDoubleTapDrag) {
            sendMouseAction(MouseAction.NONE)
            onLastTapTimestampChange(0L)
        } else if (tapDuration < TAP_DURATION_MAX && tapAccumulatedDrift < TAP_DRIFT_MAX_PX) {
            when (tapFingerCount) {
                1 -> sendMouseAction(MouseAction.MOUSE_CLICK_LEFT)
                2 -> sendMouseAction(MouseAction.MOUSE_CLICK_RIGHT)
                3 -> sendMouseAction(MouseAction.MOUSE_CLICK_MIDDLE)
            }
            sendMouseAction(MouseAction.NONE)
            onLastTapTimestampChange(if(tapFingerCount == 1) now else 0L)
        } else {
            onLastTapTimestampChange(0L)
        }

        // Reset state
        onDoubleTapDragChange(false)
        onTapStartTimestampChange(0L)
        onTapFingerCountChange(0)
        onTapAccumulatedDriftChange(0f)
    }
}

private fun doWheel(
    inputChanges: List<PointerInputChange>,
    scrollSpeed: Float,
    scrollAccumulator: Ref<Float>,
    sendMouseScroll: (Float) -> Unit
) {
    if(inputChanges.size != 2) return

    // Average the delta Y across all fingers using positionChange() for consistency
    val avgDeltaY = inputChanges
        .map { it.positionChange().y }
        .average()
        .toFloat()

    if (avgDeltaY == 0f) return // Nothing to scroll

    // Accumulate movement until a full scroll step is reached
    scrollAccumulator.value += avgDeltaY

    val steps = (scrollAccumulator.value / 20f).toInt()
    if (steps != 0) {
        sendMouseScroll(steps * scrollSpeed)
        sendMouseScroll(0f)
        scrollAccumulator.value -= steps * 20f
    }
}