package ies.mariaenriquez.programamovil.navigation

sealed class AppScreens(val route:String) {
    data object LoadingScreen: AppScreens("loading_screen")
    data object ConnectionScreen: AppScreens("connection_screen")
    data object ScannerScreen: AppScreens("scanner_screen")
}