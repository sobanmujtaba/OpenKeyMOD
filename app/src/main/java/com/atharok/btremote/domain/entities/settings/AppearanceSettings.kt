package com.atharok.btremote.domain.entities.settings

import com.atharok.btremote.common.utils.DEFAULT_ACCENT_COLOR
import com.atharok.btremote.common.utils.DEFAULT_THEME
import com.atharok.btremote.common.utils.DEFAULT_USE_BLACK_COLOR_FOR_DARK_THEME
import com.atharok.btremote.common.utils.DEFAULT_USE_FULL_SCREEN

data class AppearanceSettings(
    val theme: ThemeEntity = DEFAULT_THEME,
    val accentColor: AccentColorEntity = DEFAULT_ACCENT_COLOR,
    val useDynamicColors: Boolean = false,
    val useBlackColorForDarkTheme: Boolean = DEFAULT_USE_BLACK_COLOR_FOR_DARK_THEME,
    val useFullScreen: Boolean = DEFAULT_USE_FULL_SCREEN
)