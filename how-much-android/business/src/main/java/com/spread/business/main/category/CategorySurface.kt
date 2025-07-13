package com.spread.business.main.category

import android.content.Context
import androidx.compose.foundation.Image
import com.spread.business.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.spread.db.category.CategoryRepository
import java.io.File

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
            columns = GridCells.Adaptive(minSize = 80.dp),
            contentPadding = PaddingValues(horizontal = 5.dp, vertical = 5.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            category?.itemList?.withIndex()?.forEach { (index, categoryItem) ->
                item(key = index) {
                    val isActive = remember(selectedIdx) {
                        derivedStateOf { selectedIdx == index }
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
fun TopNCategorySurface(n: Int, onViewModelReady: ((TopNCategoryViewModel) -> Unit)? = null) {
    val context = LocalContext.current
    val fileRepo = remember {
        CategoryRepository(context.applicationContext).apply {
            initSync()
        }
    }
    val viewModel: TopNCategoryViewModel = viewModel(
        factory = TopNCategoryViewModelFactory(fileRepo, n)
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
                    val isActive = remember(selectedIdx) {
                        derivedStateOf { selectedIdx == index }
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
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CategoryItemIcon(item)
            Text(
                text = item.text.value,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1
            )
        }
    }
}

@Composable
fun CategoryItemIcon(categoryItem: CategoryItemModel) {
    val context = LocalContext.current
    val drawableResId = getDrawableResId(context, categoryItem.icon.value)

    if (drawableResId != 0) {
        // 使用项目内置图标
        Image(
            painter = painterResource(id = drawableResId),
            contentDescription = categoryItem.text.value,
            modifier = Modifier.size(20.dp)
        )
    } else {
        // 使用导入到应用沙箱的图标
        val file = File(context.filesDir, categoryItem.icon.value)
        if (file.exists()) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(file)
                    .build(),
                contentDescription = categoryItem.text.value,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

private fun getDrawableResId(context: Context, iconName: String): Int {
    return context.resources.getIdentifier(
        iconName,
        "drawable",
        context.packageName
    )
}