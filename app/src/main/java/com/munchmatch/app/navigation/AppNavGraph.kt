package com.munchmatch.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.munchmatch.app.ui.screens.BrowseScreen
import com.munchmatch.app.ui.screens.HomeScreen
import com.munchmatch.app.ui.screens.LoginScreen
import com.munchmatch.app.ui.screens.OrdersScreen
import com.munchmatch.app.ui.screens.ProfileScreen
import com.munchmatch.app.ui.screens.SplashScreen
import kotlinx.coroutines.delay

sealed class Destinations(val route: String) {
    data object Splash: Destinations("splash")
    data object Login: Destinations("login")
    data object Main: Destinations("main")
}

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Destinations.Splash.route) {
        composable(Destinations.Splash.route) {
            var done by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) { delay(3000); done = true }
            SplashScreen()
            LaunchedEffect(done) {
                if (done) navController.navigate(Destinations.Login.route) {
                    popUpTo(Destinations.Splash.route) { inclusive = true }
                }
            }
        }
        composable(Destinations.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Destinations.Main.route) {
                        popUpTo(Destinations.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Destinations.Main.route) {
            com.munchmatch.app.ui.screens.MainScaffold()
        }
    }
}

fun NavGraphBuilder.mainGraph(navController: NavHostController) {}

@Composable
fun MainNavHost() {
    val innerNav = rememberNavController()
    NavHost(innerNav, startDestination = "home") {
        composable("home") { HomeScreen() }
        composable("browse") { BrowseScreen() }
        composable("orders") { OrdersScreen() }
        composable("profile") { ProfileScreen() }
    }
}
