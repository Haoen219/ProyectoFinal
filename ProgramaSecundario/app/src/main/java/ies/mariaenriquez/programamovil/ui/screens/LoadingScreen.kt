package ies.mariaenriquez.programamovil.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ies.mariaenriquez.programamovil.R
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(onLoadingComplete: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2000) // Espera 2 segundos
        onLoadingComplete()
    }

    // UI de la pantalla de carga
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = Color.White
            )
        ,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = stringResource(id = R.string.logo_Description),
                modifier = Modifier.size(128.dp) // Ajusta el tamaño del logo
            )
            Spacer(modifier = Modifier.height(16.dp))
            // Circular Progress Indicator
            CircularProgressIndicator(color = colorResource(id = R.color.main))
        }
    }
}