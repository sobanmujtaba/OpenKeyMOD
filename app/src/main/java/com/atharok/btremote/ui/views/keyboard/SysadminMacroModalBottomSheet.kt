package com.atharok.btremote.ui.views.keyboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.atharok.btremote.R
import com.atharok.btremote.domain.entities.remoteInput.keyboard.KeyboardKey
import com.atharok.btremote.domain.entities.remoteInput.keyboard.SysadminMacro
import com.atharok.btremote.ui.components.OpenKeyModBottomSheet
import com.atharok.btremote.ui.components.OpenKeyModCard
import com.atharok.btremote.ui.components.OpenKeyModKeycap
import com.atharok.btremote.ui.components.OpenKeyModSectionLabel
import com.atharok.btremote.ui.components.OpenKeyModSheetContent
import com.atharok.btremote.ui.components.OpenKeyModSheetHeader

/**
 * Shortcut sheet for system administration macros and function/navigation keys.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SysadminMacroModalBottomSheet(
    onSendMacro: (SysadminMacro) -> Unit,
    onSendKey: (ByteArray) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    OpenKeyModBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        OpenKeyModSheetContent {
            OpenKeyModSheetHeader(
                eyebrow = stringResource(id = R.string.okm_eyebrow),
                title = stringResource(id = R.string.okm_macros_title),
                subtitle = stringResource(id = R.string.okm_macros_subtitle),
                onDismissRequest = onDismissRequest
            )

            Spacer(modifier = Modifier.height(20.dp))

            OpenKeyModCard(modifier = Modifier.fillMaxWidth()) {
                OpenKeyModSectionLabel(text = stringResource(id = R.string.okm_system_macros))
                Spacer(modifier = Modifier.height(10.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    maxItemsInEachRow = 2,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SysadminMacro.values().forEach { macro ->
                        OpenKeyModKeycap(
                            label = macro.title,
                            compact = true,
                            onClick = { onSendMacro(macro) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OpenKeyModSectionLabel(text = stringResource(id = R.string.okm_function_keys))
            Spacer(modifier = Modifier.height(10.dp))

            FunctionKeyGrid { key ->
                sendKey(onSendKey, key)
            }

            Spacer(modifier = Modifier.height(16.dp))

            OpenKeyModSectionLabel(text = stringResource(id = R.string.okm_navigation_keys))
            Spacer(modifier = Modifier.height(10.dp))

            RowOfKeys(
                keys = listOf(
                    "Esc" to KeyboardKey.KEY_ESCAPE,
                    "Tab" to KeyboardKey.KEY_TAB,
                    "Del" to KeyboardKey.KEY_DELETE,
                    "PrtScn" to KeyboardKey.KEY_PRINT_SCREEN,
                    "PgUp" to KeyboardKey.KEY_PAGE_UP,
                    "PgDn" to KeyboardKey.KEY_PAGE_DOWN
                )
            ) { key ->
                sendKey(onSendKey, key)
            }
        }
    }
}

private fun sendKey(onSendKey: (ByteArray) -> Unit, key: KeyboardKey) {
    onSendKey(byteArrayOf(0x00, key.byte))
    onSendKey(byteArrayOf(0x00, 0x00))
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FunctionKeyGrid(onSend: (KeyboardKey) -> Unit) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        maxItemsInEachRow = 6,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf(
            KeyboardKey.KEY_F1,
            KeyboardKey.KEY_F2,
            KeyboardKey.KEY_F3,
            KeyboardKey.KEY_F4,
            KeyboardKey.KEY_F5,
            KeyboardKey.KEY_F6,
            KeyboardKey.KEY_F7,
            KeyboardKey.KEY_F8,
            KeyboardKey.KEY_F9,
            KeyboardKey.KEY_F10,
            KeyboardKey.KEY_F11,
            KeyboardKey.KEY_F12
        ).forEachIndexed { index, key ->
            OpenKeyModKeycap(
                label = "F${index + 1}",
                compact = true,
                onClick = { onSend(key) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RowOfKeys(
    keys: List<Pair<String, KeyboardKey>>,
    onSend: (KeyboardKey) -> Unit
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        maxItemsInEachRow = 3,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        keys.forEach { (label, key) ->
            OpenKeyModKeycap(
                label = label,
                compact = true,
                onClick = { onSend(key) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}
