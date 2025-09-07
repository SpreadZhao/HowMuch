package com.spread.ui

import android.graphics.Color
import android.icu.util.Calendar
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
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
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.CartesianMeasuringContext
import com.patrykandpatrick.vico.core.cartesian.axis.Axis
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.shape.Shape
import com.spread.common.dayOfWeekStr

/**
 * @param data key: x, value: y
 */
@Composable
fun ColumnChart(modifier: Modifier = Modifier, data: Map<out Number, Number>) {
    val producer = remember { CartesianChartModelProducer() }
    LaunchedEffect(data) {
        producer.runTransaction {
            columnSeries {
                series(data.map { it.key }, data.map { it.value })
            }
        }
    }
    val columnProvider = ColumnCartesianLayer.ColumnProvider.series(
        rememberLineComponent(
            fill = fill(MaterialTheme.colorScheme.primary),
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
                    textSizeSp = 7f
                )
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                valueFormatter = object : CartesianValueFormatter {
                    override fun format(
                        context: CartesianMeasuringContext,
                        value: Double,
                        verticalAxisPosition: Axis.Position.Vertical?
                    ): CharSequence {
                        val offset = value.toInt()
                        val calendar = Calendar.getInstance()
                        calendar.add(Calendar.DAY_OF_WEEK, offset)
                        return dayOfWeekStr(calendar.get(Calendar.DAY_OF_WEEK))
                    }
                }
            )
        ),
        modelProducer = producer,
        modifier = modifier
    )
}