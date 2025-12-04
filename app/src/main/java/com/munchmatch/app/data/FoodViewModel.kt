package com.munchmatch.app.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

class FoodViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = FoodRepository(AppDatabase.getInstance(application).foodDao())

    private val _category = MutableStateFlow<String?>(null)
    private val _query = MutableStateFlow<String?>(null)

    val categories: List<String> = listOf(
        "All",
        "Pizza",
        "Burger",
        "Coke",
        "Cake",
        "Pasta",
        "Sushi",
        "Sandwich",
        "Salad",
        "Coffee",
        "Dessert"
    )

    val selectedCategory: StateFlow<String?> = _category
    val searchQuery: StateFlow<String?> = _query

    val items: StateFlow<List<FoodItem>> = combine(_category, _query) { c, q -> c to q }
        .let { pairFlow ->
            pairFlow.flatMapLatest { (c, q) ->
                val cat = if (c == null || c == "All") null else c
                repo.items(cat, q)
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun setCategory(category: String?) { _category.value = category }
    fun setQuery(query: String?) { _query.value = query }
}
