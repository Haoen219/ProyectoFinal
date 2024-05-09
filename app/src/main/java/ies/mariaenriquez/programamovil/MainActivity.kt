package ies.mariaenriquez.programamovil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ies.mariaenriquez.programamovil.ui.theme.ProgramaMovilTheme
import kotlinx.coroutines.launch
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.net.Socket

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProgramaMovilTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Tabla()
                }
            }
        }
    }
}

@Composable
fun Tabla() {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var puerto = 1234
    val texto = remember { mutableStateOf("") }

    // Función para establecer el texto del TextField
    val setTexto: (String) -> Unit = { nuevoTexto ->
        texto.value = nuevoTexto
    }

    fun enviarTextoPorSocket() {
        val textoAEnviar = texto.value
        val serverAddress = "192.168.1.132"

        try {
            val socket = Socket(serverAddress, puerto)
            //val writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))
            //writer.write(textoAEnviar)
            //writer.flush()
            //writer.close()
            socket.close()
        } catch (e: Exception) {
            scope.launch {
                val mensaje = e.message ?: "Error desconocido" // Si e.message es null, usa "Error desconocido"
                snackbarHostState.showSnackbar(mensaje)
                snackbarHostState.showSnackbar(e.message!!)
            }
        }
    }

    LazyColumn {
        item {
            TextField(value = texto.value, onValueChange = { setTexto(it) })
        }
        item {
            Button(
                onClick = {
                    enviarTextoPorSocket()
                },
                content = {
                    Text(text = "Enviar")
                }
            )
        }
    }

    // Mostrar el Snackbar
    SnackbarHost(snackbarHostState)
}

@Preview(showBackground = true)
@Composable
fun PreviewTabla() {
    ProgramaMovilTheme {
        Tabla()
    }
}