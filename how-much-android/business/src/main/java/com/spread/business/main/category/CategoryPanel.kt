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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.spread.db.category.CategoryItem

@Composable
fun CategoryPanel(
    categories: List<CategoryItem>,
    initialCategoryName: String? = null,
    maxCount: Int = 8,
    onCategorySelected: (categoryItem: CategoryItem) -> Unit
) {
    var selectedIdx by remember {
        mutableIntStateOf(-1)
    }
    LaunchedEffect(Unit) {
        if (initialCategoryName != null) {
            val index = categories.indexOfFirst { it.text == initialCategoryName }
            if (index >= 0) {
                selectedIdx = index
            }
        }
    }
    Column {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 80.dp),
            contentPadding = PaddingValues(0.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            for (i in 0 until maxCount) {
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
                fontWeight = FontWeight.Bold,
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
            modifier = Modifier.size(24.dp),
            colorFilter = ColorFilter.tint(color)
        )
    }
}