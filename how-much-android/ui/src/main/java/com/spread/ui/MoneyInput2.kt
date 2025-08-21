package com.spread.ui

import androidx.compose.foundation.combinedClickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.spread.common.expression.eval
import com.spread.common.performHapticFeedback
import java.math.BigDecimal

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
                index = index,
                inputState = inputState
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
    private var _inputExpression by mutableStateOf(inputExpression)

    val inputExpression: String
        get() = _inputExpression

    val expressionValue: BigDecimal?
        get() {
            if (!regex.matches(inputExpression)) {
                return null
            }
            val value = eval(inputExpression)
            return value.takeIf { it > BigDecimal.ZERO }
        }

    val isValid: Boolean
        get() = regex matches inputExpression && eval(inputExpression) > BigDecimal.ZERO

    fun appendStr(str: String) {
        val newImpression = inputExpression + str
//        if (isValidExpression(newImpression)) {
        _inputExpression = newImpression
//        }
    }

    fun removeLastChar() {
        if (_inputExpression.isNotEmpty()) {
            _inputExpression = _inputExpression.dropLast(1)
        }
    }

    fun clear() {
        _inputExpression = ""
    }

    private fun isValidExpression(expr: String): Boolean {
        return regex matches expr
    }

    companion object {
        private val regex = Regex("""^\d+(\.\d{1,2})?([+\-*]\d+(\.\d{1,2})?)*$""")
        val Saver: Saver<MoneyInputState, String> = Saver(
            save = {
                it._inputExpression
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
private const val KEY_INDEX_MULTIPLY = 15
private const val KEY_INDEX_DOT = 14

private fun getKey(index: Int): Key {
    val number = digitKeyMap[index]
    if (number != null) {
        return Key.Digit(number)
    }
    return when (index) {
        KEY_INDEX_BACKSPACE -> Key.Action(KeyAction.Backspace)
        KEY_INDEX_MINUS -> Key.Action(KeyAction.Minus)
        KEY_INDEX_PLUS -> Key.Action(KeyAction.Plus)
        KEY_INDEX_MULTIPLY -> Key.Action(KeyAction.Multiply)
        KEY_INDEX_DOT -> Key.Dot
        else -> Key.None
    }
}

enum class KeyAction {
    Backspace, Plus, Minus, Multiply
}

sealed interface Key {

    val str: String get() = ""

    data object None : Key
    data class Digit(val num: Int) : Key {
        override val str: String get() = num.toString()
    }

    data object Dot : Key {
        override val str: String get() = "."
    }

    data class Action(val action: KeyAction) : Key {
        override val str: String
            get() = when (action) {
                KeyAction.Minus -> "-"
                KeyAction.Plus -> "+"
                KeyAction.Multiply -> "*"
                KeyAction.Backspace -> super.str
            }
    }
}

@Composable
fun Key(
    modifier: Modifier,
    index: Int,
    inputState: MoneyInputState
) {
    val key = getKey(index)
    val context = LocalContext.current
    Box(
        modifier = modifier
            .height(40.dp)
            .then(
                if (key !is Key.None) Modifier.combinedClickable(
                    onClick = {
                        if (key is Key.Action && key.action == KeyAction.Backspace) {
                            inputState.removeLastChar()
                            return@combinedClickable
                        }
                        val str = key.str
                        if (str.isNotBlank()) {
                            inputState.appendStr(str)
                        }
                    },
                    onLongClick = {
                        if (key is Key.Action && key.action == KeyAction.Backspace) {
                            performHapticFeedback(context)
                            inputState.clear()
                        }
                    }
                ) else Modifier),
        content = {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
            ) {
                when (key) {
                    is Key.Action -> {
                        when (key.action) {
                            KeyAction.Plus, KeyAction.Minus, KeyAction.Multiply -> Text(text = key.str)
                            KeyAction.Backspace -> Icon(
                                painter = painterResource(id = R.drawable.ic_backspace),
                                contentDescription = "Backspace"
                            )
                        }
                    }

                    else -> Text(text = key.str)
                }
            }
        }
    )
}