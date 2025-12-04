package com.munchmatch.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.width

private data class ChatMessage(val fromUser: Boolean, val text: String)

@Composable
fun ChatbotScreen(onBack: () -> Unit) {
    val messages = remember {
        mutableStateListOf(
            ChatMessage(false, "Hi! I’m your Munch Match assistant. Ask me about food, restaurants, orders, or get mood-based suggestions.")
        )
    }
    val input = remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) { Icon(Icons.Outlined.ArrowBack, contentDescription = null) }
                Text(text = "MunchBot", style = MaterialTheme.typography.titleLarge)
            }
        }
    ) { inner ->
        Column(modifier = Modifier.fillMaxSize().padding(inner).padding(12.dp)) {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                reverseLayout = true
            ) {
                items(messages.asReversed()) { msg ->
                    MessageBubble(msg)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = input.value,
                    onValueChange = { input.value = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("Type a message") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                Spacer(Modifier.width(8.dp))
                TextButton(onClick = {
                    val q = input.value.trim()
                    if (q.isNotEmpty()) {
                        messages.add(ChatMessage(true, q))
                        val a = replyFor(q)
                        messages.add(ChatMessage(false, a))
                        input.value = ""
                    }
                }) { Text("Send") }
            }
        }
    }
}

@Composable
private fun MessageBubble(msg: ChatMessage) {
    val bg = if (msg.fromUser) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant
    val align = if (msg.fromUser) Alignment.CenterEnd else Alignment.CenterStart
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = align) {
        Surface(color = bg, shape = MaterialTheme.shapes.medium, modifier = Modifier.fillMaxWidth(0.9f)) {
            Text(text = msg.text, modifier = Modifier.padding(12.dp))
        }
    }
}

// Simple knowledge base and fuzzy matcher (local, no backend)
private val KB: List<Pair<List<String>, String>> = listOf(
    listOf("hello", "hi", "hey", "namaste") to "Hello! How can I help you discover great food today?",
    listOf("mood", "happy", "sad", "stressed", "energetic") to "Tell me your mood (happy/sad/stressed/energetic) and I’ll suggest matching meals!",
    listOf("recommend", "suggest", "meal", "food") to "Popular picks: Margherita Pizza, Veggie Burger, Pasta Alfredo, Sushi Platter.",
    listOf("pizza", "pizzeria", "margherita") to "Try Cheesy Margherita Pizza at Pizza Palace — crowd favorite!",
    listOf("burger", "veg burger") to "Veggie Burger with crispy fries pairs well with a chocolate shake.",
    listOf("sushi", "japanese") to "Sushi House offers fresh platters and maki rolls.",
    listOf("track", "order status", "where is my order") to "Open the Orders tab to see live progress of your delivery.",
    listOf("refund", "cancel") to "For refunds or cancellations, go to Orders > select your order > Help.",
    listOf("offers", "discount", "coupon") to "Today’s deals: 20% off on pizzas, free delivery above ₹299.",
    listOf("time", "delivery time", "eta") to "Average delivery time around 25–35 minutes depending on location.",
    listOf("vegetarian", "veg", "vegan") to "Green Bowl has great vegetarian and vegan bowls with fresh veggies.",
    listOf("near me", "nearby", "distance") to "Use Browse filters to sort by distance and top-rated nearby restaurants.",
    listOf("help", "support") to "You can ask me about dining suggestions, orders, offers, or restaurants."
)

private fun replyFor(query: String): String {
    val q = query.lowercase()
    // Exact keyword hit
    KB.forEach { (keys, ans) ->
        if (keys.any { q.contains(it) }) return ans
    }
    // Token overlap fallback
    val tokens = q.split(" ", ",", ".", "?", "!", "-", "/").filter { it.isNotBlank() }
    var bestAns: String? = null
    var bestScore = 0.0
    for ((keys, ans) in KB) {
        val score = jaccard(tokens.toSet(), keys.toSet())
        if (score > bestScore) { bestScore = score; bestAns = ans }
    }
    if (bestScore > 0.2) return bestAns!!
    return "I’m not sure yet. Try asking about mood-based suggestions, orders, offers, or restaurants."
}

private fun <T> jaccard(a: Set<T>, b: Set<T>): Double {
    if (a.isEmpty() && b.isEmpty()) return 0.0
    val inter = a.intersect(b).size.toDouble()
    val union = (a.size + b.size - inter).toDouble()
    return if (union == 0.0) 0.0 else inter / union
}
