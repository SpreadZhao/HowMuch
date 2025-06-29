package com.spread.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

private var arrow_right: ImageVector? = null

val Arrow_right: ImageVector
    get() {
        if (arrow_right != null) return arrow_right!!

        arrow_right = ImageVector.Builder(
            name = "Arrow_right",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000))
            ) {
                moveTo(400f, 680f)
                verticalLineToRelative(-400f)
                lineToRelative(200f, 200f)
                close()
            }
        }.build()

        return arrow_right!!
    }


