package com.atharok.btremote.ui.views.gamepad

import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.atharok.btremote.R
import com.atharok.btremote.common.utils.AppIcons
import com.atharok.btremote.ui.components.OkmColors
import com.atharok.btremote.ui.components.OkmTypography
import com.atharok.btremote.ui.components.OpenKeyModFullScreen
import com.atharok.btremote.ui.components.OpenKeyModSheetHeader
import com.atharok.btremote.ui.components.customButtons.SurfaceButton
import kotlin.math.min
import kotlin.math.roundToInt

private const val BUTTON_A = 1 shl 0
private const val BUTTON_B = 1 shl 1
private const val BUTTON_X = 1 shl 2
private const val BUTTON_Y = 1 shl 3
private const val BUTTON_LB = 1 shl 4
private const val BUTTON_RB = 1 shl 5
private const val BUTTON_LT = 1 shl 6
private const val BUTTON_RT = 1 shl 7
private const val BUTTON_SELECT = 1 shl 8
private const val BUTTON_START = 1 shl 9
private const val BUTTON_UP = 1 shl 12
private const val BUTTON_DOWN = 1 shl 13
private const val BUTTON_LEFT = 1 shl 14
private const val BUTTON_RIGHT = 1 shl 15

private const val GAMEPAD_LAYOUT_PREFS = "okm_gamepad_layout"
private const val PORTRAIT_LAYOUT_KEY = "portrait"
private const val LANDSCAPE_LAYOUT_KEY = "landscape"

private enum class GamepadControl {
    LB, LT, RT, RB,
    LEFT_STICK, RIGHT_STICK,
    DPAD_UP, DPAD_DOWN, DPAD_LEFT, DPAD_RIGHT,
    FACE_Y, FACE_X, FACE_B, FACE_A,
    SELECT, START
}

@Composable
fun GamepadFullScreen(
    onSendReport: (ByteArray) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    var buttons by rememberSaveable { mutableIntStateOf(0) }
    var leftStick by remember { mutableStateOf(Offset.Zero) }
    var rightStick by remember { mutableStateOf(Offset.Zero) }
    var editing by rememberSaveable { mutableStateOf(false) }

    val sendReport by rememberUpdatedState(onSendReport)
    val context = LocalContext.current
    val prefs = remember(context) {
        context.getSharedPreferences(GAMEPAD_LAYOUT_PREFS, 0)
    }
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val layoutKey = if (isLandscape) LANDSCAPE_LAYOUT_KEY else PORTRAIT_LAYOUT_KEY
    val customPositions = remember(layoutKey) {
        mutableStateMapOf<GamepadControl, Offset>().apply {
            putAll(loadPositions(prefs, layoutKey))
        }
    }

    LaunchedEffect(buttons, leftStick, rightStick) {
        sendReport(gamepadReport(buttons, leftStick, rightStick))
    }

    DisposableEffect(Unit) {
        onDispose { sendReport(neutralGamepadReport()) }
    }

    OpenKeyModFullScreen(
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        if (isLandscape) {
            GamepadCompactHeader(
                editing = editing,
                onToggleEditing = { editing = !editing },
                onReset = {
                    customPositions.clear()
                    prefs.edit().remove(layoutKey).apply()
                },
                onDismissRequest = onDismissRequest
            )
            Spacer(modifier = Modifier.height(8.dp))
        } else {
            OpenKeyModSheetHeader(
                eyebrow = stringResource(id = R.string.okm_eyebrow),
                title = stringResource(id = R.string.okm_gamepad_title),
                subtitle = stringResource(id = R.string.okm_gamepad_subtitle),
                onDismissRequest = onDismissRequest
            )
            Spacer(modifier = Modifier.height(8.dp))
            GamepadEditBar(
                editing = editing,
                onToggleEditing = { editing = !editing },
                onReset = {
                    customPositions.clear()
                    prefs.edit().remove(layoutKey).apply()
                }
            )
        }

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            val unit = if (maxWidth < maxHeight) maxWidth else maxHeight
            val positions = defaultPositions(maxWidth, maxHeight, unit, isLandscape) + customPositions
            val onBitChange: (Int, Boolean) -> Unit = { bit, down ->
                buttons = setBit(buttons, bit, down)
            }

            GamepadControl.entries.forEach { control ->
                val (width, height) = controlSize(control, unit)
                val position = positions[control] ?: return@forEach

                DraggableControl(
                    position = position,
                    width = width,
                    height = height,
                    areaWidth = maxWidth,
                    areaHeight = maxHeight,
                    editing = editing,
                    onPositionChange = { customPositions[control] = it },
                    onDragEnd = { savePositions(prefs, layoutKey, customPositions) }
                ) {
                    GamepadControlContent(
                        control = control,
                        buttons = buttons,
                        leftStick = leftStick,
                        rightStick = rightStick,
                        onBitChange = onBitChange,
                        onLeftStickChange = { leftStick = it },
                        onRightStickChange = { rightStick = it }
                    )
                }
            }
        }
    }
}

