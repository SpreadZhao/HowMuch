package com.spread.business.main.category

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spread.db.category.Category
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

    fun loadCategory() {
        viewModelScope.launch(Dispatchers.IO) {
            fileRepo.readFromJsonFileAsync()
                .onSuccess {
                    _category = it
                    _categoryState.emit(it.toCategoryModel())
                }
                .onFailure { Log.e(TAG, "Load failed", it) }
        }
    }

    fun saveCategory(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            fileRepo.writeToJsonFileAsync(category)
                .onSuccess {
                    Log.i(TAG, "Saved successfully")
                    _category = category
                    _categoryState.emit(category.toCategoryModel())
                }
                .onFailure { Log.e(TAG, "Save failed", it) }
        }
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
}