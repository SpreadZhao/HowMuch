package com.spread.business.main.category

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
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.spread.db.category.CategoryItem
import com.spread.ui.IconConstants
import kotlinx.coroutines.flow.first

private const val MAX_ROWS = 2

@Composable
fun CategoryPanel(
    categories: List<CategoryItem>,
    initialCategoryName: String? = null,
    maxCount: Int = categories.size,
    onCategorySelected: (categoryItem: CategoryItem) -> Unit
) {
    var selectedIdx by remember {
        mutableIntStateOf(-1)
    }
    LaunchedEffect(initialCategoryName) {
        if (initialCategoryName != null) {
            val index = categories.indexOfFirst { it.text == initialCategoryName }
            selectedIdx = if (index >= 0) {
                index
            } else {
                -1
            }
        }
    }
    Column {
        val gridState = rememberLazyGridState()
        var realMaxCount by remember { mutableIntStateOf(maxCount) }
        LaunchedEffect(gridState) {
            val columns = snapshotFlow { gridState.layoutInfo.maxSpan }.first { it > 0 }
            val maxRows = (maxCount + columns - 1) / columns
            if (maxRows > MAX_ROWS) {
                // TODO: make this configurable
                realMaxCount = MAX_ROWS * columns
            }
        }
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 80.dp),
            state = gridState,
            contentPadding = PaddingValues(0.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
            userScrollEnabled = false,
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            for (i in 0 until realMaxCount) {
                if (i >= categories.size) {
                    break
                }
                item(key = i) {
                    val isActive = remember(selectedIdx) {
                        derivedStateOf { selectedIdx == i }
                    }
                    CategoryTag(categories[i], isActive.value) {
                        selectedIdx = i
                        onCategorySelected(categories[i])
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryTag(
    item: CategoryItem,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(),  // 点击时的水波效果
                onClick = onClick,
                enabled = true,
                role = null
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically)
        ) {
            val color = if (isActive) {
                scheme.primary
            } else {
                scheme.onSurface
            }
            CategoryItemIcon(item, color)
            Text(
                text = item.text,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                color = color,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CategoryItemIcon(categoryItem: CategoryItem, color: Color) {
    // TODO: default icon
    val drawableResId = iconOf(categoryItem.id) ?: return

    if (drawableResId != 0) {
        // 使用项目内置图标
        Image(
            painter = painterResource(id = drawableResId),
            contentDescription = categoryItem.text,
            modifier = Modifier.size(IconConstants.ICON_SIZE_NORMAL),
            colorFilter = ColorFilter.tint(color)
        )
    }
}