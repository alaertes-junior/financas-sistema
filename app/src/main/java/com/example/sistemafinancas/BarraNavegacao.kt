package com.example.sistemafinancas

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

@Composable
fun BarraNavegacao(telaAtual: String, aoMudarTela: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)
            .background(Color.White),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {

        // --- ÍCONE METAS ---
        val corMetas = if (telaAtual in listOf("metas", "nova_meta", "editar_meta")) Color.Black else Color.Gray
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clickable { aoMudarTela("metas") },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_statistic),
                contentDescription = "Ícone de Metas",
                tint = corMetas,
                modifier = Modifier.size(28.dp)
            )
        }

        // --- ÍCONE PERFIL ---
        val corPerfil = if (telaAtual == "perfil") Color.Black else Color.Gray
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clickable { aoMudarTela("perfil") },
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