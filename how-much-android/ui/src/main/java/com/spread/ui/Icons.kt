package com.spread.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

private var arrow_right: ImageVector? = null
private var category: ImageVector? = null

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


val Category: ImageVector
    get() {
        if (category != null) return category!!

        category = ImageVector.Builder(
            name = "Category",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000))
            ) {
                moveToRelative(260f, -520f)
                lineToRelative(220f, -360f)
                lineToRelative(220f, 360f)
                close()
                moveTo(700f, 880f)
                quadToRelative(-75f, 0f, -127.5f, -52.5f)
                reflectiveQuadTo(520f, 700f)
                reflectiveQuadToRelative(52.5f, -127.5f)
                reflectiveQuadTo(700f, 520f)
                reflectiveQuadToRelative(127.5f, 52.5f)
                reflectiveQuadTo(880f, 700f)
                reflectiveQuadToRelative(-52.5f, 127.5f)
                reflectiveQuadTo(700f, 880f)
                moveToRelative(-580f, -20f)
                verticalLineToRelative(-320f)
                horizontalLineToRelative(320f)
                verticalLineToRelative(320f)
                close()
                moveToRelative(580f, -60f)
                quadToRelative(42f, 0f, 71f, -29f)
                reflectiveQuadToRelative(29f, -71f)
                reflectiveQuadToRelative(-29f, -71f)
                reflectiveQuadToRelative(-71f, -29f)
                reflectiveQuadToRelative(-71f, 29f)
                reflectiveQuadToRelative(-29f, 71f)
                reflectiveQuadToRelative(29f, 71f)
                reflectiveQuadToRelative(71f, 29f)
                moveToRelative(-500f, -20f)
                horizontalLineToRelative(160f)
                verticalLineToRelative(-160f)
                horizontalLineTo(200f)
                close()
                moveToRelative(202f, -420f)
                horizontalLineToRelative(156f)
                lineToRelative(-78f, -126f)
                close()
                moveToRelative(298f, 340f)
            }
        }.build()

        return category!!
    }
