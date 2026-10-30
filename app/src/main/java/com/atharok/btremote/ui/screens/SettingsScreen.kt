package com.atharok.btremote.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.atharok.btremote.R
import com.atharok.btremote.common.extensions.getAppVersion
import com.atharok.btremote.common.extensions.round
import com.atharok.btremote.common.utils.AppIcons
import com.atharok.btremote.common.utils.SOURCE_CODE_LINK
import com.atharok.btremote.common.utils.WEB_SITE_LINK
import com.atharok.btremote.common.utils.isDynamicColorsAvailable
import com.atharok.btremote.domain.entities.RemoteNavigationEntity
import com.atharok.btremote.domain.entities.remoteInput.PhysicalVolumeButtonAction
import com.atharok.btremote.domain.entities.remoteInput.keyboard.KeyboardLanguage
import com.atharok.btremote.domain.entities.settings.AccentColorEntity
import com.atharok.btremote.domain.entities.settings.AppearanceSettings
import com.atharok.btremote.domain.entities.settings.RemoteSettings
import com.atharok.btremote.domain.entities.settings.ThemeEntity
import com.atharok.btremote.presentation.viewmodel.SettingsViewModel
import com.atharok.btremote.ui.components.AppScaffold
import com.atharok.btremote.ui.components.FadeAnimatedContent
import com.atharok.btremote.ui.components.ListDialog
import com.atharok.btremote.ui.components.NavigateUpIconButton
import com.atharok.btremote.ui.components.OkmGlassCard
import com.atharok.btremote.ui.components.OkmGlassDivider
import com.atharok.btremote.ui.components.OpenKeyModSegmentedControl
import com.atharok.btremote.ui.components.TemplateDialog
import com.atharok.btremote.ui.components.TextLarge
import com.atharok.btremote.ui.components.TextNormal
import com.atharok.btremote.ui.components.okmSwitchColors
import com.atharok.btremote.ui.theme.OkmGlass
import com.atharok.btremote.ui.theme.accentColors
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    navigateUp: () -> Unit,
    navigateToThirdLibrariesScreen: () -> Unit,
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel = koinViewModel(),
    context: Context = LocalContext.current,
    uriHandler: UriHandler = LocalUriHandler.current
) {
    val appearanceSettings by settingsViewModel
        .appearanceSettingsFlow.collectAsStateWithLifecycle(AppearanceSettings())

    val remoteSettings by settingsViewModel
        .remoteSettingsFlow.collectAsStateWithLifecycle(RemoteSettings())

    val showRemoteButtonsInNotification: Boolean by settingsViewModel.showRemoteButtonsInNotificationFlow.collectAsStateWithLifecycle(initialValue = false)
    val hideBluetoothActivationButton: Boolean by settingsViewModel.hideBluetoothActivationButtonFlow.collectAsStateWithLifecycle(initialValue = false)
    var showAccentColorDialog by remember { mutableStateOf(false) }

    AppScaffold(
        title = stringResource(id = R.string.settings),
        modifier = modifier,
        navigateUp = {
            NavigateUpIconButton(navigateUp)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            if(showAccentColorDialog) {
                AccentColorDialog(
                    current = appearanceSettings.accentColor,
                    onDismiss = { showAccentColorDialog = false },
                    onSelect = { settingsViewModel.setAccentColor(it) }
                )
            }

            SettingsSection(title = stringResource(id = R.string.appearance)) {
                SettingsGroup {
                    SettingsListRow(
                        title = R.string.theme,
                        value = appearanceSettings.theme,
                        onValueChange = { settingsViewModel.changeTheme(it) },
                        items = ThemeEntity.entries,
                        convertValueToString = { context.getString(it.stringRes) }
                    )

                    SettingsDivider()

                    SettingsAccentRow(
                        accentColor = appearanceSettings.accentColor,
                        onClick = { showAccentColorDialog = true }
                    )

                    SettingsDivider()

                    SettingsToggleRow(
                        title = stringResource(id = R.string.theme_black),
                        summary = stringResource(id = R.string.theme_black_oled_info),
                        checked = appearanceSettings.useBlackColorForDarkTheme,
                        onCheckedChange = { settingsViewModel.setUseBlackColorForDarkTheme(it) }
                    )

                    if(isDynamicColorsAvailable()) {
                        SettingsDivider()

                        SettingsToggleRow(
                            title = stringResource(id = R.string.dynamic_colors),
                            checked = appearanceSettings.useDynamicColors,
                            onCheckedChange = { settingsViewModel.setUseDynamicColors(it) }
                        )
                    }

                    SettingsDivider()

                    SettingsToggleRow(
                        title = stringResource(id = R.string.full_screen),
                        checked = appearanceSettings.useFullScreen,
                        onCheckedChange = { settingsViewModel.saveUseFullScreen(it) }
                    )
                }
            }

            SettingsSection(title = stringResource(id = R.string.remote)) {
                SettingsGroup {
                    SettingsToggleRow(
                        title = stringResource(id = R.string.use_minimalist_interface),
                        summary = stringResource(id = R.string.minimalist_interface_info),
                        checked = remoteSettings.useMinimalistRemote,
                        onCheckedChange = { settingsViewModel.saveUseMinimalistRemote(it) }
                    )

                    SettingsDivider()

                    SettingsToggleRow(
                        title = stringResource(id = R.string.use_mouse_navigation_by_default),
                        checked = remoteSettings.useMouseNavigationByDefault,
                        onCheckedChange = { settingsViewModel.saveUseMouseNavigationByDefault(it) }
                    )

                    SettingsDivider()

                    SettingsNavigationRow(
                        remoteNavigation = remoteSettings.remoteNavigationEntity,
                        onRemoteNavigationChange = { settingsViewModel.saveRemoteNavigation(it) }
                    )

                    SettingsDivider()

                    SettingsToggleRow(
                        title = stringResource(id = R.string.use_enter_for_selection_title),
                        summary = stringResource(id = R.string.use_enter_for_selection_summary),
                        checked = remoteSettings.useEnterForSelection,
                        onCheckedChange = { settingsViewModel.saveUseEnterForSelection(it) }
                    )

                    SettingsDivider()

                    SettingsListRow(
                        title = R.string.assign_action_volume_up_button,
                        value = remoteSettings.physicalVolumeUpButtonAction,
                        onValueChange = { settingsViewModel.savePhysicalVolumeUpButtonAction(it) },
                        items = PhysicalVolumeButtonAction.entries,
                        convertValueToString = { context.getString(it.stringRes) }
                    )

                    SettingsDivider()

                    SettingsListRow(
                        title = R.string.assign_action_volume_down_button,
                        value = remoteSettings.physicalVolumeDownButtonAction,
                        onValueChange = { settingsViewModel.savePhysicalVolumeDownButtonAction(it) },
                        items = PhysicalVolumeButtonAction.entries,
                        convertValueToString = { context.getString(it.stringRes) }
                    )
                }
            }

            SettingsSection(title = stringResource(id = R.string.mouse)) {
                SettingsGroup {
                    SettingsSliderRow(
                        title = stringResource(id = R.string.mouse_pointer_speed),
                        valueLabel = "x${remoteSettings.mouseSpeed}",
                        value = remoteSettings.mouseSpeed,
                        onValueChange = { settingsViewModel.saveMouseSpeed(it.round(2)) }
                    )

                    SettingsDivider()

                    SettingsSliderRow(
                        title = stringResource(id = R.string.mouse_scroll_speed),
                        valueLabel = "x${remoteSettings.mouseScrollSpeed}",
                        value = remoteSettings.mouseScrollSpeed,
                        onValueChange = { settingsViewModel.saveMouseScrollSpeed(it.round(2)) }
                    )

                    SettingsDivider()

                    SettingsToggleRow(
                        title = stringResource(id = R.string.invert_mouse_scrolling_direction),
                        checked = remoteSettings.shouldInvertMouseScrollingDirection,
                        onCheckedChange = { settingsViewModel.saveInvertMouseScrollingDirection(it) }
                    )

                    SettingsDivider()

                    SettingsToggleRow(
                        title = stringResource(id = R.string.use_the_gyroscope_to_control_the_mouse),
                        checked = remoteSettings.useGyroscope,
                        onCheckedChange = { settingsViewModel.saveUseGyroscope(it) }
                    )
                }
            }

            SettingsSection(title = stringResource(id = R.string.keyboard_and_input_field)) {
                SettingsGroup {
                    SettingsListRow(
                        title = R.string.keyboard_language,
                        dialogMessage = stringResource(id = R.string.keyboard_language_info),
                        value = remoteSettings.keyboardLanguage,
                        onValueChange = { settingsViewModel.changeKeyboardLanguage(it) },
                        items = KeyboardLanguage.entries.sortedBy { context.getString(it.language) },
                        convertValueToString = { context.getString(it.language) }
                    )

                    SettingsDivider()

                    SettingsToggleRow(
                        title = stringResource(id = R.string.advanced_keyboard),
                        checked = remoteSettings.useAdvancedKeyboard,
                        onCheckedChange = { settingsViewModel.saveUseAdvancedKeyboard(it) }
                    )

                    FadeAnimatedContent(
                        targetState = remoteSettings.useAdvancedKeyboard
                    ) {
                        if(it) {
                            Column {
                                SettingsDivider()
                                SettingsToggleRow(
                                    title = stringResource(id = R.string.integrate_advanced_keyboard_into_the_view),
                                    checked = remoteSettings.useAdvancedKeyboardIntegrated,
                                    onCheckedChange = { value -> settingsViewModel.saveUseAdvancedKeyboardIntegrated(value) }
                                )
                            }
                        } else {
                            Column {
                                SettingsDivider()
                                SettingsToggleRow(
                                    title = stringResource(id = R.string.clear_input_field),
                                    checked = remoteSettings.mustClearInputField,
                                    onCheckedChange = { value -> settingsViewModel.saveMustClearInputField(value) }
                                )
                            }
                        }
                    }
                }
            }

            SettingsSection(title = stringResource(id = R.string.advanced_options)) {
                SettingsGroup {
                    SettingsToggleRow(
                        title = stringResource(id = R.string.show_remote_buttons_in_notification),
                        checked = showRemoteButtonsInNotification,
                        onCheckedChange = { settingsViewModel.saveShowRemoteButtonsInNotification(it) }
                    )

                    SettingsDivider()

                    SettingsToggleRow(
                        title = stringResource(id = R.string.hide_bluetooth_activation_button),
                        checked = hideBluetoothActivationButton,
                        onCheckedChange = { settingsViewModel.saveHideBluetoothActivationButton(it) }
                    )
                }
            }

            SettingsSection(title = stringResource(id = R.string.about)) {
                SettingsGroup {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val activity = LocalActivity.current
                        SettingsActionRow(
                            title = stringResource(id = R.string.language),
                            onClick = {
                                activity?.startActivity(
                                    Intent(
                                        Settings.ACTION_APP_LOCALE_SETTINGS,
                                        Uri.fromParts("package", activity.packageName, null)
                                    )
                                )
                            }
                        )
                        SettingsDivider()
                    }

                    SettingsActionRow(
                        title = stringResource(id = R.string.third_party_library),
                        onClick = navigateToThirdLibrariesScreen
                    )

                    SettingsDivider()

                    SettingsActionRow(
                        title = stringResource(id = R.string.github),
                        value = stringResource(id = R.string.okm_eyebrow),
                        onClick = { uriHandler.openUri(SOURCE_CODE_LINK) },
                        external = true
                    )

                    SettingsDivider()

                    SettingsActionRow(
                        title = stringResource(id = R.string.linkedin),
                        value = "Soban Mujtaba",
                        onClick = { uriHandler.openUri(WEB_SITE_LINK) },
                        external = true
                    )

                    SettingsDivider()

                    SettingsInfoRow(
                        title = stringResource(id = R.string.developed_by),
                        value = "Soban Mujtaba"
                    )

                    SettingsDivider()

                    SettingsInfoRow(
                        title = stringResource(id = R.string.code_version, context.getAppVersion()),
                        value = ""
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsAccentRow(
    accentColor: AccentColorEntity,
    onClick: () -> Unit
) {
    val isDark = OkmGlass.isDark()
    val palette = OkmGlass.palette()

    SettingsRowLayout(
        title = stringResource(id = R.string.accent_color),
        summary = stringResource(id = R.string.accent_color_info),
        onClick = onClick
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(accentColors(accentColor, isDark).accentFill)
                    .border(1.dp, palette.border, CircleShape)
            )
            Text(
                text = stringResource(id = accentColor.stringRes),
                color = palette.secondaryText,
                fontSize = 14.sp,
                lineHeight = 18.sp,
                maxLines = 1
            )
            Icon(
                imageVector = AppIcons.Right,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = palette.secondaryText
            )
        }
    }
}

@Composable
private fun AccentColorDialog(
    current: AccentColorEntity,
    onDismiss: () -> Unit,
    onSelect: (AccentColorEntity) -> Unit
) {
    var selected by remember(current) { mutableStateOf(current) }
    val isDark = OkmGlass.isDark()
    val palette = OkmGlass.palette()

    TemplateDialog(
        title = {
            TextLarge(text = stringResource(id = R.string.accent_color))
        },
        content = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                AccentColorEntity.entries.forEach { accentColor ->
                    val isSelected = accentColor == selected

                    Surface(
                        onClick = { selected = accentColor },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = if(isSelected) palette.accent.copy(alpha = 0.12f) else Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 11.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(accentColors(accentColor, isDark).accentFill)
                                    .border(1.dp, palette.border, CircleShape)
                            )
                            TextNormal(
                                text = stringResource(id = accentColor.stringRes),
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            if(isSelected) {
                                Icon(
                                    imageVector = AppIcons.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = palette.accent
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButtonText = stringResource(id = android.R.string.ok),
        onConfirmation = {
            onSelect(selected)
            onDismiss()
        },
        dismissButtonText = stringResource(id = android.R.string.cancel),
        onDismissRequest = onDismiss
    )
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title.uppercase(),
            modifier = Modifier.padding(start = 4.dp),
            color = OkmGlass.palette().secondaryText,
            fontSize = 11.sp,
            lineHeight = 14.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.1.sp
        )
        content()
    }
}

@Composable
private fun SettingsGroup(
    content: @Composable ColumnScope.() -> Unit
) {
    OkmGlassCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = 0.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth(), content = content)
    }
}

@Composable
private fun SettingsDivider() {
    OkmGlassDivider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp)
    )
}

@Composable
private fun SettingsToggleRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    summary: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .heightIn(min = 56.dp)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingsRowText(
            title = title,
            summary = summary,
            modifier = Modifier.weight(1f)
        )

        Switch(
            checked = checked,
            onCheckedChange = null,
            colors = okmSwitchColors()
        )
    }
}

