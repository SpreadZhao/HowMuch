package com.spread.business.main

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
): ViewModel() {

    companion object {
        private const val TAG = "CategoryViewModel"
    }

    private val _categoryState = MutableStateFlow<Category?>(null)
    val categoryState: StateFlow<Category?> = _categoryState

    private val _selectedItem = MutableStateFlow<Int?>(null)
    val selectedItem: StateFlow<Int?> = _selectedItem

    fun loadCategory() {
        viewModelScope.launch(Dispatchers.IO) {
            fileRepo.readFromJsonFileAsync()
                .onSuccess { _categoryState.value = it }
                .onFailure { Log.e(TAG, "Load failed", it) }
        }
    }

    fun saveCategory(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            fileRepo.writeToJsonFileAsync(category)
                .onSuccess {
                    Log.i(TAG, "Saved successfully")
                    _categoryState.emit(category)
                }
                .onFailure { Log.e(TAG, "Save failed", it) }
        }
    }

    fun selectItem(index: Int) {
        if (index < 0 || index >= (categoryState.value?.itemList?.size ?: 0)) {
            return
        }
        _selectedItem.value = index
    }
}