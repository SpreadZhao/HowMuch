package com.spread.ui

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp

fun Modifier.underline(strokeWidth: Dp, color: Color) = this then Modifier.drawBehind {
    // in draw scope, you can call conversion functions directly
    val strokeWidthPx = strokeWidth.toPx()
    val width = size.width
    val height = size.height - strokeWidthPx / 2

    drawLine(
        color = color,
        start = Offset(x = 0f, y = height),
        end = Offset(x = width, y = height),
        strokeWidth = strokeWidthPx
    )
}

fun Modifier.detectZoomGesture(
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
): Modifier = pointerInput(Unit) {
    awaitEachGesture {
        var zoomAction: (() -> Unit)? = null
        do {
            val event = awaitPointerEvent(PointerEventPass.Initial)
            val zoom = event.calculateZoom() // no zoom == 1f
            if (zoom > 1f) {
                zoomAction = onZoomIn
                event.changes.forEach { it.consume() }
            } else if (zoom < 1f) {
                zoomAction = onZoomOut
                event.changes.forEach { it.consume() }
            }
            // exit when all fingers lifted up
        } while (event.changes.any { it.pressed })
        zoomAction?.invoke()
    }
}

fun Modifier.disableDrag(): Modifier = pointerInput(Unit) {
    detectDragGestures(onDrag = { _, _ -> })
}