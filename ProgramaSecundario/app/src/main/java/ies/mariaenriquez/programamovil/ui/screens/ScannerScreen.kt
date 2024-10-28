package ies.mariaenriquez.programamovil.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.res.stringResource
import ies.mariaenriquez.programamovil.ui.viewmodel.ConexionViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale
import ies.mariaenriquez.programamovil.R
import ies.mariaenriquez.programamovil.ui.viewmodel.CameraScreen

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScannerScreen(conexionViewModel: ConexionViewModel, onDisconnect: () -> Unit) {
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    if (cameraPermissionState.status.isGranted) {
        CameraScreen(
            conexionViewModel,
            onDisconnect,
            onScanned = {
                mensajeEscaneado ->
                    conexionViewModel.enviarMensaje(
                        mensajeEscaneado,
                        onDisconnect
                    )
            }
        )
    } else if (cameraPermissionState.status.shouldShowRationale) {
        Text(stringResource(id = R.string.Scanner_Permission_TRUE))
    } else {
        SideEffect {
            cameraPermissionState.run { launchPermissionRequest() }
        }
        Text(stringResource(id = R.string.Scanner_Permission_FALSE))
    }
}

