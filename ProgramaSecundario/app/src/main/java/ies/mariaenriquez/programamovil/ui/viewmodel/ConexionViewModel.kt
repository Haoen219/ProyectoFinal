package ies.mariaenriquez.programamovil.ui.viewmodel

import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ies.mariaenriquez.programamovil.R
import ies.mariaenriquez.programamovil.ui.util.realizarSonido
import ies.mariaenriquez.programamovil.ui.util.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket

class ConexionViewModel(private val context: Context) : ViewModel() {
    private val puerto = 1234

    private var socket: Socket? = null
    private var writer: BufferedWriter? = null
    var isConnected: Boolean = false

    fun iniciarConexion(ip: String, onConnection: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Si hay una conexión existente, ciérrala primero
                if (socket != null && socket?.isConnected == true) {
                    cerrarConexion()
                }

                // Establece la nueva conexión
                socket = Socket(ip, puerto)
                writer = BufferedWriter(OutputStreamWriter(socket!!.getOutputStream()))
                isConnected = true
                withContext(Dispatchers.Main) {
                    onConnection()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    context.showToast(context.getString(R.string.Conexion_View_Toast_Conect)+" ${e.message}")
                }
            }
        }
    }

    fun enviarMensaje(message: String, onDisconnect:() -> Unit){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (socket == null || socket?.isConnected == false) {   //Comprobar si hay conexión
                    withContext(Dispatchers.Main) {     //ejecutarlo en main thread
                        isConnected = false
                        onDisconnect()
                    }
                    return@launch
                }

                if (validarCampos(context, message)) {
                    realizarSonido(context, R.raw.pitido_scanner)
                    writer?.write(message)
                    writer?.newLine()
                    writer?.flush()
                }else{
                    realizarSonido(context, R.raw.sonido_error)
                }
            } catch (e: Exception) {
                realizarSonido(context, R.raw.sonido_error)
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    context.showToast(context.getString(R.string.Conexion_View_Toast_Send)+" ${e.message}")
                }
                withContext(Dispatchers.Main) {     //ejecutarlo en main thread
                    isConnected = false
                    onDisconnect()
                }
            }
        }
    }

    //Función para leer el mensaje enviado por el programa principal y procesar con la siguiente accion.
    fun leerMensaje(onMensajeRecibido: (String) -> Unit, onDisconnect: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (socket == null || socket?.isConnected == false) {  // Comprobar si hay conexión
                    withContext(Dispatchers.Main) {
                        isConnected = false
                        onDisconnect()
                    }
                    return@launch
                }

                val reader = BufferedReader(InputStreamReader(socket?.getInputStream())) // Inicializar el lector
                val mensaje = reader.readLine() // Leer solo una línea del socket

                if (mensaje != null) {
                    withContext(Dispatchers.Main) {

                        onMensajeRecibido(mensaje)

                    }
                } else {
                    withContext(Dispatchers.Main) {
                        isConnected = false
                        onDisconnect() // Desconectar si no se recibe mensaje
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    context.showToast(context.getString(R.string.Conexion_View_Toast_Receive) + " ${e.message}")
                }
                withContext(Dispatchers.Main) {
                    isConnected = false
                    onDisconnect()
                }
            }
        }
    }

    private fun cerrarConexion() {
        try {
            writer?.close()
            socket?.close()
            isConnected = false
        } catch (e: Exception) {
            e.printStackTrace()
            context.showToast(context.getString(R.string.Conexion_View_Toast_Close)+" ${e.message}")
        }
    }

    private fun validarCampos(context: Context, texto: String): Boolean {
        if (texto.isEmpty()) {
            context.showToast(context.getString(R.string.Conexion_View_Toast_Text_Empty))
            return false
        }
        if (!texto.matches("[0-9]+".toRegex())) {
            context.showToast(context.getString(R.string.Conexion_View_Toast_Text_NotValid)+" $texto")
            return false
        }
        if (texto.length > 18) {    //18 porque en la BD BIGINT puede tener max. 19 dígitos.
            context.showToast(context.getString(R.string.Conexion_View_Toast_Text_TooLong)+" $texto")
            return false
        }
        return true
    }
}