@Composable
private fun SettingsValueRow(
    title: String,
    value: String,
    onClick: () -> Unit,
    summary: String? = null
) {
    SettingsRowLayout(
        title = title,
        summary = summary,
        onClick = onClick
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value,
                color = OkmGlass.palette().secondaryText,
                fontSize = 14.sp,
                lineHeight = 18.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Icon(
                imageVector = AppIcons.Right,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = OkmGlass.palette().secondaryText
            )
        }
    }
}

@Composable
private fun SettingsActionRow(
    title: String,
    onClick: () -> Unit,
    value: String? = null,
    summary: String? = null,
    external: Boolean = false
) {
    SettingsRowLayout(
        title = title,
        summary = summary,
        onClick = onClick
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (value != null) {
                Text(
                    text = value,
                    color = OkmGlass.palette().secondaryText,
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                imageVector = if (external) AppIcons.OpenInBrowser else AppIcons.Right,
                contentDescription = null,
                modifier = Modifier.size(if (external) 16.dp else 18.dp),
                tint = OkmGlass.palette().secondaryText
            )
        }
    }
}

@Composable
private fun SettingsInfoRow(
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingsRowText(
            title = title,
            summary = null,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            color = OkmGlass.palette().secondaryText,
            fontSize = 14.sp,
            lineHeight = 18.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun SettingsRowLayout(
    title: String,
    summary: String?,
    onClick: () -> Unit,
    trailing: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .heightIn(min = 56.dp)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingsRowText(
            title = title,
            summary = summary,
            modifier = Modifier.weight(1f)
        )
        trailing()
    }
}

@Composable
private fun SettingsRowText(
    title: String,
    summary: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 16.sp,
            lineHeight = 21.sp,
            fontWeight = FontWeight.Medium
        )
        if (summary != null) {
            Text(
                text = summary,
                color = OkmGlass.palette().secondaryText,
                fontSize = 13.sp,
                lineHeight = 17.sp
            )
        }
    }
}

