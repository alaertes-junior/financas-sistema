package com.example.sistemafinancas.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sistemafinancas.data.Receita
import com.example.sistemafinancas.ui.viewmodel.ReceitaViewModel
import com.example.sistemafinancas.validation.Formatos

@Composable
fun ReceitasScreen(
    viewModel: ReceitaViewModel,
    irParaCadastro: () -> Unit,
    irParaEdicao: (Long) -> Unit
) {
    if (viewModel.usuarioAtualId == null) { Text("Faça login para continuar."); return }

    var excluirId by rememberSaveable { mutableStateOf<Long?>(null) }

    val totalRecebido = viewModel.receitas.filter { it.recebida }.sumOf { it.valorCentavos }
    val totalPrevisto = viewModel.receitas.filterNot { it.recebida }.sumOf { it.valorCentavos }

    // Os insets do sistema já são tratados pelo Scaffold da MainActivity.
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)) {
                Text(text = "Receitas", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                Text(
                    text = "Recebido: ${Formatos.formatarMoeda(totalRecebido)}  •  A receber: ${Formatos.formatarMoeda(totalPrevisto)}",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                FiltroMensal(
                    mesSelecionado = viewModel.filtroMes,
                    anoSelecionado = viewModel.filtroAno,
                    habilitado = !viewModel.carregando,
                    aoSelecionarMes = viewModel::selecionarMesFiltro,
                    aoAlterarAno = viewModel::alterarAnoFiltro
                )
            }
        },
        bottomBar = {
            Button(
                onClick = irParaCadastro,
                enabled = !viewModel.carregando,
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) { Text("+ Nova receita", fontSize = 16.sp) }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp)) {
            viewModel.mensagem?.let { Text(it) }
            if (viewModel.carregando) CircularProgressIndicator()
            if (!viewModel.carregando && viewModel.receitas.isEmpty()) Text("Nenhuma receita cadastrada para este período.")

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(viewModel.receitas, key = { it.id }) { receita ->
                    ItemReceitaCard(
                        receita = receita,
                        habilitado = !viewModel.carregando,
                        aoAlternarRecebida = { viewModel.alternarRecebida(receita) },
                        aoEditar = { irParaEdicao(receita.id) },
                        aoExcluir = { excluirId = receita.id }
                    )
                }
            }
        }
    }

    val selecionada = viewModel.receitas.firstOrNull { it.id == excluirId }
    if (selecionada != null) {
        AlertDialog(
            onDismissRequest = { excluirId = null },
            title = { Text("Excluir receita?") },
            text = { Text("Excluir \"${selecionada.descricao}\"? Esta ação não pode ser desfeita.") },
            confirmButton = {
                TextButton(
                    onClick = { excluirId = null; viewModel.excluir(selecionada.id) },
                    enabled = !viewModel.carregando
                ) { Text("Excluir") }
            },
            dismissButton = { TextButton(onClick = { excluirId = null }) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun ItemReceitaCard(
    receita: Receita,
    habilitado: Boolean,
    aoAlternarRecebida: () -> Unit,
    aoEditar: () -> Unit,
    aoExcluir: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F2F2)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = receita.descricao,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.weight(1f)
                )
                Etiqueta(receita.tipo.uppercase())
            }
            Text(
                text = "${Formatos.formatarCompetencia(receita.mes, receita.ano)} • ${receita.categoria} • ${Formatos.formatarMoeda(receita.valorCentavos)}",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = if (receita.recebida) "Recebida" else "A receber",
                color = if (receita.recebida) Color(0xFF2E7D32) else Color.Gray,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = aoAlternarRecebida, enabled = habilitado) {
                    Text(if (receita.recebida) "Desmarcar" else "Marcar recebida")
                }
                TextButton(onClick = aoEditar, enabled = habilitado) { Text("Editar") }
                TextButton(onClick = aoExcluir, enabled = habilitado) { Text("Excluir") }
            }
        }
    }
}

@Composable
internal fun Etiqueta(texto: String) {
    Box(
        modifier = Modifier
            .background(Color(0xFFE0E0E0), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = texto, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
    }
}
