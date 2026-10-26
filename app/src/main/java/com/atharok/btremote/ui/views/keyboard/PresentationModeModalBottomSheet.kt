package com.atharok.btremote.ui.views.keyboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.atharok.btremote.R
import com.atharok.btremote.domain.entities.remoteInput.keyboard.KeyboardKey

/**
 * Presentation Remote Mode (OpenKeyMOD / Openterface Presentation Mode).
 * Large touch targets for PowerPoint, Google Slides, Keynote.
 */
@Composable
fun PresentationModeModalBottomSheet(
    onSendKey: (ByteArray) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    fun sendKeyPressAndRelease(key: KeyboardKey) {
        onSendKey(byteArrayOf(0x00, key.byte))
        onSendKey(byteArrayOf(0x00, 0x00))
    }

    KeyboardModalBottomSheet(
        onShowKeyboardBottomSheetChanged = { onDismissRequest() },
        windowInsets = WindowInsets(0, 0, 0, 0),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(id = R.dimen.padding_large))
                .padding(bottom = dimensionResource(id = R.dimen.padding_max)),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Presentation Remote Mode",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // Next & Previous Big Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilledTonalButton(
                    onClick = { sendKeyPressAndRelease(KeyboardKey.KEY_PAGE_UP) },
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "◀ Previous\n(PgUp)",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Button(
                    onClick = { sendKeyPressAndRelease(KeyboardKey.KEY_PAGE_DOWN) },
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Next ▶\n(PgDn)",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Slide Control Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { sendKeyPressAndRelease(KeyboardKey.KEY_F5) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "Start (F5)")
                }

                OutlinedButton(
                    onClick = { sendKeyPressAndRelease(KeyboardKey.KEY_ESCAPE) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "Exit (Esc)")
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { sendKeyPressAndRelease(KeyboardKey.ROW_4_KEY_05) /* 'b' */ },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "Black (B)")
                }

                OutlinedButton(
                    onClick = { sendKeyPressAndRelease(KeyboardKey.ROW_2_KEY_01) /* 'w' */ },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "White (W)")
                }
            }
        }
    }
}
