package com.atharok.btremote.ui.components.customButtons

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import com.atharok.btremote.ui.components.DefaultElevatedCard
import com.atharok.btremote.ui.theme.OkmGlass
import com.atharok.btremote.ui.theme.surfaceElevationMedium
import com.atharok.btremote.ui.theme.surfaceElevationShadow

@Composable
private fun StatefulCustomButton(
    touchDown: () -> Unit,
    touchUp: () -> Unit,
    interactionSource: MutableInteractionSource,
    content: @Composable (interactionSource: MutableInteractionSource) -> Unit
) {
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    touchDown()
                }
                is PressInteraction.Release -> touchUp()
                is PressInteraction.Cancel -> touchUp()
            }
        }
    }

    content(interactionSource)
}

@Composable
private fun CustomButton(
    touchDown: () -> Unit,
    touchUp: () -> Unit,
    shape: Shape,
    interactionSource: MutableInteractionSource,
    content: @Composable () -> Unit
) {
    StatefulCustomButton(
        touchDown = touchDown,
        touchUp = touchUp,
        interactionSource = interactionSource
    ) { source ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(shape)
                .clipToBounds()
                .clickable(
                    interactionSource = source,
                    indication = LocalIndication.current,
                    onClick = {}
                ),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

@Composable
private fun pressScaleModifier(interactionSource: MutableInteractionSource): Modifier {
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.972f else 1f,
        animationSpec = tween(durationMillis = 120),
        label = "pressScale"
    )
    return Modifier.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

// ---- Surface Button ----

@Composable
fun SurfaceButton(
    touchDown: () -> Unit,
    touchUp: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    elevation: Dp = surfaceElevationMedium(),
    shadowElevation: Dp = surfaceElevationShadow(),
    fill: Color = OkmGlass.palette().contentStrong,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    DefaultElevatedCard(
        modifier = modifier.then(pressScaleModifier(interactionSource)),
        shape = shape,
        elevation = elevation,
        shadowElevation = shadowElevation,
        fill = fill
    ) {
        CustomButton(
            touchDown = touchDown,
            touchUp = touchUp,
            shape = shape,
            interactionSource = interactionSource,
            content = content
        )
    }
}

// ---- Raw Button ----

@Composable
fun RawButton(
    touchDown: () -> Unit,
    touchUp: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(modifier = modifier.then(pressScaleModifier(interactionSource))) {
        CustomButton(
            touchDown = touchDown,
            touchUp = touchUp,
            shape = shape,
            interactionSource = interactionSource,
            content = content
        )
    }
}
