package com.atharok.btremote.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class OkmGlassPalette(
    val backgroundTop: Color,
    val backgroundBottom: Color,
    val glowPrimary: Color,
    val glowSecondary: Color,
    val accent: Color,
    val accentFill: Color,
    val onAccentFill: Color,
    val accentSecondary: Color,
    val card: Color,
    val cardStrong: Color,
    val content: Color,
    val contentStrong: Color,
    val inset: Color,
    val sheet: Color,
    val topBar: Color,
    val border: Color,
    val highlight: Color,
    val separator: Color,
    val secondaryText: Color,
    val danger: Color,
    val scrim: Color,
    val shadowLight: Color,
    val shadowDark: Color,
    val dark: Boolean
)

object OkmGlass {

    @Composable
    @ReadOnlyComposable
    fun isDark(): Boolean = MaterialTheme.colorScheme.background.luminance() < 0.5f

    @Composable
    @ReadOnlyComposable
    fun palette(): OkmGlassPalette {
        val dark = isDark()
        val trueBlack = MaterialTheme.colorScheme.background == Color.Black
        val accent = accentColors(LocalOkmAccent.current, dark)

        return if (dark) {
            OkmGlassPalette(
                backgroundTop = if (trueBlack) Color.Black else Color(0xFF15181D),
                backgroundBottom = if (trueBlack) Color.Black else Color(0xFF181C22),
                glowPrimary = Color.Transparent,
                glowSecondary = Color.Transparent,
                accent = accent.accent,
                accentFill = accent.accentFill,
                onAccentFill = accent.onAccentFill,
                accentSecondary = accent.accent,
                card = if (trueBlack) Color.Black else Color(0xFF1D222A),
                cardStrong = if (trueBlack) Color(0xFF050608) else Color(0xFF20252E),
                content = if (trueBlack) Color.Black else Color(0xFF1D222A),
                contentStrong = if (trueBlack) Color(0xFF070809) else Color(0xFF212730),
                inset = if (trueBlack) Color.Black else Color(0xFF14181E),
                sheet = if (trueBlack) Color(0xFF050608) else Color(0xFF1B2028),
                topBar = if (trueBlack) Color.Black else Color(0xFF15181D),
                border = Color.White.copy(alpha = 0.05f),
                highlight = Color.White.copy(alpha = 0.03f),
                separator = Color.White.copy(alpha = 0.06f),
                secondaryText = Color(0xFF9BA3B0),
                danger = Color(0xFFFF453A),
                scrim = Color.Black.copy(alpha = 0.55f),
                shadowLight = Color.White,
                shadowDark = Color.Black,
                dark = true
            )
        } else {
            OkmGlassPalette(
                backgroundTop = Color(0xFFE4E9F0),
                backgroundBottom = Color(0xFFDDE3EB),
                glowPrimary = Color.Transparent,
                glowSecondary = Color.Transparent,
                accent = accent.accent,
                accentFill = accent.accentFill,
                onAccentFill = accent.onAccentFill,
                accentSecondary = accent.accent,
                card = Color(0xFFE6EBF2),
                cardStrong = Color(0xFFE9EEF5),
                content = Color(0xFFE6EBF2),
                contentStrong = Color(0xFFE9EEF5),
                inset = Color(0xFFD8DFE8),
                sheet = Color(0xFFE6EBF2),
                topBar = Color(0xFFE4E9F0),
                border = Color(0x22A9B4C4),
                highlight = Color.White.copy(alpha = 0.55f),
                separator = Color(0xFFD2D9E3),
                secondaryText = Color(0xFF5C6472),
                danger = Color(0xFFFF3B30),
                scrim = Color(0xFF0B1220).copy(alpha = 0.30f),
                shadowLight = Color.White,
                shadowDark = Color(0xFF9AA5B8),
                dark = false
            )
        }
    }

    @Composable
    @ReadOnlyComposable
    fun cardShape(): Shape = RoundedCornerShape(24.dp)

    @Composable
    @ReadOnlyComposable
    fun sheetShape(): Shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
}

@Composable
fun Modifier.glassSurface(
    shape: Shape,
    palette: OkmGlassPalette = OkmGlass.palette(),
    fill: Color = palette.card,
    shadowElevation: Dp = 0.dp,
    borderWidth: Dp = 1.dp
): Modifier {
    val shadow = if (shadowElevation > 0.dp) {
        Modifier.shadow(elevation = shadowElevation, shape = shape, clip = false)
    } else {
        Modifier
    }

    return this
        .then(shadow)
        .clip(shape)
        .background(fill)
        .background(
            Brush.verticalGradient(
                colors = listOf(palette.highlight, Color.Transparent),
                startY = 0f,
                endY = 360f
            )
        )
        .border(
            width = borderWidth,
            brush = Brush.linearGradient(
                colors = listOf(palette.highlight, palette.border, palette.border)
            ),
            shape = shape
        )
}

/**
 * Neumorphic surface: a raised element gets a soft light shadow on the top-left
 * and a darker shadow on the bottom-right, while an inset element is shaded
 * from the inside so it reads as pressed into the background.
 */
@Composable
fun Modifier.contentSurface(
    shape: Shape,
    palette: OkmGlassPalette = OkmGlass.palette(),
    fill: Color = palette.content,
    borderWidth: Dp = 0.dp,
    raised: Boolean = true
): Modifier {
    val shaded = if (raised) {
        this
            .drawBehind {
                val density = this
                val path = Path().apply {
                    addOutline(shape.createOutline(size, layoutDirection, density))
                }
                val depth = 5.dp.toPx()
                val layers = 4
                val darkAlpha = if (palette.dark) 0.30f else 0.16f
                val lightAlpha = if (palette.dark) 0.18f else 0.34f

                for (i in layers downTo 1) {
                    val t = i / layers.toFloat()
                    val offset = depth * t
                    val weight = 1f - t + 0.15f
                    translate(offset, offset) {
                        drawPath(path, palette.shadowDark.copy(alpha = darkAlpha * weight))
                    }
                    translate(-offset, -offset) {
                        drawPath(path, palette.shadowLight.copy(alpha = lightAlpha * weight))
                    }
                }
            }
            .clip(shape)
            .background(palette.contentStrong)
            .background(fill)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        palette.highlight.copy(alpha = palette.highlight.alpha * 0.6f),
                        Color.Transparent,
                        Color.Transparent
                    )
                )
            )
    } else {
        this
            .clip(shape)
            .background(palette.contentStrong)
            .background(fill)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        palette.shadowDark.copy(alpha = if (palette.dark) 0.32f else 0.16f),
                        Color.Transparent,
                        Color.Transparent,
                        palette.shadowLight.copy(alpha = if (palette.dark) 0.06f else 0.5f)
                    )
                )
            )
    }

    return if (borderWidth > 0.dp) {
        shaded.border(width = borderWidth, color = palette.border, shape = shape)
    } else {
        shaded
    }
}

@Composable
fun OkmGlassBackground(
    modifier: Modifier = Modifier
) {
    val palette = OkmGlass.palette()

    Box(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(palette.backgroundTop, palette.backgroundBottom)
                    )
                )
            }
    )
}
