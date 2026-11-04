package com.atharok.btremote.ui.views.keyboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atharok.btremote.R
import com.atharok.btremote.domain.entities.remoteInput.keyboard.KeyboardKey
import com.atharok.btremote.ui.components.OkmColors
import com.atharok.btremote.ui.components.OkmTypography
import com.atharok.btremote.ui.components.OpenKeyModFullScreen
import com.atharok.btremote.ui.components.OpenKeyModCard
import com.atharok.btremote.ui.components.OpenKeyModKeycap
import com.atharok.btremote.ui.components.OpenKeyModSectionLabel
import com.atharok.btremote.ui.components.OpenKeyModSheetContent
import com.atharok.btremote.ui.components.OpenKeyModSheetHeader

@Composable
fun NumpadModalBottomSheet(
    onSendKey: (KeyboardKey) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    var lastKeyLabel by rememberSaveable { mutableStateOf<String?>(null) }

    val numLockLabel = stringResource(id = R.string.okm_num_lock)
    val backspaceLabel = stringResource(id = R.string.okm_backspace)
    val enterLabel = stringResource(id = R.string.enter)

    fun press(key: KeyboardKey, label: String) {
        onSendKey(key)
        lastKeyLabel = label
    }

    OpenKeyModFullScreen(
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        OpenKeyModSheetContent {
            OpenKeyModSheetHeader(
                eyebrow = stringResource(id = R.string.okm_eyebrow),
                title = stringResource(id = R.string.okm_numpad_title),
                subtitle = stringResource(id = R.string.okm_numpad_subtitle),
                onDismissRequest = onDismissRequest
            )

            Spacer(modifier = Modifier.height(20.dp))

            OpenKeyModCard(modifier = Modifier.fillMaxWidth()) {
                OpenKeyModSectionLabel(text = stringResource(id = R.string.okm_last_key))
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = lastKeyLabel ?: stringResource(id = R.string.okm_ready),
                    style = TextStyle(
                        fontSize = if (lastKeyLabel == null) 24.sp else 38.sp,
                        lineHeight = if (lastKeyLabel == null) 30.sp else 44.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = (-0.7).sp
                    ),
                    color = if (lastKeyLabel == null) {
                        OkmColors.secondaryText()
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                if (lastKeyLabel == null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(id = R.string.okm_nothing_sent),
                        style = OkmTypography.sheetSubtitle,
                        color = OkmColors.secondaryText()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            NumpadRow {
                NumpadKey("7", Modifier.weight(1f)) { press(KeyboardKey.KEY_KP_7, "7") }
                NumpadKey("8", Modifier.weight(1f)) { press(KeyboardKey.KEY_KP_8, "8") }
                NumpadKey("9", Modifier.weight(1f)) { press(KeyboardKey.KEY_KP_9, "9") }
                NumpadKey("÷", Modifier.weight(1f)) { press(KeyboardKey.KEY_KP_SLASH, "÷") }
            }

            Spacer(modifier = Modifier.height(10.dp))

            NumpadRow {
                NumpadKey("4", Modifier.weight(1f)) { press(KeyboardKey.KEY_KP_4, "4") }
                NumpadKey("5", Modifier.weight(1f)) { press(KeyboardKey.KEY_KP_5, "5") }
                NumpadKey("6", Modifier.weight(1f)) { press(KeyboardKey.KEY_KP_6, "6") }
                NumpadKey("×", Modifier.weight(1f)) { press(KeyboardKey.KEY_KP_ASTERISK, "×") }
            }

            Spacer(modifier = Modifier.height(10.dp))

            NumpadRow {
                NumpadKey("1", Modifier.weight(1f)) { press(KeyboardKey.KEY_KP_1, "1") }
                NumpadKey("2", Modifier.weight(1f)) { press(KeyboardKey.KEY_KP_2, "2") }
                NumpadKey("3", Modifier.weight(1f)) { press(KeyboardKey.KEY_KP_3, "3") }
                NumpadKey("−", Modifier.weight(1f)) { press(KeyboardKey.KEY_KP_MINUS, "−") }
            }

            Spacer(modifier = Modifier.height(10.dp))

            NumpadRow {
                OpenKeyModKeycap(
                    label = numLockLabel,
                    compact = true,
                    onClick = { press(KeyboardKey.KEY_NUM_LOCK, numLockLabel) },
                    modifier = Modifier.weight(1f)
                )
                NumpadKey("0", Modifier.weight(1f)) { press(KeyboardKey.KEY_KP_0, "0") }
                NumpadKey(".", Modifier.weight(1f)) { press(KeyboardKey.KEY_KP_DOT, ".") }
                NumpadKey("+", Modifier.weight(1f)) { press(KeyboardKey.KEY_KP_PLUS, "+") }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OpenKeyModKeycap(
                    label = enterLabel,
                    compact = true,
                    accent = true,
                    onClick = { press(KeyboardKey.KEY_KP_ENTER, enterLabel) },
                    modifier = Modifier.weight(3f)
                )
                OpenKeyModKeycap(
                    label = "⌫",
                    onClick = { press(KeyboardKey.KEY_BACKSPACE, backspaceLabel) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun NumpadRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

@Composable
private fun RowScope.NumpadKey(
    label: String,
    modifier: Modifier,
    onClick: () -> Unit
) {
    OpenKeyModKeycap(
        label = label,
        onClick = onClick,
        modifier = modifier
    )
}
