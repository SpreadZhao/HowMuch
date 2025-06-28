package com.spread.business.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.spread.db.category.CategoryRepository

class CategoryViewModelFactory(
    private val fileRepo: CategoryRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoryViewModel(fileRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}