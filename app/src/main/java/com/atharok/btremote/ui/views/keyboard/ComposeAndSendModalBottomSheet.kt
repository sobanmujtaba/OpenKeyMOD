package com.atharok.btremote.ui.views.keyboard

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.atharok.btremote.R
import com.atharok.btremote.domain.entities.remoteInput.keyboard.virtualKeyboard.VirtualKeyboardLayout
import com.atharok.btremote.domain.entities.runbook.RunbookSnippet

/**
 * Compose & Send Modal Bottom Sheet (OpenKeyMOD / Openterface KM Pro feature).
 * Enables multi-line composition, clipboard pasting, typing speed control, and runbook quick-triggers.
 */
@Composable
fun ComposeAndSendModalBottomSheet(
    virtualKeyboardLayout: VirtualKeyboardLayout,
    onSendText: (text: String, shouldSendEnter: Boolean, delayMs: Long) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    KeyboardModalBottomSheet(
        onShowKeyboardBottomSheetChanged = { onDismissRequest() },
        windowInsets = WindowInsets.ime,
        modifier = modifier
    ) {
        val clipboardManager = LocalClipboardManager.current
        var textContent by remember { mutableStateOf("") }
        var autoSendEnter by remember { mutableStateOf(true) }
        var delayMillis by remember { mutableFloatStateOf(25f) }
        val snippets = remember { RunbookSnippet.defaultSnippets() }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(id = R.dimen.padding_large))
                .padding(bottom = dimensionResource(id = R.dimen.padding_large))
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Compose & Send (KM Pro)",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            clipboardManager.getText()?.text?.let { clipboardText ->
                                textContent = if (textContent.isEmpty()) clipboardText else "$textContent\n$clipboardText"
                            }
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "Paste")
                    }

                    OutlinedButton(
                        onClick = { textContent = "" },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "Clear")
                    }
                }
            }

            // Quick Runbook Snippets row
            Text(
                text = "Runbook Quick Snippets:",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                snippets.forEach { snippet ->
                    AssistChip(
                        onClick = {
                            textContent = snippet.command
                            autoSendEnter = snippet.autoEnter
                            delayMillis = snippet.delayMs.toFloat()
                        },
                        label = { Text(snippet.title) }
                    )
                }
            }

            // Text input area
            OutlinedTextField(
                value = textContent,
                onValueChange = { textContent = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                placeholder = { Text("Draft or paste text, terminal scripts, passwords, or BIOS commands...") },
                maxLines = 10,
                shape = RoundedCornerShape(12.dp)
            )

            // Auto Enter & Delay controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = autoSendEnter,
                        onCheckedChange = { autoSendEnter = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Send Enter at end",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Text(
                    text = "${delayMillis.toInt()}ms / char",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Typing speed slider
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Keystroke Delay (Higher is safer for slow BIOS / terminal buffers):",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Slider(
                    value = delayMillis,
                    onValueChange = { delayMillis = it },
                    valueRange = 5f..150f,
                    steps = 14
                )
            }

            // Send Action Button
            Button(
                onClick = {
                    if (textContent.isNotEmpty()) {
                        onSendText(textContent, autoSendEnter, delayMillis.toLong())
                        onDismissRequest()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = textContent.isNotEmpty(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = "Send to Target (${textContent.length} chars)")
            }
        }
    }
}
