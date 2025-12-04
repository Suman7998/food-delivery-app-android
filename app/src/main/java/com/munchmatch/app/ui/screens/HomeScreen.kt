package com.munchmatch.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.munchmatch.app.data.FoodItem
import com.munchmatch.app.data.FoodViewModel
import com.munchmatch.app.data.OrderStore
import com.munchmatch.app.navigation.RouteBus

@Composable
fun HomeScreen() {
    val vm: FoodViewModel = viewModel()
    val items by vm.items.collectAsStateWithLifecycle()
    val selectedCategory by vm.selectedCategory.collectAsStateWithLifecycle(initialValue = null)
    val search by vm.searchQuery.collectAsStateWithLifecycle(initialValue = null)

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Discover Foods", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))
        CategoryChips(categories = vm.categories, selected = selectedCategory ?: "All") {
            vm.setCategory(if (it == "All") null else it)
        }
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = search ?: "",
            onValueChange = { vm.setQuery(it.ifBlank { null }) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Search foods") }
        )
        Spacer(Modifier.height(12.dp))
        FoodList(items = items)
    }
}

@Composable
private fun CategoryChips(categories: List<String>, selected: String, onSelect: (String) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(categories) { cat ->
            AssistChip(
                onClick = { onSelect(cat) },
                label = { Text(cat) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = if (cat == selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                )
            )
        }
    }
}

@Composable
private fun FoodList(items: List<FoodItem>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(items) { item ->
            FoodRow(item)
        }
    }
}

@Composable
private fun FoodRow(item: FoodItem) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    modifier = Modifier.clickable {
        OrderStore.setSelected(item.name, item.price, item.imageUrl)
        RouteBus.navigate("order/start")
    }) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Image(
                painter = rememberAsyncImagePainter(item.imageUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.height(72.dp).fillMaxWidth(0.3f)
            )
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = item.name, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text(text = item.category, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(8.dp))
                Text(text = "₹" + String.format("%.2f", item.price), style = MaterialTheme.typography.titleSmall)
            }
        }
    }
}
