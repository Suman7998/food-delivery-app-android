package com.munchmatch.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseScreen() {
    val query = remember { mutableStateOf("") }
    val restaurants = listOf("Pizza Palace", "Spice Route", "Green Bowl", "Sushi House", "Cafe Mocha")
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(value = query.value, onValueChange = { query.value = it }, label = { Text("Search restaurants or food") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        LazyColumn {
            items(restaurants.filter { it.contains(query.value, ignoreCase = true) }) { item ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    Text(text = item, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp))
                    Text(text = "Opens 9 AM - 11 PM · 4.5★", modifier = Modifier.padding(horizontal = 16.dp, vertical = 0.dp))
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}
