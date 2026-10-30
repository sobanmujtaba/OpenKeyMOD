package com.atharok.btremote.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.atharok.btremote.R
import com.atharok.btremote.ui.theme.OkmGlass

@Composable
fun MaterialButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = "",
    icon: ImageVector? = null
) {
    val palette = OkmGlass.palette()

    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = palette.accentFill,
            contentColor = palette.onAccentFill
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
    ) {
        ButtonContent(
            text = text,
            icon = icon
        )
    }
}

@Composable
fun MaterialFilledTonalButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = "",
    icon: ImageVector? = null
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp)
    ) {
        ButtonContent(
            text = text,
            icon = icon
        )
    }
}

@Composable
fun MaterialOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = "",
    icon: ImageVector? = null
) {
    val palette = OkmGlass.palette()

    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = 1.dp,
            color = palette.border
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = palette.accent
        )
    ) {
        ButtonContent(
            text = text,
            icon = icon,
            textColor = palette.accent,
            iconColor = palette.accent
        )
    }
}

@Composable
private fun ButtonContent(
    text: String = "",
    icon: ImageVector? = null,
    textColor: Color = Color.Unspecified,
    iconColor: Color = LocalContentColor.current
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,// Le texte sert de description
                modifier = Modifier.padding(0.dp),
                tint = iconColor
            )
        }

        TextNormal(
            text = text,
            color = textColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}