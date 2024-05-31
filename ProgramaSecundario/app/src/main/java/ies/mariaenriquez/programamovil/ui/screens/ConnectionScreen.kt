package ies.mariaenriquez.programamovil.ui.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import ies.mariaenriquez.programamovil.ui.viewmodel.ConexionViewModel

@Composable
fun ConnectionScreen(conexionViewModel: ConexionViewModel, onConnection: () -> Unit) {
    val texto = remember { mutableStateOf("") }

    // Función para establecer el texto del TextField
    val setTexto: (String) -> Unit = { nuevoTexto ->
        texto.value = nuevoTexto
    }

    LazyColumn {
        item {
            TextField(value = texto.value, onValueChange = { setTexto(it) })
        }
        item {
            Button(
                onClick = {
                    Thread {
                        conexionViewModel.iniciarConexion(texto.value, onConnection)
                    }.start()
                },
                content = {
                    Text(text = "Conectar")
                }
            )
        }
    }
}