package com.example.areader.navigation

import okhttp3.Route

enum class AllScreens {
    SplashScreen,
    LoginScreen,
    RegisterScreen,
    HomeScreen,
    DetailsScreen,
    SearchScreen,
    StatsScreen,
    UpdateScreen;

    companion object {
        fun fromRoute(route: String): AllScreens =
            when (route?.substringBefore("/")) {
                SplashScreen.name -> SplashScreen
                LoginScreen.name -> LoginScreen
                RegisterScreen.name -> RegisterScreen
                HomeScreen.name -> HomeScreen
                DetailsScreen.name -> DetailsScreen
                SearchScreen.name -> SearchScreen
                StatsScreen.name -> StatsScreen
                UpdateScreen.name -> UpdateScreen
                null -> HomeScreen
                else -> throw IllegalArgumentException("Route $route is not recognized")
            }
    }
}