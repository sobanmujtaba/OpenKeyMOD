package com.atharok.btremote.ui.views.keyboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atharok.btremote.R
import com.atharok.btremote.common.utils.AppIcons
import com.atharok.btremote.domain.entities.remoteInput.keyboard.KeyboardKey
import com.atharok.btremote.ui.components.OkmColors
import com.atharok.btremote.ui.components.OkmTypography
import com.atharok.btremote.ui.components.OpenKeyModFullScreen
import com.atharok.btremote.ui.components.OpenKeyModCard
import com.atharok.btremote.ui.components.OpenKeyModKeycap
import com.atharok.btremote.ui.components.OpenKeyModSectionLabel
import com.atharok.btremote.ui.components.OpenKeyModSheetContent
import com.atharok.btremote.ui.components.OpenKeyModSheetHeader
import com.atharok.btremote.ui.components.customButtons.SurfaceButton
import kotlinx.coroutines.delay
import java.util.Locale

/**
 * Presentation remote with a local rehearsal timer.
 * Sends standard PageUp/PageDown/F5/Esc/B/W keys, so it works with Keynote,
 * PowerPoint, Google Slides, and browser-based decks.
 */
@Composable
fun PresentationModeModalBottomSheet(
    onSendKey: (KeyboardKey) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    var elapsedSeconds by rememberSaveable { mutableIntStateOf(0) }
    var isRunning by rememberSaveable { mutableStateOf(false) }

    val view = LocalView.current
    DisposableEffect(view) {
        val previous = view.keepScreenOn
        view.keepScreenOn = true
        onDispose { view.keepScreenOn = previous }
    }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1_000)
            elapsedSeconds += 1
        }
    }

    OpenKeyModFullScreen(
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        OpenKeyModSheetContent {
            OpenKeyModSheetHeader(
                eyebrow = stringResource(id = R.string.okm_eyebrow),
                title = stringResource(id = R.string.okm_presentation_title),
                subtitle = stringResource(id = R.string.okm_presentation_subtitle),
                onDismissRequest = onDismissRequest
            )

            Spacer(modifier = Modifier.height(20.dp))

            OpenKeyModCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(id = R.string.okm_timer),
                        style = OkmTypography.sectionTitle,
                        color = OkmColors.secondaryText()
                    )
                    Text(
                        text = when {
                            isRunning -> stringResource(id = R.string.okm_timer_running)
                            elapsedSeconds > 0 -> stringResource(id = R.string.okm_timer_paused)
                            else -> stringResource(id = R.string.okm_ready)
                        },
                        style = TextStyle(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = if (isRunning) OkmColors.accent() else OkmColors.secondaryText()
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = formatElapsedTime(elapsedSeconds),
                    style = TextStyle(
                        fontSize = 52.sp,
                        lineHeight = 56.sp,
                        fontWeight = FontWeight.Light,
                        letterSpacing = (-1.8).sp
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TimerAction(
                        text = if (isRunning) {
                            stringResource(id = R.string.okm_pause)
                        } else {
                            stringResource(id = R.string.okm_start)
                        },
                        accent = true,
                        modifier = Modifier.weight(1f),
                        onClick = { isRunning = !isRunning }
                    )
                    TimerAction(
                        text = stringResource(id = R.string.okm_reset),
                        accent = false,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            isRunning = false
                            elapsedSeconds = 0
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OpenKeyModSectionLabel(text = stringResource(id = R.string.okm_navigation_keys))
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(28.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SlideButton(
                    icon = AppIcons.Left,
                    contentDescription = stringResource(id = R.string.okm_previous_slide),
                    onClick = { onSendKey(KeyboardKey.KEY_PAGE_UP) },
                    modifier = Modifier.size(96.dp)
                )
                SlideButton(
                    icon = AppIcons.Right,
                    contentDescription = stringResource(id = R.string.okm_next_slide),
                    accent = true,
                    onClick = { onSendKey(KeyboardKey.KEY_PAGE_DOWN) },
                    modifier = Modifier.size(96.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OpenKeyModSectionLabel(text = stringResource(id = R.string.okm_deck_controls))
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OpenKeyModKeycap(
                    label = "F5",
                    caption = stringResource(id = R.string.okm_slideshow_start),
                    onClick = { onSendKey(KeyboardKey.KEY_F5) },
                    modifier = Modifier.weight(1f)
                )
                OpenKeyModKeycap(
                    label = "Esc",
                    caption = stringResource(id = R.string.okm_slideshow_end),
                    onClick = { onSendKey(KeyboardKey.KEY_ESCAPE) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OpenKeyModKeycap(
                    label = "B",
                    caption = stringResource(id = R.string.okm_black_screen),
                    onClick = { onSendKey(KeyboardKey.ROW_4_KEY_05) },
                    modifier = Modifier.weight(1f)
                )
                OpenKeyModKeycap(
                    label = "W",
                    caption = stringResource(id = R.string.okm_white_screen),
                    onClick = { onSendKey(KeyboardKey.ROW_2_KEY_01) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SlideButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accent: Boolean = false
) {
    SurfaceButton(
        touchDown = onClick,
        touchUp = {},
        modifier = modifier,
        shape = CircleShape,
        fill = if (accent) OkmColors.accent() else OkmColors.card()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(38.dp),
            tint = if (accent) Color.White else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun TimerAction(
    text: String,
    accent: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(42.dp),
        shape = CircleShape,
        color = if (accent) OkmColors.accent() else OkmColors.inset(),
        contentColor = if (accent) Color.White else MaterialTheme.colorScheme.onSurface
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

private fun formatElapsedTime(totalSeconds: Int): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) {
        String.format(Locale.US, "%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format(Locale.US, "%02d:%02d", minutes, seconds)
    }
}
