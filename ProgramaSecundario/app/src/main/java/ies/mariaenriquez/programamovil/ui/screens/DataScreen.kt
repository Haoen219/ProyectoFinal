package ies.mariaenriquez.programamovil.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ies.mariaenriquez.programamovil.ui.viewmodel.ConexionViewModel

@Composable
fun DataScreen(conexionViewModel: ConexionViewModel, articulo: String?, onDisconnect: () -> Unit) {
    var field1 by remember { mutableStateOf("") }
    var field2 by remember { mutableStateOf("") }
    var field3 by remember { mutableStateOf("") }
    var field4 by remember { mutableStateOf("") }
    var field5 by remember { mutableStateOf("") }

    // Contenido de la pantalla
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), // Padding alrededor de los componentes
        verticalArrangement = Arrangement.SpaceBetween // Alineación vertical con separación
    ) {
        // Campos de texto (TextField) para introducir datos
        TextField(
            value = field1,
            onValueChange = { field1 = it },
            label = { Text(text = "Campo 1") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp)) // Espaciado entre campos

        TextField(
            value = field2,
            onValueChange = { field2 = it },
            label = { Text(text = "Campo 2") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp)) // Espaciado entre campos

        TextField(
            value = field3,
            onValueChange = { field3 = it },
            label = { Text(text = "Campo 3") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp)) // Espaciado entre campos

        TextField(
            value = field4,
            onValueChange = { field4 = it },
            label = { Text(text = "Campo 4") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp)) // Espaciado entre campos

        TextField(
            value = field5,
            onValueChange = { field5 = it },
            label = { Text(text = "Campo 5") },
            modifier = Modifier.fillMaxWidth()
        )

        // Un Spacer grande que empuja los botones hacia abajo
        Spacer(modifier = Modifier.weight(1f))

        // Botones en la parte inferior
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween // Espaciado entre botones
        ) {
            Button(
                onClick = { onDisconnect() }, // Acción para desconectar
                modifier = Modifier.weight(1f) // Botón ocupa el mismo espacio que el otro
            ) {
                Text(text = "Cancelar")
            }
            Spacer(modifier = Modifier.width(16.dp)) // Espacio entre los botones

            Button(
                onClick = {
                    // Lógica para guardar los datos
                    println("Datos guardados: $field1, $field2, $field3, $field4, $field5")
                },
                modifier = Modifier.weight(1f) // Botón ocupa el mismo espacio que el otro
            ) {
                Text(text = "Guardar")
            }
        }
    }
}