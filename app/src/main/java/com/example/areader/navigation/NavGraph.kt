package com.example.areader.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.areader.screens.details.DetailsScreen
import com.example.areader.screens.home.HomeScreen
import com.example.areader.screens.login.LoginScreen
import com.example.areader.screens.search.SearchScreen
import com.example.areader.screens.search.SearchViewModel
import com.example.areader.screens.splash.SplashScreen
import com.example.areader.screens.stats.StatsScreen
import com.example.areader.screens.update.UpdateScreen

@ExperimentalMaterial3Api
@ExperimentalComposeUiApi
@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AllScreens.SplashScreen.name) {
        composable(route = AllScreens.SplashScreen.name) {
            SplashScreen(navController = navController)
        }
        composable(route = AllScreens.LoginScreen.name) {
            LoginScreen(navController = navController)
        }
        composable(route = AllScreens.HomeScreen.name) {
            HomeScreen(navController = navController)
        }
        composable(route = AllScreens.DetailsScreen.name + "/{BookId}", arguments = listOf(
            navArgument(name = "BookId") {
                type = NavType.StringType
            }
        )) {
            DetailsScreen(
                navController = navController,
                bookId = it.arguments?.getString("BookId").toString()
            )
        }
        composable(route = AllScreens.SearchScreen.name) {
            val searchViewModel = hiltViewModel<SearchViewModel>()
            SearchScreen(navController = navController, searchViewModel = searchViewModel)
        }
        composable(route = AllScreens.StatsScreen.name) {
            StatsScreen(navController = navController)
        }
        composable(route = AllScreens.UpdateScreen.name) {
            UpdateScreen(navController = navController)
        }
    }
}