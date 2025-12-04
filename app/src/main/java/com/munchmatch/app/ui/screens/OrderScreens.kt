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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.munchmatch.app.data.OrderStore

@Composable
fun OrderStartScreen(onNavigate: (String) -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Choose Order Type", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = {
                    OrderStore.setMode("delivery")
                    onNavigate("order/deliveryDetails")
                }, modifier = Modifier.fillMaxWidth()) { Text("Home Delivery") }
                Button(onClick = {
                    OrderStore.setMode("dine_in")
                    onNavigate("order/dinein")
                }, modifier = Modifier.fillMaxWidth()) { Text("Dine In") }
            }
        }
    }
}

@Composable
fun DeliveryDetailsScreen(onNavigate: (String) -> Unit) {
    val name = remember { mutableStateOf("") }
    val contact = remember { mutableStateOf("") }
    val address = remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Delivery Details", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(value = name.value, onValueChange = { name.value = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = contact.value, onValueChange = { contact.value = it }, label = { Text("Contact Number") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = address.value, onValueChange = { address.value = it }, label = { Text("Address") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        Button(onClick = { onNavigate("order/payment") }, modifier = Modifier.fillMaxWidth()) { Text("Continue") }
    }
}

@Composable
fun PaymentMethodScreen(onNavigate: (String) -> Unit) {
    val selected = remember { mutableStateOf("cod") }
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Payment Method", style = MaterialTheme.typography.titleLarge)
        Row { RadioButton(selected = selected.value == "cod", onClick = { selected.value = "cod" }); Text("Cash on Delivery", modifier = Modifier.padding(start = 8.dp)) }
        Row { RadioButton(selected = selected.value == "gpay", onClick = { selected.value = "gpay" }); Text("GPay", modifier = Modifier.padding(start = 8.dp)) }
        Spacer(Modifier.height(8.dp))
        Button(onClick = {
            OrderStore.setPayment(selected.value)
            if (selected.value == "cod") onNavigate("order/codConfirm") else onNavigate("order/gpaySummary")
        }, modifier = Modifier.fillMaxWidth()) { Text("Continue") }
    }
}

@Composable
fun CodConfirmScreen(onDone: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Order Confirmed", style = MaterialTheme.typography.titleLarge)
        Text("The package will be delivered within 9 to 10 minutes.")
        OrderStore.updateStatus("Delivery in 9–10 minutes")
        Spacer(Modifier.height(8.dp))
        Button(onClick = onDone, modifier = Modifier.fillMaxWidth()) { Text("Done") }
    }
}

@Composable
fun GPaySummaryScreen(onNavigate: (String) -> Unit) {
    val order = OrderStore.currentOrder.value
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("GPay Summary", style = MaterialTheme.typography.titleLarge)
        Text("Item: ${order?.name ?: "Selected food"}")
        Text("Amount: ₹${String.format("%.2f", order?.price ?: 0.0)}")
        Spacer(Modifier.height(8.dp))
        Button(onClick = { onNavigate("order/gpaySuccess") }, modifier = Modifier.fillMaxWidth()) { Text("Pay with GPay") }
    }
}

@Composable
fun GPaySuccessScreen(onDone: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Payment Successful", style = MaterialTheme.typography.titleLarge)
        Text("The package will be delivered soon within 9 to 10 minutes.")
        OrderStore.updateStatus("Delivery in 9–10 minutes")
        Spacer(Modifier.height(8.dp))
        Button(onClick = onDone, modifier = Modifier.fillMaxWidth()) { Text("Done") }
    }
}

@Composable
fun DineInListScreen(onBack: () -> Unit) {
    val list = (1..100).map { idx -> "Restaurant $idx · Location ${listOf("Andheri","Bandra","Juhu","Colaba")[idx % 4]}" }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Dine-in Restaurants", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(list) { entry ->
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Text(entry, modifier = Modifier.padding(12.dp))
                }
            }
        }
    }
}
