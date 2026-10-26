package com.example.sistemafinancas.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.sistemafinancas.R

@Composable
fun Menu(navController: NavHostController) {
    val entradaAtual by navController.currentBackStackEntryAsState()
    val destino = entradaAtual?.destination

    val emMetas = destino?.hasRoute<Metas>() == true
            || destino?.hasRoute<CadastrarMeta>() == true
            || destino?.hasRoute<MetaEditar>() == true
    val emDespesas = destino?.hasRoute<Despesas>() == true
            || destino?.hasRoute<CadastrarDespesa>() == true
            || destino?.hasRoute<DespesaEditar>() == true
    val emUsuarios = destino?.hasRoute<Usuarios>() == true
            || destino?.hasRoute<CadastrarUsuario>() == true
            || destino?.hasRoute<UsuarioEditar>() == true
    val emPerfil = destino?.hasRoute<Perfil>() == true

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .shadow(elevation = 8.dp, shape = CircleShape)
                .background(Color.Black, CircleShape)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ItemMenu(R.drawable.ic_statistic, "Metas", emMetas) {
                navController.navigate(Metas) { launchSingleTop = true }
            }
            ItemMenu(R.drawable.ic_dollar, "Despesas", emDespesas) {
                navController.navigate(Despesas) { launchSingleTop = true }
            }
            ItemMenu(R.drawable.ic_profile, "Usuários", emUsuarios) {
                navController.navigate(Usuarios) { launchSingleTop = true }
            }
            ItemMenu(R.drawable.ic_settings, "Perfil", emPerfil) {
                navController.navigate(Perfil) { launchSingleTop = true }
            }
        }
    }
}

@Composable
private fun RowScope.ItemMenu(
    icone: Int,
    descricao: String,
    selecionado: Boolean,
    aoClicar: () -> Unit
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .padding(horizontal = 4.dp)
            .height(48.dp)
            .clip(CircleShape)
            .background(if (selecionado) Color(0xFF3A3A3A) else Color.Transparent)
            .clickable { aoClicar() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = icone),
            contentDescription = "Ícone de $descricao",
            tint = if (selecionado) Color.White else Color.Gray,
            modifier = Modifier.size(26.dp)
        )
    }
}
