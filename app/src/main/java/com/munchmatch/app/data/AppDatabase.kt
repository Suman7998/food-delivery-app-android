package com.munchmatch.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [FoodItem::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun foodDao(): FoodDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "munchmatch.db"
                ).build()
                INSTANCE = instance
                prepopulateIfEmpty(instance)
                instance
            }
        }

        private fun prepopulateIfEmpty(db: AppDatabase) {
            val scope = CoroutineScope(Dispatchers.IO)
            scope.launch {
                val dao = db.foodDao()
                if (dao.count() == 0) {
                    val items = generateSeedItems()
                    dao.insertAll(items)
                }
            }
        }

        private fun generateSeedItems(): List<FoodItem> {
            val categories = listOf(
                "Pizza" to "https://images.unsplash.com/photo-1548365328-9f547fb09530",
                "Burger" to "https://images.unsplash.com/photo-1550547660-d9450f859349",
                "Coke" to "https://images.unsplash.com/photo-1511920170033-f8396924c348",
                "Cake" to "https://images.unsplash.com/photo-1542826438-bd32f43b6d09",
                "Pasta" to "https://images.unsplash.com/photo-1525755662778-989d0524087e",
                "Sushi" to "https://images.unsplash.com/photo-1546069901-ba9599a7e63c",
                "Sandwich" to "https://images.unsplash.com/photo-1540189549336-e6e99c3679fe",
                "Salad" to "https://images.unsplash.com/photo-1512621776951-a57141f2eefd",
                "Coffee" to "https://images.unsplash.com/photo-1517705008128-361805f42e86",
                "Dessert" to "https://images.unsplash.com/photo-1505250469679-203ad9ced0cb"
            )
            val list = ArrayList<FoodItem>(categories.size * 100)
            var basePrice = 99
            categories.forEach { (cat, image) ->
                repeat(100) { idx ->
                    val name = "$cat ${idx + 1}"
                    val price = (basePrice + (idx % 50) * 3).toDouble()
                    list += FoodItem(
                        name = name,
                        category = cat,
                        price = price,
                        imageUrl = "$image?w=800&auto=format&fit=crop"
                    )
                }
                basePrice += 10
            }
            return list
        }
    }
}
