package com.spread.business.main.category

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spread.db.category.CategoryRepository

@Composable
fun CategorySurface(onViewModelReady: ((CategoryViewModel) -> Unit)? = null) {
    val context = LocalContext.current
    val fileRepo = remember {
        CategoryRepository(context.applicationContext).apply {
            initSync()
        }
    }
    val viewModel: CategoryViewModel = viewModel(
        factory = CategoryViewModelFactory(fileRepo)
    )
    val category by viewModel.categoryState.collectAsState()
    val selectedIdx by viewModel.selectedIdx.collectAsState(-1)

    LaunchedEffect(Unit) {
        viewModel.loadCategory()
    }

    LaunchedEffect(viewModel) {
        onViewModelReady?.invoke(viewModel)
    }

    Column {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 100.dp)
        ) {
            category?.itemList?.withIndex()?.forEach { (index, categoryItem) ->
                item(key = index) {
                    val isActive = remember {
                        mutableStateOf(selectedIdx == index)
                    }
                    CategoryTag(categoryItem, isActive) {
                        viewModel.select(index)
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryTag(item: CategoryItemModel, isActive: State<Boolean>, onClickAction: () -> Unit) {
    Button(
        modifier = Modifier.padding(0.dp),
        onClick = onClickAction,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isActive.value) {
                Color.Blue
            } else {
                Color.LightGray
            }
        )
    ) {
        Row {
            Text(
                text = "${item.icon.value} ${item.text.value}",
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}


@Composable
fun highFrequencyTags() {

}