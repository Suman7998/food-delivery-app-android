package com.munchmatch.app.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "food_items",
    indices = [Index(value = ["name"]), Index(value = ["category"])]
)
data class FoodItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String,
    val price: Double,
    val imageUrl: String
)
