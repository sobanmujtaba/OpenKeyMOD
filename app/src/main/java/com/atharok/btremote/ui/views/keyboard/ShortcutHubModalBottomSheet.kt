package com.atharok.btremote.ui.views.keyboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atharok.btremote.R
import com.atharok.btremote.common.utils.AppIcons
import com.atharok.btremote.domain.entities.runbook.RunbookSnippet
import com.atharok.btremote.domain.entities.runbook.ShortcutProfile
import com.atharok.btremote.domain.entities.runbook.ShortcutProfileCodec
import com.atharok.btremote.ui.components.OkmColors
import com.atharok.btremote.ui.components.OkmTypography
import com.atharok.btremote.ui.components.OpenKeyModFullScreen
import com.atharok.btremote.ui.components.OpenKeyModCard
import com.atharok.btremote.ui.components.OpenKeyModSectionLabel
import com.atharok.btremote.ui.components.OpenKeyModSheetContent
import com.atharok.btremote.ui.components.OpenKeyModSheetHeader
import com.atharok.btremote.ui.components.okmSwitchColors
import java.util.UUID

@Composable
fun ShortcutHubModalBottomSheet(
    profiles: List<ShortcutProfile>,
    onSaveProfiles: (List<ShortcutProfile>) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    var selectedProfileId by rememberSaveable { mutableStateOf<String?>(profiles.firstOrNull()?.id) }
    var profileDialogTarget by remember { mutableStateOf<ShortcutProfile?>(null) }
    var isProfileDialogVisible by remember { mutableStateOf(false) }
    var snippetDialogTarget by remember { mutableStateOf<RunbookSnippet?>(null) }
    var isSnippetDialogVisible by remember { mutableStateOf(false) }
    var profileToDelete by remember { mutableStateOf<ShortcutProfile?>(null) }
    var snippetToDelete by remember { mutableStateOf<RunbookSnippet?>(null) }
    var isImportDialogVisible by remember { mutableStateOf(false) }
    var feedback by remember { mutableStateOf<String?>(null) }

    val importSuccessMessage = stringResource(id = R.string.okm_import_success)
    val importInvalidMessage = stringResource(id = R.string.okm_import_invalid)
    val exportSuccessMessage = stringResource(id = R.string.okm_export_success)

    LaunchedEffect(profiles) {
        if (profiles.none { it.id == selectedProfileId }) {
            selectedProfileId = profiles.firstOrNull()?.id
        }
    }

    val selectedProfile = profiles.firstOrNull { it.id == selectedProfileId }

    OpenKeyModFullScreen(
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        OpenKeyModSheetContent {
            OpenKeyModSheetHeader(
                eyebrow = stringResource(id = R.string.okm_eyebrow),
                title = stringResource(id = R.string.okm_hub_title),
                subtitle = stringResource(id = R.string.okm_hub_subtitle),
                onDismissRequest = onDismissRequest
            )

            Spacer(modifier = Modifier.height(20.dp))

            OpenKeyModSectionLabel(text = stringResource(id = R.string.okm_profiles))
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                profiles.forEach { profile ->
                    ProfileChip(
                        name = profile.name,
                        selected = profile.id == selectedProfileId,
                        onClick = {
                            selectedProfileId = profile.id
                            feedback = null
                        }
                    )
                }

                HubActionChip(
                    label = stringResource(id = R.string.okm_new_profile),
                    icon = true,
                    onClick = {
                        profileDialogTarget = null
                        isProfileDialogVisible = true
                    }
                )
            }

            if (selectedProfile != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    HubActionChip(
                        label = stringResource(id = R.string.okm_rename),
                        onClick = {
                            profileDialogTarget = selectedProfile
                            isProfileDialogVisible = true
                        }
                    )
                    HubActionChip(
                        label = stringResource(id = R.string.delete),
                        destructive = true,
                        onClick = { profileToDelete = selectedProfile }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            OpenKeyModSectionLabel(text = stringResource(id = R.string.okm_snippets))
            Spacer(modifier = Modifier.height(10.dp))

            if (selectedProfile == null) {
                OpenKeyModCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(id = R.string.okm_no_profile),
                        style = OkmTypography.sheetSubtitle,
                        color = OkmColors.secondaryText()
                    )
                }
            } else {
                if (selectedProfile.snippets.isEmpty()) {
                    OpenKeyModCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = stringResource(id = R.string.okm_no_snippets),
                            style = OkmTypography.sheetSubtitle,
                            color = OkmColors.secondaryText()
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                selectedProfile.snippets.forEach { snippet ->
                    SnippetCard(
                        snippet = snippet,
                        onEdit = {
                            snippetDialogTarget = snippet
                            isSnippetDialogVisible = true
                        },
                        onDelete = { snippetToDelete = snippet }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                HubActionChip(
                    label = stringResource(id = R.string.okm_add_snippet),
                    icon = true,
                    onClick = {
                        snippetDialogTarget = null
                        isSnippetDialogVisible = true
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            OpenKeyModCard(modifier = Modifier.fillMaxWidth()) {
                OpenKeyModSectionLabel(text = stringResource(id = R.string.okm_import_export))
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(id = R.string.okm_import_export_hint),
                    style = OkmTypography.sheetSubtitle,
                    color = OkmColors.secondaryText()
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    HubActionChip(
                        label = stringResource(id = R.string.okm_export_json),
                        onClick = {
                            clipboardManager.setText(AnnotatedString(ShortcutProfileCodec.encode(profiles)))
                            feedback = exportSuccessMessage
                        }
                    )
                    HubActionChip(
                        label = stringResource(id = R.string.okm_import_json),
                        onClick = { isImportDialogVisible = true }
                    )
                }
                if (feedback != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = feedback.orEmpty(),
                        style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium),
                        color = OkmColors.accent()
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    if (isProfileDialogVisible) {
        ProfileNameDialog(
            title = if (profileDialogTarget == null) {
                stringResource(id = R.string.okm_new_profile)
            } else {
                stringResource(id = R.string.okm_rename)
            },
            initialName = profileDialogTarget?.name.orEmpty(),
            confirmLabel = if (profileDialogTarget == null) {
                stringResource(id = R.string.okm_create)
            } else {
                stringResource(id = R.string.okm_save)
            },
            onDismiss = { isProfileDialogVisible = false },
            onConfirm = { name ->
                val trimmedName = name.trim()
                if (trimmedName.isNotEmpty()) {
                    val target = profileDialogTarget
                    if (target == null) {
                        val newProfile = ShortcutProfile(
                            id = UUID.randomUUID().toString(),
                            name = trimmedName
                        )
                        onSaveProfiles(profiles + newProfile)
                        selectedProfileId = newProfile.id
                    } else {
                        onSaveProfiles(profiles.map {
                            if (it.id == target.id) it.copy(name = trimmedName) else it
                        })
                    }
                }
                isProfileDialogVisible = false
            }
        )
    }

    if (isSnippetDialogVisible && selectedProfile != null) {
        SnippetEditorDialog(
            initialSnippet = snippetDialogTarget ?: RunbookSnippet(
                id = UUID.randomUUID().toString(),
                title = "",
                command = ""
            ),
            isNew = snippetDialogTarget == null,
            onDismiss = { isSnippetDialogVisible = false },
            onConfirm = { snippet ->
                val target = snippetDialogTarget
                val updatedSnippets = if (target == null) {
                    selectedProfile.snippets + snippet
                } else {
                    selectedProfile.snippets.map {
                        if (it.id == target.id) snippet else it
                    }
                }
                onSaveProfiles(profiles.map {
                    if (it.id == selectedProfile.id) {
                        it.copy(snippets = updatedSnippets)
                    } else {
                        it
                    }
                })
                isSnippetDialogVisible = false
            }
        )
    }

    if (isImportDialogVisible) {
        ImportDialog(
            onDismiss = { isImportDialogVisible = false },
            onImport = { raw ->
                val imported = ShortcutProfileCodec.decode(raw)
                if (imported.isNullOrEmpty()) {
                    feedback = importInvalidMessage
                    false
                } else {
                    onSaveProfiles(imported)
                    selectedProfileId = imported.firstOrNull()?.id
                    feedback = importSuccessMessage
                    true
                }
            }
        )
    }

    profileToDelete?.let { profile ->
        DeleteDialog(
            title = stringResource(id = R.string.okm_delete_profile_title),
            message = stringResource(id = R.string.okm_delete_profile_message, profile.name),
            onDismiss = { profileToDelete = null },
            onConfirm = {
                val remaining = profiles.filterNot { it.id == profile.id }
                onSaveProfiles(remaining)
                if (selectedProfileId == profile.id) {
                    selectedProfileId = remaining.firstOrNull()?.id
                }
                profileToDelete = null
            }
        )
    }

    snippetToDelete?.let { snippet ->
        val profile = selectedProfile
        if (profile != null) {
            DeleteDialog(
                title = stringResource(id = R.string.okm_delete_snippet_title),
                message = stringResource(id = R.string.okm_delete_snippet_message, snippet.title),
                onDismiss = { snippetToDelete = null },
                onConfirm = {
                    onSaveProfiles(profiles.map {
                        if (it.id == profile.id) {
                            it.copy(snippets = it.snippets.filterNot { item -> item.id == snippet.id })
                        } else {
                            it
                        }
                    })
                    snippetToDelete = null
                }
            )
        }
    }
}

@Composable
private fun ProfileChip(
    name: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = if (selected) OkmColors.accent() else OkmColors.card(),
        contentColor = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
        border = if (selected) null else BorderStroke(1.dp, OkmColors.separator().copy(alpha = 0.65f))
    ) {
        Text(
            text = name,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp),
            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        )
    }
}

@Composable
private fun HubActionChip(
    label: String,
    onClick: () -> Unit,
    icon: Boolean = false,
    destructive: Boolean = false
) {
    val contentColor = if (destructive) OkmColors.danger() else OkmColors.accent()
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = OkmColors.card(),
        contentColor = contentColor,
        border = BorderStroke(1.dp, OkmColors.separator().copy(alpha = 0.65f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (icon) {
                Icon(
                    imageVector = AppIcons.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = contentColor
                )
            }
            Text(
                text = label,
                style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            )
        }
    }
}

@Composable
private fun SnippetCard(
    snippet: RunbookSnippet,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val meta = if (snippet.autoEnter) {
        stringResource(id = R.string.okm_snippet_meta_enter, snippet.delayMs.toInt())
    } else {
        stringResource(id = R.string.okm_snippet_meta, snippet.delayMs.toInt())
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = OkmColors.card(),
        border = BorderStroke(1.dp, OkmColors.separator().copy(alpha = 0.55f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = snippet.title,
                    modifier = Modifier.weight(1f),
                    style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = snippet.category.uppercase(),
                    style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.SemiBold),
                    color = OkmColors.secondaryText()
                )
            }
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = snippet.command,
                style = TextStyle(
                    fontSize = 13.sp,
                    lineHeight = 17.sp,
                    fontFamily = FontFamily.Monospace
                ),
                color = OkmColors.secondaryText(),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = meta,
                    style = TextStyle(fontSize = 11.sp),
                    color = OkmColors.secondaryText()
                )
                Row {
                    TextButton(
                        onClick = onEdit,
                        colors = ButtonDefaults.textButtonColors(contentColor = OkmColors.accent())
                    ) {
                        Text(stringResource(id = R.string.okm_edit))
                    }
                    TextButton(
                        onClick = onDelete,
                        colors = ButtonDefaults.textButtonColors(contentColor = OkmColors.danger())
                    ) {
                        Text(stringResource(id = R.string.delete))
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileNameDialog(
    title: String,
    initialName: String,
    confirmLabel: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember(initialName) { mutableStateOf(initialName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = OkmColors.card(),
        shape = RoundedCornerShape(26.dp),
        title = {
            Text(
                text = title,
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            )
        },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(id = R.string.okm_profile_name)) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = dialogFieldColors()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name) },
                colors = ButtonDefaults.textButtonColors(contentColor = OkmColors.accent())
            ) {
                Text(confirmLabel)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = OkmColors.secondaryText())
            ) {
                Text(stringResource(id = R.string.okm_cancel))
            }
        }
    )
}

@Composable
private fun SnippetEditorDialog(
    initialSnippet: RunbookSnippet,
    isNew: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (RunbookSnippet) -> Unit
) {
    var title by remember(initialSnippet.id) { mutableStateOf(initialSnippet.title) }
    var command by remember(initialSnippet.id) { mutableStateOf(initialSnippet.command) }
    var category by remember(initialSnippet.id) { mutableStateOf(initialSnippet.category) }
    var autoEnter by remember(initialSnippet.id) { mutableStateOf(initialSnippet.autoEnter) }
    var delayMillis by remember(initialSnippet.id) { mutableStateOf(initialSnippet.delayMs.toFloat()) }

    val canSave = title.isNotBlank() && command.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = OkmColors.card(),
        shape = RoundedCornerShape(26.dp),
        title = {
            Text(
                text = if (isNew) {
                    stringResource(id = R.string.okm_add_snippet)
                } else {
                    stringResource(id = R.string.okm_edit)
                },
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(id = R.string.okm_snippet_title)) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = dialogFieldColors()
                )
                OutlinedTextField(
                    value = command,
                    onValueChange = { command = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(id = R.string.okm_snippet_command)) },
                    minLines = 3,
                    maxLines = 6,
                    shape = RoundedCornerShape(14.dp),
                    colors = dialogFieldColors()
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(id = R.string.okm_snippet_category)) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = dialogFieldColors()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.okm_send_enter),
                        style = TextStyle(fontSize = 14.sp),
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = autoEnter,
                        onCheckedChange = { autoEnter = it },
                        colors = okmSwitchColors()
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.okm_typing_speed),
                        style = TextStyle(fontSize = 14.sp)
                    )
                    Text(
                        text = stringResource(id = R.string.okm_delay_value, delayMillis.toInt()),
                        style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
                        color = OkmColors.accent()
                    )
                }
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
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        initialSnippet.copy(
                            title = title.trim(),
                            command = command,
                            category = category.trim().ifBlank { "General" },
                            autoEnter = autoEnter,
                            delayMs = delayMillis.toLong().coerceIn(5L, 150L)
                        )
                    )
                },
                enabled = canSave,
                colors = ButtonDefaults.textButtonColors(contentColor = OkmColors.accent())
            ) {
                Text(if (isNew) stringResource(id = R.string.okm_add_snippet) else stringResource(id = R.string.okm_save))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = OkmColors.secondaryText())
            ) {
                Text(stringResource(id = R.string.okm_cancel))
            }
        }
    )
}

