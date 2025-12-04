package com.munchmatch.app.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object RouteBus {
    private val _navigator = MutableStateFlow<(String) -> Unit>({})
    val navigator: StateFlow<(String) -> Unit> = _navigator

    fun setNavigator(navigate: (String) -> Unit) {
        _navigator.value = navigate
    }

    fun navigate(route: String) {
        _navigator.value.invoke(route)
    }
}
