package com.atharok.btremote.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atharok.btremote.R
import com.atharok.btremote.common.utils.AppIcons
import com.atharok.btremote.ui.theme.OkmGlass
import com.atharok.btremote.ui.theme.contentSurface

/**
 * Shared Apple-inspired building blocks for the OpenKeyMOD surfaces.
 * These are intentionally scoped to the new tools so the upstream remote UI can
 * keep its own visual language until it is migrated deliberately.
 */
object OkmColors {

    @Composable
    fun isDark(): Boolean = OkmGlass.isDark()

    @Composable
    fun background(): Color = OkmGlass.palette().sheet

    @Composable
    fun card(): Color = OkmGlass.palette().contentStrong

    @Composable
    fun inset(): Color = OkmGlass.palette().inset

    @Composable
    fun accent(): Color = OkmGlass.palette().accent

    @Composable
    fun secondaryText(): Color = OkmGlass.palette().secondaryText

    @Composable
    fun separator(): Color = OkmGlass.palette().separator

    @Composable
    fun danger(): Color = OkmGlass.palette().danger
}

@Composable
fun okmSwitchColors(): SwitchColors {
    val palette = OkmGlass.palette()

    return SwitchDefaults.colors(
        checkedThumbColor = Color.White,
        checkedTrackColor = palette.accentFill,
        checkedBorderColor = Color.Transparent,
        checkedIconColor = Color.White,
        uncheckedThumbColor = palette.secondaryText,
        uncheckedTrackColor = palette.inset,
        uncheckedBorderColor = palette.border,
        uncheckedIconColor = palette.secondaryText,
        disabledCheckedThumbColor = Color.White.copy(alpha = 0.65f),
        disabledCheckedTrackColor = palette.accentFill.copy(alpha = 0.45f),
        disabledCheckedBorderColor = Color.Transparent,
        disabledCheckedIconColor = Color.White.copy(alpha = 0.65f),
        disabledUncheckedThumbColor = palette.secondaryText.copy(alpha = 0.50f),
        disabledUncheckedTrackColor = palette.inset.copy(alpha = 0.60f),
        disabledUncheckedBorderColor = palette.border.copy(alpha = 0.50f),
        disabledUncheckedIconColor = palette.secondaryText.copy(alpha = 0.50f)
    )
}

object OkmTypography {
    val sheetTitle = TextStyle(
        fontSize = 28.sp,
        lineHeight = 32.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = (-0.6).sp
    )

    val sheetSubtitle = TextStyle(
        fontSize = 14.sp,
        lineHeight = 19.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = (-0.1).sp
    )

    val eyebrow = TextStyle(
        fontSize = 11.sp,
        lineHeight = 14.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.1.sp
    )

    val sectionTitle = TextStyle(
        fontSize = 13.sp,
        lineHeight = 17.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = (-0.1).sp
    )

    val keyLabel = TextStyle(
        fontSize = 22.sp,
        lineHeight = 26.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = (-0.3).sp
    )

    val keyLabelCompact = TextStyle(
        fontSize = 14.sp,
        lineHeight = 17.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = (-0.1).sp
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpenKeyModBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = WindowInsets(0, 0, 0, 0),
    content: @Composable ColumnScope.() -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = OkmColors.background(),
        contentColor = MaterialTheme.colorScheme.onSurface,
        contentWindowInsets = { windowInsets },
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        scrimColor = OkmGlass.palette().scrim,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 6.dp)
                    .size(width = 36.dp, height = 5.dp)
                    .background(OkmColors.separator(), CircleShape)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
            content = content
        )
    }
}

@Composable
fun OpenKeyModSheetContent(
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        content = content
    )
}

@Composable
fun OpenKeyModSheetHeader(
    eyebrow: String,
    title: String,
    subtitle: String,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = eyebrow.uppercase(),
                style = OkmTypography.eyebrow,
                color = OkmColors.accent()
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = title,
                style = OkmTypography.sheetTitle,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                style = OkmTypography.sheetSubtitle,
                color = OkmColors.secondaryText()
            )
        }

        IconButton(
            onClick = onDismissRequest,
            modifier = Modifier
                .size(34.dp)
                .background(OkmColors.inset(), CircleShape)
        ) {
            Icon(
                imageVector = AppIcons.Close,
                contentDescription = stringResource(id = R.string.close),
                modifier = Modifier.size(18.dp),
                tint = OkmColors.secondaryText()
            )
        }
    }
}

@Composable
fun OpenKeyModCard(
    modifier: Modifier = Modifier,
    contentPadding: Dp = 16.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .contentSurface(
                shape = RoundedCornerShape(20.dp),
                fill = OkmColors.card()
            )
            .padding(contentPadding),
        content = content
    )
}

@Composable
fun OpenKeyModSectionLabel(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text.uppercase(),
        style = OkmTypography.sectionTitle,
        color = OkmColors.secondaryText(),
        modifier = modifier
    )
}

@Composable
fun OpenKeyModKeycap(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    caption: String? = null,
    compact: Boolean = false,
    accent: Boolean = false,
    enabled: Boolean = true
) {
    val containerColor = when {
        !enabled -> OkmColors.inset().copy(alpha = 0.65f)
        accent -> OkmColors.accent()
        else -> OkmColors.card()
    }
    val contentColor = when {
        !enabled -> OkmColors.secondaryText()
        accent -> Color.White
        else -> MaterialTheme.colorScheme.onSurface
    }

    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .heightIn(min = 56.dp)
            .contentSurface(
                shape = shape,
                fill = containerColor,
                raised = enabled && !accent
            )
            .clip(shape)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                color = contentColor,
                style = if (compact) OkmTypography.keyLabelCompact else OkmTypography.keyLabel,
                textAlign = TextAlign.Center
            )
            if (caption != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = caption,
                    style = TextStyle(fontSize = 10.sp, lineHeight = 12.sp),
                    color = if (accent) Color.White.copy(alpha = 0.75f) else OkmColors.secondaryText()
                )
            }
        }
    }
}

@Composable
fun OpenKeyModPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp),
        enabled = enabled,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = OkmColors.accent(),
            contentColor = Color.White,
            disabledContainerColor = OkmColors.inset(),
            disabledContentColor = OkmColors.secondaryText()
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = 16.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.1).sp
            )
        )
    }
}

@Composable
fun <T> OpenKeyModSegmentedControl(
    options: List<T>,
    selected: T,
    label: @Composable (T) -> String,
    onSelected: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(OkmColors.inset(), RoundedCornerShape(13.dp))
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        options.forEach { option ->
            val isSelected = option == selected
            Surface(
                onClick = { onSelected(option) },
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp),
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) OkmColors.card() else Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurface,
                shadowElevation = 0.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = label(option),
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            OkmColors.secondaryText()
                        },
                        style = TextStyle(
                            fontSize = 13.sp,
                            lineHeight = 16.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
