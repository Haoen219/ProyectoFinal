package com.iesma.dam2.test.moviesv2.navigation

sealed class AppScreens(val route:String) {
    object LoadingScreen: AppScreens("loading_screen")
    object ConnectionScreen: AppScreens("connection_screen")
    object ScannerScreen: AppScreens("scanner_screen")
}