package ies.mariaenriquez.programamovil.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ies.mariaenriquez.programamovil.R
import ies.mariaenriquez.programamovil.ui.util.getLastConnectionIp
import ies.mariaenriquez.programamovil.ui.util.saveLastConnectionIp
import ies.mariaenriquez.programamovil.ui.viewmodel.ConexionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectionScreen(conexionViewModel: ConexionViewModel, onConnection: () -> Unit) {
    val contexto = LocalContext.current
    val texto = remember { mutableStateOf("") }
    val lastConnectionIp = remember { mutableStateOf<String?>(null) }

    // Recuperar la última IP de conexión almacenada
    LaunchedEffect(Unit) {
        lastConnectionIp.value = getLastConnectionIp(contexto)
    }

    val isConnected = conexionViewModel.isConnected

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // Box para el texto de conexión
        Box(
            modifier = Modifier
                .padding(16.dp)
                .background(
                    color = if (isConnected) colorResource(id = R.color.conectado) else Color.LightGray,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(8.dp)
                .padding(top = 60.dp)
        ) {
            Text(
                text = if (isConnected) stringResource(id = R.string.Conextion_Conected) else stringResource(id = R.string.Conextion_Disconected),
                color = Color.White
            )
        }
        Column(
            modifier = Modifier.fillMaxSize(), // Para ocupar todo el espacio disponible
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .background(
                        color = Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(bottom = 15.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = stringResource(id = R.string.Conextion_Intruction), color = Color.Black, textAlign = TextAlign.Start)
                    Spacer(modifier = Modifier.height(16.dp))
                    TextField(
                        value = texto.value,
                        onValueChange = { newText -> texto.value = newText },
                        label = { Text(stringResource(id = R.string.Conextion_TextField_Label)) },
                        modifier = Modifier.background(Color.LightGray),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.Gray,
                            focusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            Thread {
                                conexionViewModel.iniciarConexion(texto.value, onConnection)
                                saveLastConnectionIp(contexto, texto.value)
                            }.start()
                        },
                        content = {
                            Text(text = stringResource(id = R.string.Conextion_Button))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.main),
                            contentColor = Color.White
                        )
                    )
                }
            }

            if (lastConnectionIp != null) {
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .background(
                            color = colorResource(id = R.color.main_semi),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .border(2.dp, colorResource(id = R.color.main))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = stringResource(id = R.string.Conextion_Last_Conection)+" ${lastConnectionIp.value}", color = Color.White)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                Thread {
                                    conexionViewModel.iniciarConexion(lastConnectionIp.value!!, onConnection)
                                }.start()
                            },
                            content = {
                                Text(text = stringResource(id = R.string.Conextion_Last_Conection_Button))
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(id = R.color.main),
                                contentColor = Color.White
                            )
                        )
                    }
                }
            }
        }
    }
}
