package com.spread.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp

@Composable
fun EasyTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
    withClearIcon: Boolean = true
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
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .underline(
                        strokeWidth = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    .padding(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    innerTextField()
                }
                if (withClearIcon) {
                    Icon(
                        modifier = Modifier
                            .padding(start = 5.dp)
                            .clickable {
                                onValueChange("")
                            },
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear"
                    )
                }
            }
        }
    )
}