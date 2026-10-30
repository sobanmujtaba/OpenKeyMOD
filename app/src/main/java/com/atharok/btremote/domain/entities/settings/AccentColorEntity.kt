package com.atharok.btremote.domain.entities.settings

import androidx.annotation.StringRes
import com.atharok.btremote.R

enum class AccentColorEntity(@param:StringRes val stringRes: Int) {
    GRAPHITE(R.string.accent_graphite),
    BLUE(R.string.accent_blue),
    GREEN(R.string.accent_green),
    TEAL(R.string.accent_teal),
    ORANGE(R.string.accent_orange),
    PINK(R.string.accent_pink),
    PURPLE(R.string.accent_purple),
    RED(R.string.accent_red)
}
