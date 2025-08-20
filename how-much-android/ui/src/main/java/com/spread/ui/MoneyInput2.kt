package com.spread.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun MoneyInput2(
    modifier: Modifier = Modifier,
    inputState: MoneyInputState = rememberMoneyInputState()
) {
    Row(modifier = modifier) {
        InputKeys(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            inputState = inputState
        )
    }
}

@Composable
fun InputKeys(
    modifier: Modifier,
    inputState: MoneyInputState
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(4),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        itemsIndexed(listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)) { index, _ ->
            Key(
                modifier = Modifier,
                index = index
            )
        }
    }
}

@Composable
fun rememberMoneyInputState(
    inputExpression: String = ""
): MoneyInputState {
    return rememberSaveable(saver = MoneyInputState.Saver) {
        MoneyInputState(inputExpression)
    }
}

class MoneyInputState(
    inputExpression: String = ""
) {
    var inputExpression by mutableStateOf(inputExpression)

    companion object {
        val Saver: Saver<MoneyInputState, String> = Saver(
            save = {
                it.inputExpression
            },
            restore = {
                MoneyInputState(it)
            }
        )
    }
}

private val digitKeyMap = mapOf(
    0 to 1,
    1 to 2,
    2 to 3,
    4 to 4,
    5 to 5,
    6 to 6,
    8 to 7,
    9 to 8,
    10 to 9,
    13 to 0
)

private const val KEY_INDEX_BACKSPACE = 3
private const val KEY_INDEX_MINUS = 7
private const val KEY_INDEX_PLUS = 11
private const val KEY_INDEX_DOT = 15

private fun getKey(index: Int): Key {
    val number = digitKeyMap[index]
    if (number != null) {
        return Key.Digit(number)
    }
    return when (index) {
        KEY_INDEX_BACKSPACE -> Key.Action(KeyAction.Backspace)
        KEY_INDEX_MINUS -> Key.Action(KeyAction.Minus)
        KEY_INDEX_PLUS -> Key.Action(KeyAction.Plus)
        KEY_INDEX_DOT -> Key.Dot
        else -> Key.None
    }
}

enum class KeyAction {
    Backspace, Plus, Minus
}

sealed interface Key {
    data object None : Key
    data class Digit(val num: Int) : Key
    data object Dot : Key
    data class Action(val action: KeyAction) : Key
}

@Composable
fun Key(
    modifier: Modifier,
    index: Int
) {
    val key = getKey(index)
    Box(
        modifier = modifier
            .height(40.dp)
            .then(if (key !is Key.None) Modifier.clickable {} else Modifier),
        content = {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
            ) {
                when (key) {
                    is Key.Digit -> Text(text = key.num.toString())

                    is Key.Action -> {
                        when (key.action) {
                            KeyAction.Plus -> Text(text = "+")
                            KeyAction.Minus -> Text(text = "-")
                            KeyAction.Backspace -> Icon(
                                painter = painterResource(id = R.drawable.ic_backspace),
                                contentDescription = "Backspace"
                            )
                        }
                    }

                    Key.Dot -> Text(text = ".")
                    Key.None -> Text(text = "")
                }
            }
        }
    )
}