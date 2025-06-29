package com.spread.business.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spread.ui.InlineDatePicker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsertRecord() {
    val typeState = rememberSegmentedButtonState(
        options = listOf("Income", "Expense")
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 10.dp, vertical = 5.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Category(modifier = Modifier.fillMaxWidth(), state = typeState)
        InlineDatePicker(
            displayMode = DisplayMode.Input,
            showToggleMode = true,
        ) {

        }
    }
}

@Composable
fun Category(modifier: Modifier, state: CategoryState) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = state.categoryInputText,
            textStyle = TextStyle(fontSize = 10.sp),
            onValueChange = { state.categoryInputText = it },
            label = { Text("Category", fontSize = 10.sp) },
            modifier = Modifier.weight(1f),
            singleLine = true
        )
        Spacer(modifier = Modifier.width(10.dp))
        SingleChoiceSegmentedButton(modifier = Modifier.wrapContentWidth(), state = state)
    }
}

@Composable
fun rememberSegmentedButtonState(
    options: List<String>
): CategoryState {
    return rememberSaveable(saver = CategoryState.Saver) {
        CategoryState(options)
    }
}

class CategoryState(
    val options: List<String>,
    selectedIndex: Int = 0,
    categoryInputText: String = ""
) {
    var selectedIndex by mutableIntStateOf(selectedIndex)
    var categoryInputText by mutableStateOf(categoryInputText)

    companion object {
        val Saver: Saver<CategoryState, *> = listSaver(
            save = { listOf(it.options, it.selectedIndex, it.categoryInputText) },
            restore = {
                @Suppress("UNCHECKED_CAST")
                CategoryState(it[0] as List<String>, it[1] as Int, it[2] as String)
            }
        )
    }
}

@Composable
fun SingleChoiceSegmentedButton(
    modifier: Modifier = Modifier,
    state: CategoryState
) {
    SingleChoiceSegmentedButtonRow(modifier = modifier) {
        state.options.forEachIndexed { index, label ->
            SegmentedButton(
                modifier = Modifier.wrapContentSize(),
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = state.options.size
                ),
                onClick = { state.selectedIndex = index },
                selected = index == state.selectedIndex,
                label = { Text(text = label, fontSize = 10.sp) }
            )
        }
    }
}
