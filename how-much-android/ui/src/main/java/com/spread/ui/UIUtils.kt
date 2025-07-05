package com.spread.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit

@Composable
fun Int.toDp(): Dp = with(LocalDensity.current) { this@toDp.toDp() }

@Composable
fun Float.toDp(): Dp = with(LocalDensity.current) { this@toDp.toDp() }

@Composable
fun TextUnit.toDp(): Dp = with(LocalDensity.current) { this@toDp.toDp() }
