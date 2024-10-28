package ies.mariaenriquez.programamovil.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ies.mariaenriquez.programamovil.R
import ies.mariaenriquez.programamovil.ui.screens.ConnectionScreen
import ies.mariaenriquez.programamovil.ui.screens.DataScreen
import ies.mariaenriquez.programamovil.ui.screens.EditorScreen
import ies.mariaenriquez.programamovil.ui.screens.LoadingScreen
import ies.mariaenriquez.programamovil.ui.screens.ScannerScreen
import ies.mariaenriquez.programamovil.ui.util.Scaffold
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
            BackHandler(true) {
                //desactivar la funcion de volver atras
            }
            Scaffold(navController = navController, titulo = stringResource(id = R.string.TopBar_Conection)) {
                ConnectionScreen(
                    conexionViewModel = conexionViewModel,
                    onConnection = {
                        navController.navigate(AppScreens.ScannerScreen.route)
                    }
                )
            }
        }
        composable(route = AppScreens.ScannerScreen.route) {
            Scaffold(navController = navController, titulo = stringResource(id = R.string.TopBar_Scanner)) {
                ScannerScreen(
                    conexionViewModel,
                    onDisconnect = {
                        navController.navigate(AppScreens.ConnectionScreen.route)
                    }
                )
            }
        }



        composable(route = AppScreens.EditorScreen.route) {
            Scaffold(navController = navController, titulo = stringResource(id = R.string.TopBar_Scanner)) {
                BackHandler(true) {
                    navController.navigate(AppScreens.ConnectionScreen.route)
                }
                EditorScreen(
                    conexionViewModel,
                    onDisconnect = {
                        navController.navigate(AppScreens.ConnectionScreen.route)
                    },
                    onFound = { dataArticulo ->
                        navController.navigate("data_screen/$dataArticulo")
                    },
                    onNotFound = { idArticulo ->
                        navController.navigate("data_screen/$idArticulo")
                    }
                )
            }
        }
        composable(route = AppScreens.DataScreen.route) { navBackStackEntry ->
            val articulo = navBackStackEntry.arguments?.getString("articulo")       //recuperar el valor del {articulo} guardado en la ruta
            Scaffold(navController = navController, titulo = stringResource(id = R.string.TopBar_Scanner)) {
                DataScreen(
                    conexionViewModel,
                    articulo,
                    onDisconnect = {
                        navController.navigate(AppScreens.ConnectionScreen.route)
                    }
                )
            }
        }
    }
}