@Composable
private fun SettingsSliderRow(
    title: String,
    valueLabel: String,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                lineHeight = 21.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = valueLabel,
                color = OkmGlass.palette().accent,
                fontSize = 14.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Slider(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.padding(top = 4.dp),
            valueRange = 0.25f..5f,
            steps = 18,
            colors = SliderDefaults.colors(
                thumbColor = OkmGlass.palette().accent,
                activeTrackColor = OkmGlass.palette().accent,
                inactiveTrackColor = OkmGlass.palette().accent.copy(alpha = 0.18f),
                activeTickColor = Color.Transparent,
                inactiveTickColor = Color.Transparent
            )
        )
    }
}

@Composable
private fun SettingsNavigationRow(
    remoteNavigation: RemoteNavigationEntity,
    onRemoteNavigationChange: (RemoteNavigationEntity) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(id = R.string.navigation_mode),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 16.sp,
            lineHeight = 21.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = stringResource(id = remoteNavigation.description),
            color = OkmGlass.palette().secondaryText,
            fontSize = 13.sp,
            lineHeight = 17.sp
        )
        OpenKeyModSegmentedControl(
            options = RemoteNavigationEntity.entries,
            selected = remoteNavigation,
            label = {
                when(it) {
                    RemoteNavigationEntity.D_PAD -> stringResource(id = R.string.d_pad)
                    RemoteNavigationEntity.TOUCHPAD -> stringResource(id = R.string.touchpad)
                }
            },
            onSelected = onRemoteNavigationChange
        )
    }
}

@Composable
private fun <T> SettingsListRow(
    @StringRes title: Int,
    value: T,
    onValueChange: (T) -> Unit,
    items: List<T>,
    convertValueToString: (T) -> String,
    dialogMessage: String? = null
) {
    var isShowingDialog by remember { mutableStateOf(false) }

    if(isShowingDialog) {
        ListDialog(
            confirmButtonText = stringResource(android.R.string.ok),
            dismissButtonText = stringResource(android.R.string.cancel),
            onConfirmation = { index ->
                onValueChange(items[index])
                isShowingDialog = false
            },
            onDismissRequest = { isShowingDialog = false },
            dialogTitle = stringResource(title),
            dialogMessage = dialogMessage,
            items = items.map { convertValueToString(it) },
            defaultItemIndex = items.indexOf(value)
        )
    }

    SettingsValueRow(
        title = stringResource(id = title),
        value = convertValueToString(value),
        onClick = { isShowingDialog = true }
    )
}
