package com.spread.business.main

import androidx.compose.foundation.Image
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spread.db.category.CategoryRepository

@Composable
fun CategorySurface() {
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
    val selected by viewModel.selectedItem.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadCategory()
    }

    Column {
        Text(text ="Category Surface")
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 80.dp)
        ) {
            category?.itemList?.withIndex()?.forEach { (index, categoryItem) ->
                item {
                    Button(
                        modifier = Modifier.padding(0.dp),
                        onClick = {
                            viewModel.selectItem(index)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selected == index) {
                                Color.Blue
                            } else {
                                Color.LightGray
                            }
                        )
                    ) {
                        Row(
                        ) {
//                        Image() {
//
//                        }
                            Text(
                                text = categoryItem.text,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }

}
