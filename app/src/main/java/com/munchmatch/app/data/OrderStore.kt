package com.munchmatch.app.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object OrderStore {
    data class Order(
        val name: String,
        val price: Double,
        val imageUrl: String,
        val mode: String? = null, // dine_in or delivery
        val payment: String? = null // cod or gpay
    )

    private val _currentOrder = MutableStateFlow<Order?>(null)
    val currentOrder: StateFlow<Order?> = _currentOrder

    private val _status = MutableStateFlow<String>("No active orders")
    val status: StateFlow<String> = _status

    fun setSelected(name: String, price: Double, imageUrl: String) {
        _currentOrder.value = Order(name, price, imageUrl)
    }

    fun setMode(mode: String) {
        _currentOrder.value = _currentOrder.value?.copy(mode = mode)
    }

    fun setPayment(payment: String) {
        _currentOrder.value = _currentOrder.value?.copy(payment = payment)
    }

    fun updateStatus(text: String) {
        _status.value = text
    }
}
