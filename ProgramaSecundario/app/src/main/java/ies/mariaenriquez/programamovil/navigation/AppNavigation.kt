package com.iesma.dam2.test.moviesv2.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ies.mariaenriquez.programamovil.ui.screens.ConnectionScreen
import ies.mariaenriquez.programamovil.ui.screens.LoadingScreen
import ies.mariaenriquez.programamovil.ui.screens.ScannerScreen
import ies.mariaenriquez.programamovil.ui.viewmodel.ConexionViewModel

@Composable
fun AppNavigation (conexionViewModel: ConexionViewModel){
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = AppScreens.LoadingScreen.route
    ){
        composable(route = AppScreens.LoadingScreen.route){
            LoadingScreen(
                onLoadingComplete = {
                    navController.navigate(AppScreens.ConnectionScreen.route)
                }
            )
        }
        composable(route = AppScreens.ConnectionScreen.route){
            ConnectionScreen(
                conexionViewModel,
                onConnection = {
                    navController.navigate(AppScreens.ScannerScreen.route)
                }
            )
        }
        composable(route = AppScreens.ScannerScreen.route){
            ScannerScreen(conexionViewModel)
        }
    }
}