@Composable
private fun ImportDialog(
    onDismiss: () -> Unit,
    onImport: (String) -> Boolean
) {
    val clipboardManager = LocalClipboardManager.current
    var raw by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = OkmColors.card(),
        shape = RoundedCornerShape(26.dp),
        title = {
            Text(
                text = stringResource(id = R.string.okm_import_json),
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            )
        },
        text = {
            Column {
                Text(
                    text = stringResource(id = R.string.okm_import_dialog_hint),
                    style = OkmTypography.sheetSubtitle,
                    color = OkmColors.secondaryText()
                )
                TextButton(
                    onClick = {
                        clipboardManager.getText()?.text?.let { raw = it }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = OkmColors.accent())
                ) {
                    Text(stringResource(id = R.string.okm_paste))
                }
                OutlinedTextField(
                    value = raw,
                    onValueChange = { raw = it },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 5,
                    maxLines = 8,
                    shape = RoundedCornerShape(14.dp),
                    colors = dialogFieldColors()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (onImport(raw)) onDismiss() },
                enabled = raw.isNotBlank(),
                colors = ButtonDefaults.textButtonColors(contentColor = OkmColors.accent())
            ) {
                Text(stringResource(id = R.string.okm_import_json))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = OkmColors.secondaryText())
            ) {
                Text(stringResource(id = R.string.okm_cancel))
            }
        }
    )
}

@Composable
private fun DeleteDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = OkmColors.card(),
        shape = RoundedCornerShape(26.dp),
        title = {
            Text(
                text = title,
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            )
        },
        text = {
            Text(
                text = message,
                style = OkmTypography.sheetSubtitle,
                color = OkmColors.secondaryText()
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(contentColor = OkmColors.danger())
            ) {
                Text(stringResource(id = R.string.delete))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = OkmColors.secondaryText())
            ) {
                Text(stringResource(id = R.string.okm_cancel))
            }
        }
    )
}

@Composable
private fun dialogFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = OkmColors.card(),
    unfocusedContainerColor = OkmColors.card(),
    focusedBorderColor = OkmColors.accent().copy(alpha = 0.55f),
    unfocusedBorderColor = OkmColors.separator().copy(alpha = 0.55f),
    cursorColor = OkmColors.accent()
)
