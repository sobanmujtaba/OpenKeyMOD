package com.atharok.btremote.ui.views.keyboard

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
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
import com.atharok.btremote.domain.entities.remoteInput.keyboard.SysadminMacro

/**
 * Sysadmin Macro & Function Key Modal Bottom Sheet (OpenKeyMOD).
 * Offers one-tap administrative shortcuts and function keys F1-F12.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SysadminMacroModalBottomSheet(
    onSendMacro: (SysadminMacro) -> Unit,
    onSendKey: (ByteArray) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    KeyboardModalBottomSheet(
        onShowKeyboardBottomSheetChanged = { onDismissRequest() },
        windowInsets = WindowInsets(0, 0, 0, 0),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(id = R.dimen.padding_large))
                .padding(bottom = dimensionResource(id = R.dimen.padding_max))
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "System Administration Macros",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // Macro Grid
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SysadminMacro.values().forEach { macro ->
                    FilledTonalButton(
                        onClick = {
                            onSendMacro(macro)
                        },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column {
                            Text(
                                text = macro.title,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }

            // Function Keys Row F1-F12
            Text(
                text = "Function Keys (F1 - F12):",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            val functionKeys = listOf(
                "F1" to KeyboardKey.KEY_F1,
                "F2" to KeyboardKey.KEY_F2,
                "F3" to KeyboardKey.KEY_F3,
                "F4" to KeyboardKey.KEY_F4,
                "F5" to KeyboardKey.KEY_F5,
                "F6" to KeyboardKey.KEY_F6,
                "F7" to KeyboardKey.KEY_F7,
                "F8" to KeyboardKey.KEY_F8,
                "F9" to KeyboardKey.KEY_F9,
                "F10" to KeyboardKey.KEY_F10,
                "F11" to KeyboardKey.KEY_F11,
                "F12" to KeyboardKey.KEY_F12
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                functionKeys.forEach { (label, key) ->
                    OutlinedButton(
                        onClick = {
                            onSendKey(byteArrayOf(0x00, key.byte))
                            onSendKey(byteArrayOf(0x00, 0x00))
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = label)
                    }
                }
            }

            // Special Keys Row
            Text(
                text = "Navigation & Editing:",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            val specialKeys = listOf(
                "Esc" to KeyboardKey.KEY_ESCAPE,
                "Tab" to KeyboardKey.KEY_TAB,
                "Del" to KeyboardKey.KEY_DELETE,
                "PrtScn" to KeyboardKey.KEY_PRINT_SCREEN,
                "PgUp" to KeyboardKey.KEY_PAGE_UP,
                "PgDn" to KeyboardKey.KEY_PAGE_DOWN
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                specialKeys.forEach { (label, key) ->
                    OutlinedButton(
                        onClick = {
                            onSendKey(byteArrayOf(0x00, key.byte))
                            onSendKey(byteArrayOf(0x00, 0x00))
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = label)
                    }
                }
            }
        }
    }
}
