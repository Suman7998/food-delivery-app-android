package com.munchmatch.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private enum class Category { Pizza, Burger, Coke, Cake, Other }

private data class Food(
    val id: Int,
    val name: String,
    val category: Category,
    val avgRating: Double,
    val types: List<String>,
    val reviews: List<Review>
)

private data class Review(
    val user: String,
    val stars: Int,
    val text: String,
    val type: String
)

private object MlData {
    val foods: List<Food> by lazy { generateFoods() }

    private fun generateFoods(): List<Food> {
        val seed = 42L
        val rnd = java.util.Random(seed)
        val categories = listOf(Category.Pizza, Category.Burger, Category.Coke, Category.Cake, Category.Other)
        val namesByCat = mapOf(
            Category.Pizza to listOf("Margherita", "Farmhouse", "Paneer Tikka", "Veggie Supreme", "Cheese Burst", "Mexican Wave", "Pepper Paneer", "BBQ Corn", "Spinach Corn", "Deluxe Veggie"),
            Category.Burger to listOf("Veg Classic", "Paneer Melt", "Aloo Tikki", "Crispy Veg", "Double Veggie", "Spicy Bean", "Cheese Blast", "BBQ Veg", "Mushroom Melt", "Garden Fresh"),
            Category.Coke to listOf("Classic Coke", "Diet Coke", "Coke Zero", "Cherry Coke", "Vanilla Coke"),
            Category.Cake to listOf("Chocolate Truffle", "Red Velvet", "Black Forest", "Cheesecake", "Butterscotch"),
            Category.Other to listOf("Pasta Alfredo", "Sushi Platter", "Veg Biryani", "Hakka Noodles", "Tacos Veg")
        )
        val typeSuffix = listOf("Small", "Medium", "Large", "Thin Crust", "Cheese Burst", "Extra Cheese", "Jain", "Whole Wheat")
        val reviewPhrases = listOf(
            "Amazing taste", "Too salty", "Perfectly cooked", "Would order again", "Great value", "Average", "Loved the spice", "Too oily", "Fresh ingredients", "Fast delivery"
        )
        val users = (1..300).map { "User$it" }
        val foods = mutableListOf<Food>()
        var id = 1
        while (foods.size < 100) {
            val cat = categories[id % categories.size]
            val baseNames = namesByCat[cat] ?: listOf("Item")
            val base = baseNames[id % baseNames.size]
            val types = List(4) { typeSuffix[(id + it) % typeSuffix.size] }
            val reviews = mutableListOf<Review>()
            repeat(100) { idx ->
                val user = users[(id + idx) % users.size]
                val stars = 3 + rnd.nextInt(3)
                val phrase = reviewPhrases[rnd.nextInt(reviewPhrases.size)]
                val t = types[(idx + id) % types.size]
                reviews.add(Review(user = user, stars = stars, text = phrase, type = t))
            }
            val avg = reviews.map { it.stars }.average()
            val food = Food(id = id, name = "$base $id", category = cat, avgRating = avg, types = types, reviews = reviews)
            foods.add(food)
            id++
        }
        return foods
    }
}

@Composable
fun MlFeaturesScreen(onFoodClick: (Int) -> Unit) {
    val selected = remember { mutableStateOf<Category?>(null) }
    val allFoods = MlData.foods
    val filtered = allFoods.filter { selected.value == null || it.category == selected.value }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Category.values().forEach { cat ->
                AssistChip(onClick = { selected.value = if (selected.value == cat) null else cat }, label = { Text(cat.name) })
            }
        }
        Spacer(Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(filtered, key = { it.id }) { food ->
                FoodCard(food = food, onClick = { onFoodClick(food.id) })
            }
        }
    }
}

@Composable
private fun FoodCard(food: Food, onClick: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Text(text = food.name, style = MaterialTheme.typography.titleMedium)
            Text(text = "Category: ${food.category}")
            Text(text = "Average Rating: ${String.format("%.1f", food.avgRating)} ★ out of 5")
            Text(text = "Types: ${food.types.joinToString()}")
            TextButton(onClick = onClick) { Text("View 100 Reviews") }
        }
    }
}

@Composable
fun MlFoodDetailScreen(foodId: Int, onBack: () -> Unit) {
    val food = MlData.foods.firstOrNull { it.id == foodId }
    if (food == null) {
        Column(Modifier.fillMaxSize().padding(16.dp)) { Text("Not found") }
        return
    }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = food.name, style = MaterialTheme.typography.titleLarge)
        Text(text = "Average Rating: ${String.format("%.1f", food.avgRating)} ★")
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MaterialTheme.colorScheme
            AssistChip(onClick = {}, label = { Text("All Reviews: ${food.reviews.size}") })
        }
        Spacer(Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(food.reviews) { r ->
                Card { Column(Modifier.padding(12.dp)) { Text("${r.user} · ${r.stars}★ · ${r.type}"); Text(r.text) } }
            }
        }
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onBack) { Text("Back") }
    }
}