@Composable
private fun GamepadCompactHeader(
    editing: Boolean,
    onToggleEditing: () -> Unit,
    onReset: () -> Unit,
    onDismissRequest: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(id = R.string.okm_gamepad_title),
            style = OkmTypography.sheetTitle.copy(fontSize = 22.sp, lineHeight = 26.sp),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        GamepadEditActions(
            editing = editing,
            onToggleEditing = onToggleEditing,
            onReset = onReset
        )
        IconButton(
            onClick = onDismissRequest,
            modifier = Modifier
                .size(34.dp)
                .background(OkmColors.inset(), CircleShape)
        ) {
            Icon(
                imageVector = AppIcons.Close,
                contentDescription = stringResource(id = R.string.close),
                modifier = Modifier.size(18.dp),
                tint = OkmColors.secondaryText()
            )
        }
    }
}

@Composable
private fun GamepadEditBar(
    editing: Boolean,
    onToggleEditing: () -> Unit,
    onReset: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(34.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(
                if (editing) R.string.okm_gamepad_edit_hint else R.string.okm_gamepad_layout_hint
            ),
            color = OkmColors.secondaryText(),
            fontSize = 12.sp,
            lineHeight = 15.sp,
            modifier = Modifier.weight(1f)
        )
        GamepadEditActions(
            editing = editing,
            onToggleEditing = onToggleEditing,
            onReset = onReset
        )
    }
}

@Composable
private fun GamepadEditActions(
    editing: Boolean,
    onToggleEditing: () -> Unit,
    onReset: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (editing) {
            GamepadChip(
                text = stringResource(id = R.string.okm_gamepad_reset),
                onClick = onReset
            )
        }
        GamepadChip(
            text = stringResource(
                if (editing) R.string.okm_gamepad_done else R.string.okm_gamepad_edit
            ),
            accent = editing,
            onClick = onToggleEditing
        )
    }
}

