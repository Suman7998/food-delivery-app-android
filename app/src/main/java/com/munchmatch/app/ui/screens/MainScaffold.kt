package com.munchmatch.app.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import android.os.Build
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import com.munchmatch.app.notifications.NotificationHelper
import com.munchmatch.app.net.ConnectivityObserver
import com.munchmatch.app.net.DataExchange
import com.munchmatch.app.navigation.RouteBus
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.ui.graphics.Brush
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.fillMaxSize

private data class Tab(val route: String, val label: String, val icon: @Composable () -> Unit)

@Composable
fun MainScaffold() {
    val navController = rememberNavController()
    val tabs = listOf(
        Tab("home", "Home", { Icon(Icons.Outlined.Home, contentDescription = null) }),
        Tab("browse", "Browse", { Icon(Icons.Outlined.Explore, contentDescription = null) }),
        Tab("orders", "Orders", { Icon(Icons.Outlined.ReceiptLong, contentDescription = null) }),
        Tab("profile", "Profile", { Icon(Icons.Outlined.Person, contentDescription = null) })
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBars = currentRoute in tabs.map { it.route }

    // Notifications: request permission on API 33+ and post alerts once
    val context = LocalContext.current
    val notified = remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted && !notified.value) {
                NotificationHelper.postSampleAlerts(context)
                notified.value = true
            }
        }
    )

    LaunchedEffect(showBars) {
        if (showBars && !notified.value) {
            if (Build.VERSION.SDK_INT >= 33) {
                val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
                if (granted) {
                    NotificationHelper.postSampleAlerts(context)
                    notified.value = true
                } else {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            } else {
                NotificationHelper.postSampleAlerts(context)
                notified.value = true
            }
        }
    }

    val connectivityObserver = remember { ConnectivityObserver(context) }
    val exchanged = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        RouteBus.setNavigator { route -> navController.navigate(route) }
    }
    LaunchedEffect(Unit) {
        connectivityObserver.isConnected.collect { connected ->
            if (connected && !exchanged.value) {
                val result = withContext(Dispatchers.IO) { DataExchange.perform() }
                val transport = connectivityObserver.transport.value
                val title = if (result.success) "Data exchange completed" else "Data exchange failed"
                val text = if (result.success) "Connected over $transport · code ${result.code}" else "Error code ${result.code}"
                NotificationHelper.postMessage(context, title, text)
                exchanged.value = true
            }
        }
    }

    Scaffold(
        bottomBar = {
            if (showBars) {
                NavigationBar {
                    val currentDestination = navBackStackEntry?.destination
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            selected = currentDestination.isRouteInHierarchy(tab.route),
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = tab.icon,
                            label = { Text(tab.label) }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (showBars) {
                Column {
                    ExtendedFloatingActionButton(
                        onClick = { navController.navigate("chat") },
                        text = { Text("Chatbot") },
                        icon = { Icon(Icons.Outlined.Chat, contentDescription = null) },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(Modifier.height(12.dp))
                    ExtendedFloatingActionButton(
                        onClick = { navController.navigate("ml") },
                        text = { Text("ML Features") },
                        icon = { Icon(Icons.Outlined.Explore, contentDescription = null) },
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Spacer(Modifier.height(12.dp))
                    ExtendedFloatingActionButton(
                        onClick = { navController.navigate("map") },
                        text = { Text("Map") },
                        icon = { Icon(Icons.Outlined.Map, contentDescription = null) },
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(Modifier.height(12.dp))
                    ExtendedFloatingActionButton(
                        onClick = { navController.navigate("multimedia") },
                        text = { Text("Multimedia") },
                        icon = { Icon(Icons.Outlined.ReceiptLong, contentDescription = null) },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = "home",
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.tertiaryContainer,
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .padding(innerPadding)
        ) {
            composable("home") { HomeScreen() }
            composable("browse") { BrowseScreen() }
            composable("orders") { OrdersScreen() }
            composable("profile") { ProfileScreen() }
            composable("chat") { ChatbotScreen(onBack = { navController.popBackStack() }) }
            composable("ml") {
                MlFeaturesScreen(onFoodClick = { id -> navController.navigate("mlDetail/$id") })
            }
            composable(
                route = "mlDetail/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: -1
                MlFoodDetailScreen(foodId = id, onBack = { navController.popBackStack() })
            }
            composable("map") { MapScreen() }
            composable("multimedia") { MultimediaScreen(onBack = { navController.popBackStack() }) }
            // Order flow routes
            composable("order/start") { OrderStartScreen(onNavigate = { route -> navController.navigate(route) }) }
            composable("order/deliveryDetails") { DeliveryDetailsScreen(onNavigate = { route -> navController.navigate(route) }) }
            composable("order/payment") { PaymentMethodScreen(onNavigate = { route -> navController.navigate(route) }) }
            composable("order/codConfirm") { CodConfirmScreen(onDone = { navController.popBackStack("home", inclusive = false) }) }
            composable("order/gpaySummary") { GPaySummaryScreen(onNavigate = { route -> navController.navigate(route) }) }
            composable("order/gpaySuccess") { GPaySuccessScreen(onDone = { navController.popBackStack("home", inclusive = false) }) }
            composable("order/dinein") { DineInListScreen(onBack = { navController.popBackStack() }) }
        }
    }
}

private fun NavDestination?.isRouteInHierarchy(route: String): Boolean {
    return this?.hierarchy?.any { it.route == route } == true
}
