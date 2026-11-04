package com.atharok.btremote.ui.views.keyboard

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atharok.btremote.R
import com.atharok.btremote.domain.entities.remoteInput.keyboard.virtualKeyboard.VirtualKeyboardLayout
import com.atharok.btremote.domain.entities.runbook.RunbookSnippet
import com.atharok.btremote.ui.components.OkmColors
import com.atharok.btremote.ui.components.OkmTypography
import com.atharok.btremote.ui.components.OpenKeyModFullScreen
import com.atharok.btremote.ui.components.OpenKeyModCard
import com.atharok.btremote.ui.components.OpenKeyModPrimaryButton
import com.atharok.btremote.ui.components.OpenKeyModSectionLabel
import com.atharok.btremote.ui.components.OpenKeyModSegmentedControl
import com.atharok.btremote.ui.components.OpenKeyModSheetContent
import com.atharok.btremote.ui.components.OpenKeyModSheetHeader
import com.atharok.btremote.ui.components.okmSwitchColors
import kotlin.math.abs

/**
 * Compose & Send (OpenKeyMOD / KeyMOD Pro).
 * Multi-line composition, clipboard handoff, snippet shortcuts, and pacing.
 */
@Composable
fun ComposeAndSendModalBottomSheet(
    virtualKeyboardLayout: VirtualKeyboardLayout,
    snippets: List<RunbookSnippet>,
    onSendText: (text: String, shouldSendEnter: Boolean, delayMs: Long) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    var textContent by remember { mutableStateOf("") }
    var autoSendEnter by remember { mutableStateOf(true) }
    var delayMillis by remember { mutableFloatStateOf(25f) }

    val speedOptions = listOf(80L, 40L, 15L)
    val selectedSpeed = speedOptions.minByOrNull { abs(it - delayMillis.toLong()) } ?: 40L
    val speedLabels = mapOf(
        80L to stringResource(id = R.string.okm_speed_slow),
        40L to stringResource(id = R.string.okm_speed_balanced),
        15L to stringResource(id = R.string.okm_speed_fast)
    )

    OpenKeyModFullScreen(
        onDismissRequest = onDismissRequest,
        windowInsets = WindowInsets.safeDrawing.union(WindowInsets.ime),
        modifier = modifier
    ) {
        OpenKeyModSheetContent {
            OpenKeyModSheetHeader(
                eyebrow = stringResource(id = R.string.okm_eyebrow),
                title = stringResource(id = R.string.okm_compose_title),
                subtitle = stringResource(id = R.string.okm_compose_subtitle),
                onDismissRequest = onDismissRequest
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    onClick = {
                        clipboardManager.getText()?.text?.let { clipboardText ->
                            textContent = if (textContent.isEmpty()) {
                                clipboardText
                            } else {
                                "$textContent\n$clipboardText"
                            }
                        }
                    },
                    shape = CircleShape,
                    color = OkmColors.card(),
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    Text(
                        text = stringResource(id = R.string.okm_paste),
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    )
                }

                Surface(
                    onClick = { textContent = "" },
                    shape = CircleShape,
                    color = OkmColors.card(),
                    contentColor = OkmColors.secondaryText()
                ) {
                    Text(
                        text = stringResource(id = R.string.okm_clear),
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            OpenKeyModCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = 14.dp
            ) {
                OutlinedTextField(
                    value = textContent,
                    onValueChange = { textContent = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.okm_compose_placeholder),
                            color = OkmColors.secondaryText()
                        )
                    },
                    minLines = 5,
                    maxLines = 10,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = OkmColors.card(),
                        unfocusedContainerColor = OkmColors.card(),
                        focusedBorderColor = OkmColors.accent().copy(alpha = 0.55f),
                        unfocusedBorderColor = OkmColors.separator().copy(alpha = 0.55f),
                        cursorColor = OkmColors.accent()
                    )
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            OpenKeyModSectionLabel(text = stringResource(id = R.string.okm_quick_snippets))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.okm_snippet_hint),
                style = OkmTypography.sheetSubtitle,
                color = OkmColors.secondaryText()
            )
            Spacer(modifier = Modifier.height(10.dp))

            if (snippets.isEmpty()) {
                Text(
                    text = stringResource(id = R.string.okm_no_snippets),
                    style = OkmTypography.sheetSubtitle,
                    color = OkmColors.secondaryText()
                )
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    snippets.forEach { snippet ->
                        Surface(
                            onClick = {
                                textContent = snippet.command
                                autoSendEnter = snippet.autoEnter
                                delayMillis = snippet.delayMs.toFloat()
                            },
                            shape = CircleShape,
                            color = OkmColors.card(),
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ) {
                            Text(
                                text = snippet.title,
                                modifier = Modifier.padding(horizontal = 15.dp, vertical = 9.dp),
                                style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            OpenKeyModCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.okm_send_enter),
                        style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    )
                    Switch(
                        checked = autoSendEnter,
                        onCheckedChange = { autoSendEnter = it },
                        colors = okmSwitchColors()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.okm_typing_speed),
                        style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    )
                    Text(
                        text = stringResource(id = R.string.okm_delay_value, delayMillis.toInt()),
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = OkmColors.accent()
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OpenKeyModSegmentedControl(
                    options = speedOptions,
                    selected = selectedSpeed,
                    label = { speedLabels[it] ?: "" },
                    onSelected = { delayMillis = it.toFloat() }
                )

                Spacer(modifier = Modifier.height(10.dp))

                Slider(
                    value = delayMillis,
                    onValueChange = { delayMillis = it },
                    valueRange = 5f..150f,
                    steps = 14,
                    colors = SliderDefaults.colors(
                        thumbColor = OkmColors.accent(),
                        activeTrackColor = OkmColors.accent(),
                        inactiveTrackColor = OkmColors.inset()
                    )
                )

                Text(
                    text = stringResource(id = R.string.okm_delay_hint),
                    style = OkmTypography.sheetSubtitle,
                    color = OkmColors.secondaryText()
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            OpenKeyModPrimaryButton(
                text = stringResource(
                    id = R.string.okm_send_to_target
                ) + " · " + pluralStringResource(
                    id = R.plurals.okm_char_count,
                    count = textContent.length,
                    textContent.length
                ),
                onClick = {
                    if (textContent.isNotEmpty()) {
                        onSendText(textContent, autoSendEnter, delayMillis.toLong())
                        onDismissRequest()
                    }
                },
                enabled = textContent.isNotEmpty()
            )

            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}
