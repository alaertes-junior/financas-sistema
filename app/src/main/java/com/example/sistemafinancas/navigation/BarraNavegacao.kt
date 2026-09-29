package com.example.sistemafinancas.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import com.example.sistemafinancas.R

@Composable
fun BarraNavegacao(
    destino: NavDestination?,
    aoNavegarParaMetas: () -> Unit,
    aoNavegarParaPerfil: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)
            .background(Color.White),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {

        
        val corMetas = if (
            destino?.hasRoute<Metas>() == true
            || destino?.hasRoute<NovaMeta>() == true
            || destino?.hasRoute<EditarMeta>() == true
        ) Color.Black else Color.Gray
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clickable { aoNavegarParaMetas() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_statistic),
                contentDescription = "Ícone de Metas",
                tint = corMetas,
                modifier = Modifier.size(28.dp)
            )
        }

      
        val corPerfil = if (destino?.hasRoute<Perfil>() == true) Color.Black else Color.Gray
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clickable { aoNavegarParaPerfil() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_profile),
                contentDescription = "Ícone de Perfil",
                tint = corPerfil,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
