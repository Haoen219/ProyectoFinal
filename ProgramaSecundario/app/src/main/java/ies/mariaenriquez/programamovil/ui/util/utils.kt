package ies.mariaenriquez.programamovil.ui.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import ies.mariaenriquez.programamovil.navigation.AppScreens
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.BottomNavigation
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.BottomNavigationItem
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Divider
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.TextButton
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import ies.mariaenriquez.programamovil.R

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Scaffold(navController: NavHostController, titulo:String, content: @Composable () -> Unit) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet { // Aquí puedes definir el ancho del drawer
                Box(
                    modifier = Modifier
                        .width(200.dp) // Cambia el valor según tu preferencia
                ) {
                    // Contenido del drawer
                    DrawerContent(navController = navController)
                } }
        },
    ) {
        Scaffold(
            topBar = {
                TopBar(
                    drawerState = drawerState,
                    coroutineScope = coroutineScope,
                    titulo = titulo
                )
            },
            bottomBar = {
                BottomNavigationBar(navController = navController)
            }
        ) { content() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(drawerState: DrawerState, coroutineScope: CoroutineScope, titulo: String) {
    TopAppBar(
        modifier = Modifier.
            background(
                color = colorResource(id = R.color.main)
            ),
        title = { Text(titulo) },
        navigationIcon = {
            IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                Icon(Icons.Default.Menu, contentDescription = stringResource(id = R.string.menu_Description))
            }
        }
    )
}

data class BottomNavItem(val label: String, val icon: Int, val route: String)

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem(stringResource(id = R.string.connection_text), R.drawable.wifi, AppScreens.ConnectionScreen.route),
        BottomNavItem(stringResource(id = R.string.scanner_text), R.drawable.barcode_scan, AppScreens.ScannerScreen.route)
    )
    BottomNavigation {
        val currentRoute = navController.currentDestination!!.route
        items.forEach { item ->
            BottomNavigationItem(
                icon = {
                    val iconPainter: Painter = painterResource(id = item.icon)
                    Icon(iconPainter, contentDescription = item.label)
                },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route)
                }
            )
        }
    }
}

@Composable
fun DrawerNavigationItem(text: String, icon: Int, iconDescription: String, onClick: () -> Unit) {
    val iconPainter: Painter = painterResource(id = icon)

    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(iconPainter, contentDescription = iconDescription, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(text)
    }
}

@Composable
fun DrawerContent(navController: NavHostController) {
    Column {
        Text(text = stringResource(id = R.string.app_name), modifier = Modifier.padding(16.dp))
        Divider()
        Spacer(modifier = Modifier.height(16.dp))

        DrawerNavigationItem(
            text = stringResource(id = R.string.Connection_Screen),
            icon = R.drawable.wifi,
            iconDescription = stringResource(id = R.string.wifi_Description),
            onClick = {
                navController.navigate(AppScreens.ConnectionScreen.route)
            }
        )
        DrawerNavigationItem(
            text = stringResource(id = R.string.Scanner_Screen),
            icon = R.drawable.barcode_scan,
            iconDescription = stringResource(id = R.string.scanner_Description),
            onClick = {
                navController.navigate(AppScreens.ScannerScreen.route)
            }
        )
    }
}

fun realizarSonido(context: Context, sonido :Int) {
    val mediaPlayer = MediaPlayer.create(context, sonido) // Reemplaza un pitido usando el mp3
    mediaPlayer.start()
    mediaPlayer.setOnCompletionListener { mediaPlayer.release() }
}

fun Context.showToast(message: String) {
    (this as? Activity)?.runOnUiThread {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

//Estas dos funciones sirven para obtener datos guardados en caché
fun saveLastConnectionIp(context: Context, ip: String) {
    val sharedPreferences = context.getSharedPreferences("conexion_prefs", Context.MODE_PRIVATE)
    sharedPreferences.edit().putString("last_connection_ip", ip).apply()
}

fun getLastConnectionIp(context: Context): String? {
    val sharedPreferences = context.getSharedPreferences("conexion_prefs", Context.MODE_PRIVATE)
    return sharedPreferences.getString("last_connection_ip", null)
}