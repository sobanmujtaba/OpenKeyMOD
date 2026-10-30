package com.atharok.btremote.ui.views.keyboard

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.atharok.btremote.ui.theme.OkmGlass

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeyboardModalBottomSheet(
    onShowKeyboardBottomSheetChanged: (Boolean) -> Unit,
    windowInsets: WindowInsets,
    modifier: Modifier = Modifier,
    content: @Composable (ColumnScope.() -> Unit)
) {
    val palette = OkmGlass.palette()

    ModalBottomSheet(
        onDismissRequest = { onShowKeyboardBottomSheetChanged(false) },
        modifier = modifier,
        sheetState = rememberModalBottomSheetState(true),
        containerColor = palette.sheet,
        contentColor = MaterialTheme.colorScheme.onSurface,
        scrimColor = palette.scrim,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        contentWindowInsets = {
            windowInsets
        }
    ) {
        content()
    }
}