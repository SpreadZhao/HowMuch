package com.spread.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp

@Composable
fun EasyTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
) {
    BasicTextField(
        modifier = modifier,
        value = value,
        enabled = enabled,
        textStyle = LocalTextStyle.current.copy(
            fontSize = TextConstants.FONT_SIZE_H4,
            color = MaterialTheme.colorScheme.onSurface
        ),
        onValueChange = onValueChange,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        singleLine = true,
        decorationBox = {
            Box(
                modifier = Modifier
                    .underline(
                        strokeWidth = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    .padding(2.dp)
            ) {
                it()
            }
        }
    )
}