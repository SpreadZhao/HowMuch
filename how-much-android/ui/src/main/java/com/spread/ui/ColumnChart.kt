package com.spread.ui

import android.graphics.Color
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.Fill
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.shape.Shape

private class ChartConstant {
    companion object {
        val DEFAULT_COLOR = Color.argb(255, 74, 177, 74)
    }
}

@Composable
fun ColumnChart(modifier: Modifier = Modifier, color: Int = ChartConstant.DEFAULT_COLOR) {
    val producer = remember { CartesianChartModelProducer() }
    LaunchedEffect(Unit) {
        producer.runTransaction {
            columnSeries {
                series(100, 200, 800, 400, 500, 600, 700)
            }
        }
    }
    val columnProvider = ColumnCartesianLayer.ColumnProvider.series(
        rememberLineComponent(
            fill = Fill(color),
            thickness = 8.dp,
            shape = Shape.Rectangle
        )
    )

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberColumnCartesianLayer(
                columnProvider = columnProvider,
                dataLabel = TextComponent(
                    color = if (isSystemInDarkTheme()) Color.WHITE else Color.BLACK,
                    textSizeSp = 14f
                )
            ),
            bottomAxis = HorizontalAxis.rememberBottom()
        ),
        modelProducer = producer,
        modifier = modifier
    )
}