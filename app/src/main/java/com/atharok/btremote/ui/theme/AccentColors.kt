package com.atharok.btremote.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.atharok.btremote.domain.entities.settings.AccentColorEntity

data class OkmAccentColors(
    val accent: Color,
    val accentFill: Color,
    val onAccentFill: Color = Color.White
)

val LocalOkmAccent = staticCompositionLocalOf { AccentColorEntity.GRAPHITE }

fun accentColors(accentColor: AccentColorEntity, dark: Boolean): OkmAccentColors {
    return when(accentColor) {
        AccentColorEntity.GRAPHITE -> if(dark) {
            OkmAccentColors(
                accent = Color(0xFFB4BCC8),
                accentFill = Color(0xFF5A6373)
            )
        } else {
            OkmAccentColors(
                accent = Color(0xFF5C6472),
                accentFill = Color(0xFF3F4652)
            )
        }
        AccentColorEntity.BLUE -> if(dark) {
            OkmAccentColors(
                accent = Color(0xFF6CB2FF),
                accentFill = Color(0xFF0A84FF)
            )
        } else {
            OkmAccentColors(
                accent = Color(0xFF0066CC),
                accentFill = Color(0xFF007AFF)
            )
        }
        AccentColorEntity.GREEN -> if(dark) {
            OkmAccentColors(
                accent = Color(0xFF6FD695),
                accentFill = Color(0xFF248A3D)
            )
        } else {
            OkmAccentColors(
                accent = Color(0xFF1B7A43),
                accentFill = Color(0xFF1E7A43)
            )
        }
        AccentColorEntity.TEAL -> if(dark) {
            OkmAccentColors(
                accent = Color(0xFF5FD5DE),
                accentFill = Color(0xFF0F7C86)
            )
        } else {
            OkmAccentColors(
                accent = Color(0xFF00707A),
                accentFill = Color(0xFF007C86)
            )
        }
        AccentColorEntity.ORANGE -> if(dark) {
            OkmAccentColors(
                accent = Color(0xFFFFB05C),
                accentFill = Color(0xFFC25E00)
            )
        } else {
            OkmAccentColors(
                accent = Color(0xFFA34F00),
                accentFill = Color(0xFFB25000)
            )
        }
        AccentColorEntity.PINK -> if(dark) {
            OkmAccentColors(
                accent = Color(0xFFFF8FB1),
                accentFill = Color(0xFFD81B60)
            )
        } else {
            OkmAccentColors(
                accent = Color(0xFFB0255B),
                accentFill = Color(0xFFC2185B)
            )
        }
        AccentColorEntity.PURPLE -> if(dark) {
            OkmAccentColors(
                accent = Color(0xFFC9A7FF),
                accentFill = Color(0xFF7E4FB5)
            )
        } else {
            OkmAccentColors(
                accent = Color(0xFF6B3FA0),
                accentFill = Color(0xFF7B3FB5)
            )
        }
        AccentColorEntity.RED -> if(dark) {
            OkmAccentColors(
                accent = Color(0xFFFF7B72),
                accentFill = Color(0xFFD32F2F)
            )
        } else {
            OkmAccentColors(
                accent = Color(0xFFB3261E),
                accentFill = Color(0xFFC62828)
            )
        }
    }
}
