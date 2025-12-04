package com.munchmatch.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<FoodItem>)

    @Query("SELECT COUNT(*) FROM food_items")
    suspend fun count(): Int

    @Query(
        "SELECT * FROM food_items " +
                "WHERE (:category IS NULL OR category = :category) " +
                "AND (:query IS NULL OR name LIKE '%' || :query || '%') " +
                "ORDER BY name ASC"
    )
    fun items(category: String?, query: String?): Flow<List<FoodItem>>
}