@Composable
private fun GamepadChip(
    text: String,
    onClick: () -> Unit,
    accent: Boolean = false
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = if (accent) OkmColors.accent() else OkmColors.card(),
        contentColor = if (accent) Color.White else MaterialTheme.colorScheme.onSurface
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            fontSize = 12.sp,
            lineHeight = 15.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun DraggableControl(
    position: Offset,
    width: Dp,
    height: Dp,
    areaWidth: Dp,
    areaHeight: Dp,
    editing: Boolean,
    onPositionChange: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val areaWidthPx = with(density) { areaWidth.toPx() }
    val areaHeightPx = with(density) { areaHeight.toPx() }
    val widthPx = with(density) { width.toPx() }
    val heightPx = with(density) { height.toPx() }
    val minX = (widthPx / 2f / areaWidthPx).coerceIn(0f, 0.5f)
    val minY = (heightPx / 2f / areaHeightPx).coerceIn(0f, 0.5f)
    val x = position.x.coerceIn(minX, 1f - minX)
    val y = position.y.coerceIn(minY, 1f - minY)

    val currentPosition = rememberUpdatedState(Offset(x, y))
    val currentOnPositionChange = rememberUpdatedState(onPositionChange)
    val currentOnDragEnd = rememberUpdatedState(onDragEnd)

    Box(
        modifier = Modifier
            .offset(
                x = areaWidth * x - width / 2,
                y = areaHeight * y - height / 2
            )
            .size(width, height)
    ) {
        content()

        if (editing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(1f)
                    .background(OkmColors.accent().copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                    .border(1.dp, OkmColors.accent().copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                    .pointerInput(areaWidthPx, areaHeightPx, widthPx, heightPx) {
                        detectDragGestures(
                            onDrag = { change, dragAmount ->
                                change.consume()
                                val nextX = currentPosition.value.x + dragAmount.x / areaWidthPx
                                val nextY = currentPosition.value.y + dragAmount.y / areaHeightPx
                                currentOnPositionChange.value(
                                    Offset(
                                        nextX.coerceIn(minX, 1f - minX),
                                        nextY.coerceIn(minY, 1f - minY)
                                    )
                                )
                            },
                            onDragEnd = { currentOnDragEnd.value() },
                            onDragCancel = { currentOnDragEnd.value() }
                        )
                    }
            )
        }
    }
}

@Composable
private fun GamepadControlContent(
    control: GamepadControl,
    buttons: Int,
    leftStick: Offset,
    rightStick: Offset,
    onBitChange: (Int, Boolean) -> Unit,
    onLeftStickChange: (Offset) -> Unit,
    onRightStickChange: (Offset) -> Unit
) {
    when (control) {
        GamepadControl.LB -> GamepadButton(
            label = "LB",
            contentDescription = stringResource(id = R.string.okm_gamepad_lb),
            pressed = buttons and BUTTON_LB != 0,
            onBitChange = { down -> onBitChange(BUTTON_LB, down) },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxSize()
        )
        GamepadControl.LT -> GamepadButton(
            label = "LT",
            contentDescription = stringResource(id = R.string.okm_gamepad_lt),
            pressed = buttons and BUTTON_LT != 0,
            onBitChange = { down -> onBitChange(BUTTON_LT, down) },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxSize()
        )
        GamepadControl.RT -> GamepadButton(
            label = "RT",
            contentDescription = stringResource(id = R.string.okm_gamepad_rt),
            pressed = buttons and BUTTON_RT != 0,
            onBitChange = { down -> onBitChange(BUTTON_RT, down) },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxSize()
        )
        GamepadControl.RB -> GamepadButton(
            label = "RB",
            contentDescription = stringResource(id = R.string.okm_gamepad_rb),
            pressed = buttons and BUTTON_RB != 0,
            onBitChange = { down -> onBitChange(BUTTON_RB, down) },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxSize()
        )
        GamepadControl.LEFT_STICK -> AnalogStick(
            value = leftStick,
            onValueChange = onLeftStickChange,
            contentDescription = stringResource(id = R.string.okm_gamepad_stick_left),
            modifier = Modifier.fillMaxSize()
        )
        GamepadControl.RIGHT_STICK -> AnalogStick(
            value = rightStick,
            onValueChange = onRightStickChange,
            contentDescription = stringResource(id = R.string.okm_gamepad_stick_right),
            modifier = Modifier.fillMaxSize()
        )
        GamepadControl.DPAD_UP -> GamepadButton(
            icon = AppIcons.Up,
            contentDescription = stringResource(id = R.string.up),
            pressed = buttons and BUTTON_UP != 0,
            onBitChange = { down -> onBitChange(BUTTON_UP, down) },
            modifier = Modifier.fillMaxSize()
        )
        GamepadControl.DPAD_DOWN -> GamepadButton(
            icon = AppIcons.Down,
            contentDescription = stringResource(id = R.string.down),
            pressed = buttons and BUTTON_DOWN != 0,
            onBitChange = { down -> onBitChange(BUTTON_DOWN, down) },
            modifier = Modifier.fillMaxSize()
        )
        GamepadControl.DPAD_LEFT -> GamepadButton(
            icon = AppIcons.Left,
            contentDescription = stringResource(id = R.string.left),
            pressed = buttons and BUTTON_LEFT != 0,
            onBitChange = { down -> onBitChange(BUTTON_LEFT, down) },
            modifier = Modifier.fillMaxSize()
        )
        GamepadControl.DPAD_RIGHT -> GamepadButton(
            icon = AppIcons.Right,
            contentDescription = stringResource(id = R.string.right),
            pressed = buttons and BUTTON_RIGHT != 0,
            onBitChange = { down -> onBitChange(BUTTON_RIGHT, down) },
            modifier = Modifier.fillMaxSize()
        )
        GamepadControl.FACE_Y -> GamepadButton(
            label = "Y",
            pressed = buttons and BUTTON_Y != 0,
            onBitChange = { down -> onBitChange(BUTTON_Y, down) },
            modifier = Modifier.fillMaxSize()
        )
        GamepadControl.FACE_X -> GamepadButton(
            label = "X",
            pressed = buttons and BUTTON_X != 0,
            onBitChange = { down -> onBitChange(BUTTON_X, down) },
            modifier = Modifier.fillMaxSize()
        )
        GamepadControl.FACE_B -> GamepadButton(
            label = "B",
            pressed = buttons and BUTTON_B != 0,
            onBitChange = { down -> onBitChange(BUTTON_B, down) },
            modifier = Modifier.fillMaxSize()
        )
        GamepadControl.FACE_A -> GamepadButton(
            label = "A",
            pressed = buttons and BUTTON_A != 0,
            onBitChange = { down -> onBitChange(BUTTON_A, down) },
            modifier = Modifier.fillMaxSize()
        )
        GamepadControl.SELECT -> GamepadButton(
            label = stringResource(id = R.string.okm_gamepad_select),
            contentDescription = stringResource(id = R.string.okm_gamepad_select),
            pressed = buttons and BUTTON_SELECT != 0,
            onBitChange = { down -> onBitChange(BUTTON_SELECT, down) },
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier.fillMaxSize()
        )
        GamepadControl.START -> GamepadButton(
            label = stringResource(id = R.string.okm_gamepad_start),
            contentDescription = stringResource(id = R.string.okm_gamepad_start),
            pressed = buttons and BUTTON_START != 0,
            onBitChange = { down -> onBitChange(BUTTON_START, down) },
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier.fillMaxSize()
        )
    }
}

private fun controlSize(control: GamepadControl, unit: Dp): Pair<Dp, Dp> {
    return when (control) {
        GamepadControl.LEFT_STICK,
        GamepadControl.RIGHT_STICK -> {
            val size = (unit * 0.30f).coerceAtLeast(120.dp)
            size to size
        }
        GamepadControl.LB,
        GamepadControl.LT,
        GamepadControl.RT,
        GamepadControl.RB -> {
            (unit * 0.20f).coerceAtLeast(64.dp) to 48.dp
        }
        GamepadControl.SELECT,
        GamepadControl.START -> {
            (unit * 0.24f).coerceAtLeast(88.dp) to 48.dp
        }
        else -> {
            val size = (unit * 0.13f).coerceAtLeast(48.dp)
            size to size
        }
    }
}

private fun defaultPositions(
    areaWidth: Dp,
    areaHeight: Dp,
    unit: Dp,
    isLandscape: Boolean
): Map<GamepadControl, Offset> {
    val width = areaWidth.value.coerceAtLeast(1f)
    val height = areaHeight.value.coerceAtLeast(1f)
    val button = controlSize(GamepadControl.FACE_A, unit).first.value
    val dpadStep = button * 0.98f
    val faceStep = button * 0.88f

    fun at(x: Float, y: Float): Offset = Offset(
        (x / width).coerceIn(0f, 1f),
        (y / height).coerceIn(0f, 1f)
    )

    return if (isLandscape) {
        val stickX = width * 0.14f
        val rightStickX = width * 0.86f
        val dpadX = width * 0.28f
        val faceX = width * 0.72f
        val clusterY = height * 0.60f
        val selectY = height * 0.90f

        mapOf(
            GamepadControl.LB to at(width * 0.14f, height * 0.08f),
            GamepadControl.LT to at(width * 0.30f, height * 0.08f),
            GamepadControl.RT to at(width * 0.70f, height * 0.08f),
            GamepadControl.RB to at(width * 0.86f, height * 0.08f),
            GamepadControl.LEFT_STICK to at(stickX, height * 0.55f),
            GamepadControl.RIGHT_STICK to at(rightStickX, height * 0.55f),
            GamepadControl.DPAD_UP to at(dpadX, clusterY - dpadStep),
            GamepadControl.DPAD_DOWN to at(dpadX, clusterY + dpadStep),
            GamepadControl.DPAD_LEFT to at(dpadX - dpadStep, clusterY),
            GamepadControl.DPAD_RIGHT to at(dpadX + dpadStep, clusterY),
            GamepadControl.FACE_Y to at(faceX, clusterY - faceStep),
            GamepadControl.FACE_A to at(faceX, clusterY + faceStep),
            GamepadControl.FACE_X to at(faceX - faceStep, clusterY),
            GamepadControl.FACE_B to at(faceX + faceStep, clusterY),
            GamepadControl.SELECT to at(width * 0.44f, selectY),
            GamepadControl.START to at(width * 0.56f, selectY)
        )
    } else {
        val stickX = width * 0.25f
        val rightStickX = width * 0.75f
        val dpadX = width * 0.25f
        val faceX = width * 0.75f
        val clusterY = height * 0.69f
        val selectY = height * 0.93f

        mapOf(
            GamepadControl.LB to at(width * 0.14f, height * 0.05f),
            GamepadControl.LT to at(width * 0.38f, height * 0.05f),
            GamepadControl.RT to at(width * 0.62f, height * 0.05f),
            GamepadControl.RB to at(width * 0.86f, height * 0.05f),
            GamepadControl.LEFT_STICK to at(stickX, height * 0.36f),
            GamepadControl.RIGHT_STICK to at(rightStickX, height * 0.36f),
            GamepadControl.DPAD_UP to at(dpadX, clusterY - dpadStep),
            GamepadControl.DPAD_DOWN to at(dpadX, clusterY + dpadStep),
            GamepadControl.DPAD_LEFT to at(dpadX - dpadStep, clusterY),
            GamepadControl.DPAD_RIGHT to at(dpadX + dpadStep, clusterY),
            GamepadControl.FACE_Y to at(faceX, clusterY - faceStep),
            GamepadControl.FACE_A to at(faceX, clusterY + faceStep),
            GamepadControl.FACE_X to at(faceX - faceStep, clusterY),
            GamepadControl.FACE_B to at(faceX + faceStep, clusterY),
            GamepadControl.SELECT to at(width * 0.35f, selectY),
            GamepadControl.START to at(width * 0.65f, selectY)
        )
    }
}

private fun loadPositions(
    prefs: SharedPreferences,
    key: String
): Map<GamepadControl, Offset> {
    val raw = prefs.getString(key, null) ?: return emptyMap()
    return raw.split(';').mapNotNull { entry ->
        val parts = entry.split('=')
        if (parts.size != 2) return@mapNotNull null
        val control = GamepadControl.entries.firstOrNull { it.name == parts[0] }
            ?: return@mapNotNull null
        val coordinates = parts[1].split(',')
        if (coordinates.size != 2) return@mapNotNull null
        val x = coordinates[0].toFloatOrNull() ?: return@mapNotNull null
        val y = coordinates[1].toFloatOrNull() ?: return@mapNotNull null
        control to Offset(x, y)
    }.toMap()
}

private fun savePositions(
    prefs: SharedPreferences,
    key: String,
    positions: Map<GamepadControl, Offset>
) {
    val raw = positions.entries.joinToString(";") { (control, offset) ->
        "${control.name}=${offset.x},${offset.y}"
    }
    prefs.edit().putString(key, raw).apply()
}

@Composable
private fun GamepadButton(
    pressed: Boolean,
    onBitChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    icon: ImageVector? = null,
    contentDescription: String? = null,
    shape: Shape = CircleShape
) {
    SurfaceButton(
        touchDown = { onBitChange(true) },
        touchUp = { onBitChange(false) },
        modifier = modifier.semantics {
            if (contentDescription != null) this.contentDescription = contentDescription
        },
        shape = shape,
        fill = if (pressed) OkmColors.accent() else OkmColors.card()
    ) {
        when {
            icon != null -> Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (pressed) Color.White else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(24.dp)
            )
            label != null -> Text(
                text = label,
                color = if (pressed) Color.White else MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun AnalogStick(
    value: Offset,
    onValueChange: (Offset) -> Unit,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    val ringColor = OkmColors.inset()
    val accent = OkmColors.accent()
    val knobColor = OkmColors.card()
    val backgroundColor = OkmColors.background()

    Box(
        modifier = modifier
            .semantics { this.contentDescription = contentDescription }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { position ->
                        onValueChange(position.toStickValue(size))
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        onValueChange(change.position.toStickValue(size))
                    },
                    onDragEnd = { onValueChange(Offset.Zero) },
                    onDragCancel = { onValueChange(Offset.Zero) }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = size.minDimension / 2f
            val travel = radius * 0.62f
            val knobCenter = center + Offset(value.x * travel, value.y * travel)

            drawCircle(color = ringColor, radius = radius)
            drawCircle(color = backgroundColor, radius = radius - 1.5.dp.toPx())
            drawCircle(color = accent.copy(alpha = 0.16f), radius = radius * 0.86f)
            drawCircle(color = knobColor, radius = radius * 0.30f, center = knobCenter)
            drawCircle(
                color = accent.copy(alpha = 0.85f),
                radius = radius * 0.30f,
                center = knobCenter,
                style = Stroke(width = 1.5.dp.toPx())
            )
        }
    }
}

private fun Offset.toStickValue(size: androidx.compose.ui.unit.IntSize): Offset {
    if (size.width == 0 || size.height == 0) return Offset.Zero
    val center = Offset(size.width / 2f, size.height / 2f)
    val travel = min(size.width, size.height) / 2f * 0.62f
    val delta = this - center
    val distance = delta.getDistance()
    val clamped = if (distance > travel && distance > 0f) {
        delta * (travel / distance)
    } else {
        delta
    }
    return Offset(
        (clamped.x / travel).coerceIn(-1f, 1f),
        (clamped.y / travel).coerceIn(-1f, 1f)
    )
}

private fun setBit(buttons: Int, bit: Int, down: Boolean): Int {
    return if (down) buttons or bit else buttons and bit.inv()
}

private fun gamepadReport(buttons: Int, left: Offset, right: Offset): ByteArray {
    return byteArrayOf(
        (buttons and 0xFF).toByte(),
        ((buttons shr 8) and 0xFF).toByte(),
        axisByte(left.x),
        axisByte(left.y),
        axisByte(right.x),
        axisByte(right.y)
    )
}

private fun neutralGamepadReport(): ByteArray {
    return byteArrayOf(0.toByte(), 0.toByte(), 128.toByte(), 128.toByte(), 128.toByte(), 128.toByte())
}

private fun axisByte(value: Float): Byte {
    return ((value.coerceIn(-1f, 1f) * 127f).roundToInt() + 128)
        .coerceIn(0, 255)
        .toByte()
}
