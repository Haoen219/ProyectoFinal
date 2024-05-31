package ies.mariaenriquez.programamovil.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ies.mariaenriquez.programamovil.showSnackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.net.Socket

class ConexionViewModel(val context: Context) : ViewModel() {
    private val puerto = 1234

    private var socket: Socket? = null
    private var writer: BufferedWriter? = null

    fun iniciarConexion(ip : String, onConnection: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                socket = Socket(ip, puerto)
                writer = BufferedWriter(OutputStreamWriter(socket!!.getOutputStream()))
                withContext(Dispatchers.Main) {
                    onConnection()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    context.showSnackbar("Error al iniciar conexión: ${e.message}")
                }
            }
        }
    }

    fun enviarMensaje(message: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (validarCampos(context, message)) {
                    writer?.write(message)
                    writer?.newLine()
                    writer?.flush()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    context.showSnackbar("Error al enviar mensaje: ${e.message}")
                }
            }
        }
    }

    private fun cerrarConexion() {
        try {
            writer?.close()
            socket?.close()
        } catch (e: Exception) {
            e.printStackTrace()
            context.showSnackbar("Error al cerrar conexión: ${e.message}")
        }
    }

    fun validarCampos(context: Context, texto: String): Boolean {
        if (texto.isEmpty()) {
            context.showSnackbar("Error: rellena los campos.")
            return false
        }
        if (!texto.matches("[0-9]+".toRegex())) {
            context.showSnackbar("Error: valor no válido introducido. $texto")
            return false
        }
        if (texto.length > 19) {    //19 porque en la BD BIGINT puede tener max. 19 dígitos.
            context.showSnackbar("Error: Valor no válido para Núm. Barra. $texto")
            return false
        }
        return true
    }
}