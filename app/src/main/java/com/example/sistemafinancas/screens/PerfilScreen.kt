package com.example.sistemafinancas.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sistemafinancas.R

@Composable
fun TelaPerfil(aoSairDaConta: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

      
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(Color(0xFFF2F2F2), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_profile),
                contentDescription = "Foto de perfil",
                modifier = Modifier.size(40.dp),
                tint = Color.DarkGray
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

       
        ItemMenuPerfil(
            texto = "Preferências da conta",
            icone = R.drawable.ic_settings,
            onClick = { }
        )

        ItemMenuPerfil(
            texto = "Notificações",
            icone = R.drawable.ic_bell,
            onClick = { }
        )

        ItemMenuPerfil(
            texto = "Metas e orçamento",
            icone = R.drawable.ic_flag,
            onClick = { }
        )

        ItemMenuPerfil(
            texto = "Contas conectadas",
            icone = R.drawable.ic_profile,
            onClick = { }
        )

        Spacer(modifier = Modifier.height(32.dp))

     
        OutlinedButton(
            onClick = aoSairDaConta,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Sair da conta", color = Color.Red)
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}


@Composable
fun ItemMenuPerfil(texto: String, icone: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFF2F2F2))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Color.White, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = icone),
                    contentDescription = "Ícone de $texto",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Black
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = texto,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
        }
    }
}
