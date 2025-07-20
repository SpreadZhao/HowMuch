package com.spread.business.main.category

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spread.db.category.Category
import com.spread.db.category.CategoryItem
import com.spread.db.category.CategoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val fileRepo: CategoryRepository
) : ViewModel() {

    companion object {
        private const val TAG = "CategoryViewModel"
    }

    private var _category: Category? = null

    private val _categoryState = MutableStateFlow<CategoryModel?>(null)
    val categoryState: StateFlow<CategoryModel?> = _categoryState

    private val _selectedIdx = MutableStateFlow<Int?>(null)
    val selectedIdx: StateFlow<Int?> = _selectedIdx

    /**
     * 对分类项进行原地排序，置顶的分类项排在前面，使用次数多的分类项排在前面
     * @param category 分类项
     */
    private fun sortCategory(category: Category) {
        category.itemList.sortWith(compareByDescending<CategoryItem> {
            it.setTopTS
        }.thenByDescending {
            it.usageCount
        })
    }

    suspend fun loadCategory() {
        viewModelScope.launch(Dispatchers.IO) {
            fileRepo.readFromJsonFileAsync()
                .onSuccess {
                    sortCategory(it)
                    _category = it
                    _categoryState.emit(it.toCategoryModel())
                }
                .onFailure { Log.e(TAG, "Load failed", it) }
        }.join()
    }

    suspend fun saveCategory(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            fileRepo.writeToJsonFileAsync(category)
                .onSuccess {
                    Log.i(TAG, "Saved successfully")
                    _category = category
                    _categoryState.emit(category.toCategoryModel())
                }
                .onFailure { Log.e(TAG, "Save failed", it) }
        }.join()
    }

    /**
     * 选择一个分类项
     * @param index 分类项的索引
     */
    fun select(index: Int) {
        if (index < 0 || index >= (categoryState.value?.itemList?.size ?: 0)) {
            return
        }
        _selectedIdx.value = index
    }

    /**
     * 获取选中的分类项
     * @return 选中的分类项，如果没有选中任何项，则返回null
     */
    fun getSelected(): CategoryItemModel? {
        return selectedIdx.value?.let {
            categoryState.value?.itemList?.get(it)
        }
    }

    /**
     * 置顶一个分类项
     * @param index 分类项的索引
     */
    fun setTop(index: Int) {
        if (index < 0 || index >= (categoryState.value?.itemList?.size ?: 0)) {
            return
        }
        val category = _category ?: return
        val item = category.itemList[index]
        item.setTopTS = System.currentTimeMillis()
        // move item to top
        category.itemList.remove(item)
        category.itemList.add(0, item)
        viewModelScope.launch {
            saveCategory(category)
        }
        // reset the state item to top
        val stateItem = _categoryState.value?.itemList?.removeAt(index)
        stateItem?.let {
            _categoryState.value?.itemList?.add(0, it)
        }
    }

    /**
     * 取消置顶一个分类项
     * @param index 分类项的索引
     */
    fun cancelTop(index: Int) {
        if (index < 0 || index >= (categoryState.value?.itemList?.size ?: 0)) {
            return
        }
        val category = _category ?: return
        val item = category.itemList[index]
        item.setTopTS = 0L
        // calculate the index of the item by usage count
        val idx = category.itemList.indexOfFirst {
            it.usageCount > item.usageCount
        }
        // reset item to the index
        category.itemList.remove(item)
        if (idx == -1) {
            category.itemList.add(item)
        } else {
            category.itemList.add(idx, item)
        }
        viewModelScope.launch {
            saveCategory(category)
        }
        // reset the state item to the index
        val stateItem = _categoryState.value?.itemList?.removeAt(index)
        stateItem?.let {
            _categoryState.value?.itemList?.add(idx, it)
        }
    }

    /**
     * 增加一个分类项
     * @param text 分类项的文本
     * @param icon 分类项的图标
     */
    fun add(text: String, icon: String) {
        val category = _category ?: return
        category.itemList.add(CategoryItem(text, icon))
        viewModelScope.launch {
            _categoryState.emit(category.toCategoryModel())
        }
    }

    /**
     * 删除一个分类项
     * @param index 分类项的索引
     * @return 删除的分类项，如果没有删除任何项，则返回null
     */
    fun remove(index: Int): CategoryItem? {
        if (index < 0 || index >= (categoryState.value?.itemList?.size ?: 0)) {
            return null
        }
        val category = _category ?: return null
        val item = category.itemList.removeAt(index)
        viewModelScope.launch {
            saveCategory(category)
        }
        _categoryState.value?.itemList?.removeAt(index)
        return item
    }

    /**
     * 增加一个分类项的使用次数
     *
     * 不会立刻更新排序，在再次加载时调用[sortCategory]方法进行排序
     * @param index 分类项的索引
     */
    fun increaseUsageCount(index: Int) {
        if (index < 0 || index >= (categoryState.value?.itemList?.size ?: 0)) {
            return
        }
        val category = _category ?: return
        val item = category.itemList[index]
        item.usageCount++
        viewModelScope.launch {
            saveCategory(category)
        }
    }

    /**
     * 获取前n个使用次数最多的分类项
     * @param n 前n个使用次数最多的分类项
     * @return 前n个使用次数最多的分类项，如果没有分类项，则返回空列表
     */
    fun getTopN(n: Int): List<CategoryItem> {
        val category = _category ?: return emptyList()
        sortCategory(category)
        return category.itemList.take(n)
    }
}

class TopNCategoryViewModel(
    private val fileRepo: CategoryRepository,
    private val n: Int
) : ViewModel() {
    // 仿照上面CategoryViewModel的写法
    // 但是没有保存到文件中，而是直接从CategoryViewModel中获取
    // 这样可以避免重复加载文件

    companion object {
        private const val TAG = "CategoryTopNViewModel"
    }

    private val categoryViewModel: CategoryViewModel = CategoryViewModel(fileRepo)

    private val _categoryState = MutableStateFlow<CategoryModel?>(null)
    val categoryState: StateFlow<CategoryModel?> = _categoryState

    private val _selectedIdx = MutableStateFlow<Int?>(null)
    val selectedIdx: StateFlow<Int?> = _selectedIdx

    suspend fun loadCategory() {
        categoryViewModel.loadCategory()
        val topN: MutableList<CategoryItem> = categoryViewModel.getTopN(n).toMutableList()
        _categoryState.emit(Category(topN).toCategoryModel())
    }

    fun select(index: Int) {
        if (index < 0 || index >= (categoryState.value?.itemList?.size ?: 0)) {
            return
        }
        _selectedIdx.value = index
    }
}