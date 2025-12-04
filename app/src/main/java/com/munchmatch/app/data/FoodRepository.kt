package com.munchmatch.app.data

import kotlinx.coroutines.flow.Flow

class FoodRepository(private val dao: FoodDao) {
    fun items(category: String?, query: String?): Flow<List<FoodItem>> = dao.items(category, query)
}
