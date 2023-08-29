package com.example.areader.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.areader.screens.splash.SplashScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AllScreens.SearchScreen.name) {
        composable(route = AllScreens.SearchScreen.name){
            SplashScreen(navController = navController)
        }
    }
}