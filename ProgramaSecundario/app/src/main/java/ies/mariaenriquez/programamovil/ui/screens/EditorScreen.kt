package ies.mariaenriquez.programamovil.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.res.stringResource
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import ies.mariaenriquez.programamovil.R
import ies.mariaenriquez.programamovil.ui.viewmodel.CameraScreen
import ies.mariaenriquez.programamovil.ui.viewmodel.ConexionViewModel

/*
    Pantalla para escanear codigos de barra y editar/registrar articulos
 */

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun EditorScreen(conexionViewModel: ConexionViewModel, onDisconnect: () -> Unit, onFound: (String) -> Unit, onNotFound: (String) -> Unit) {
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    if (cameraPermissionState.status.isGranted) {
        CameraScreen(
            conexionViewModel,
            onDisconnect,
            onScanned = {
                //enviar el ID del articulo para ver si existe
                mensajeEscaneado ->
                conexionViewModel.enviarMensaje(
                    mensajeEscaneado,
                    onDisconnect
                )

                // Leer la respuesta del programa principal, <<FOUND>> o <<NOT_FOUND>>
                conexionViewModel.leerMensaje(
                    onMensajeRecibido = { mensajeRecibido ->
                        // Extraer el comando y los datos del mensaje recibido
                        val finComando: Int = mensajeRecibido.indexOf(">>") + 2
                        val comando: String = mensajeRecibido.substring(0, finComando).trim()  // Asegurarse de eliminar espacios
                        val datos: String = mensajeRecibido.substring(finComando).trim()

                        // Verificar el comando
                        when (comando) {
                            "<<FOUND>>" -> {
                                //extraer y editar datos
                            }
                            "<<NOT_FOUND>>" -> {
                                //registrar nuevos datos
                            }
                            else -> {
                                println("Comando desconocido: $comando")
                            }
                        }
                    },
                    onDisconnect = onDisconnect  // Manejo de desconexión
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