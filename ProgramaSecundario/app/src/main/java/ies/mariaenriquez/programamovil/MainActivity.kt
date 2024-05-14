package ies.mariaenriquez.programamovil

import android.app.Activity
import android.os.Bundle
import android.content.Context
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.google.android.material.snackbar.Snackbar
import ies.mariaenriquez.programamovil.ui.theme.ProgramaMovilTheme
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
                    val gestorCliente = GestorCliente(this)
                    Tabla(gestorCliente)
                }
            }
        }
    }
}

fun Context.showSnackbar(message: String) {
    (this as? Activity)?.runOnUiThread {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

fun validarCampos(context: Context, texto: String): Boolean {
    if (texto.isEmpty()) {
        context.showSnackbar("Error: rellena los campos.")
        return false
    }
    if (!texto.matches("[0-9]+".toRegex())) {
        context.showSnackbar("Error: valor no válido introducido.")
        return false
    }
    if (texto.length > 19) {    //19 porque en la BD BIGINT puede tener max. 19 dígitos.
        context.showSnackbar("Error: Valor no válido para Núm. Barra.")
        return false
    }
    return true
}

class GestorCliente(val context: Context) : Runnable {
    val serverAddress = "192.168.1.132"
    val puerto = 1234

    var socket: Socket? = null
    var writer: BufferedWriter? = null

    override fun run() {
        try {
            socket = Socket(serverAddress, puerto)
            writer = BufferedWriter(OutputStreamWriter(socket!!.getOutputStream()))
        } catch (e: Exception) {
            e.printStackTrace();
            context.showSnackbar("Error al enviar mensaje: ${e.message}")
        }
    }

    private fun cerrarConexion() {
        try {
            writer?.close()
            socket?.close()
        } catch (e: Exception) {
            e.printStackTrace()
            context.showSnackbar("Error al enviar mensaje: ${e.message}")
        }
    }

    fun enviarMensaje(message: String) {
        try {
            if (validarCampos(context, message)){
                writer?.write(message)
                writer?.newLine()
                writer?.flush()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            context.showSnackbar("Error al enviar mensaje: ${e.message}")
        }
    }
}

@Composable
fun Tabla(gestorCliente: GestorCliente) {
    val texto = remember { mutableStateOf("") }

    Thread(gestorCliente).start();

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
                        gestorCliente.enviarMensaje(texto.value)
                    }.start()
                },
                content = {
                    Text(text = "Enviar")
                }
            )
        }
    }
}