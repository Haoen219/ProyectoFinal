package ies.mariaenriquez.programamovil

import android.annotation.SuppressLint
import android.os.Bundle
import android.content.pm.ActivityInfo
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import ies.mariaenriquez.programamovil.navigation.AppNavigation
import ies.mariaenriquez.programamovil.ui.theme.ProgramaMovilTheme
import ies.mariaenriquez.programamovil.ui.viewmodel.ConexionViewModel

class MainActivity : ComponentActivity() {
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContent {
            ProgramaMovilTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val conexionViewModel: ConexionViewModel = ConexionViewModel(this)
                    AppNavigation(conexionViewModel)
                }
            }
        }
    }
}
