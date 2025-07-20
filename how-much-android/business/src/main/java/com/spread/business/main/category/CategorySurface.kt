package com.spread.business.main.category

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.spread.db.category.CategoryRepository
import java.io.File

@Composable
fun CategorySurface(
    onCategorySelected: (categoryItem: CategoryItemModel) -> Unit,
    onViewModelReady: ((CategoryViewModel) -> Unit)? = null
) {
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

    LaunchedEffect(selectedIdx) {
        viewModel.getSelected()?.let(onCategorySelected)
    }

    Column {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 80.dp),
            contentPadding = PaddingValues(0.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
            horizontalArrangement = Arrangement.spacedBy(0.dp)
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
            columns = GridCells.Adaptive(minSize = 100.dp),
            contentPadding = PaddingValues(0.dp)
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
    val scheme = MaterialTheme.colorScheme
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(),  // 点击时的水波效果
                onClick = onClickAction,
                enabled = true,
                role = null
            )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().height(80.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically)
        ) {
            val color = if (isActive.value) {
                scheme.primary
            } else {
                scheme.onSurface
            }
            CategoryItemIcon(item, color)
            Text(
                text = item.text.value,
                fontWeight = FontWeight.Bold,
                color = color,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CategoryItemIcon(categoryItem: CategoryItemModel, color: Color) {
    val context = LocalContext.current
    val drawableResId = getDrawableResId(context, categoryItem.icon.value)

    if (drawableResId != 0) {
        // 使用项目内置图标
        Image(
            painter = painterResource(id = drawableResId),
            contentDescription = categoryItem.text.value,
            modifier = Modifier.size(24.dp),
            colorFilter = ColorFilter.tint(color)
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