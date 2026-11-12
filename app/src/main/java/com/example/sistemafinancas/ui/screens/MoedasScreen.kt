package com.example.sistemafinancas.ui.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.MaterialTheme
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
import com.example.sistemafinancas.data.CotacaoMoeda
import com.example.sistemafinancas.data.MoedaAcompanhada
import com.example.sistemafinancas.ui.viewmodel.MoedaViewModel
import com.example.sistemafinancas.validation.Formatos
import com.example.sistemafinancas.validation.MoedaValidator
import java.math.BigDecimal
import java.math.RoundingMode

@Composable
fun MoedasScreen(
    viewModel: MoedaViewModel,
    irParaCadastro: () -> Unit,
    irParaEdicao: (Long) -> Unit
) {
    if (viewModel.usuarioAtualId == null) { Text("Faça login para continuar."); return }

    var excluirId by rememberSaveable { mutableStateOf<Long?>(null) }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)) {
                Text(text = "Moedas", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Cotações em tempo real (AwesomeAPI)",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(
                        onClick = viewModel::atualizarCotacoes,
                        enabled = !viewModel.buscandoCotacoes && viewModel.moedas.isNotEmpty()
                    ) { Text("Atualizar") }
                }
            }
        },
        bottomBar = {
            Button(
                onClick = irParaCadastro,
                enabled = !viewModel.carregando,
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) { Text("+ Nova moeda", fontSize = 16.sp) }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp)) {
            viewModel.mensagem?.let { Text(it) }
            viewModel.erroCotacao?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            if (viewModel.carregando || viewModel.buscandoCotacoes) CircularProgressIndicator()
            if (!viewModel.carregando && viewModel.moedas.isEmpty()) Text("Nenhuma moeda acompanhada.")

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(viewModel.moedas, key = { it.id }) { moeda ->
                    ItemMoedaCard(
                        moeda = moeda,
                        cotacao = viewModel.cotacoes[moeda.codigo],
                        habilitado = !viewModel.carregando,
                        aoEditar = { irParaEdicao(moeda.id) },
                        aoExcluir = { excluirId = moeda.id }
                    )
                }
            }
        }
    }

    val selecionada = viewModel.moedas.firstOrNull { it.id == excluirId }
    if (selecionada != null) {
        AlertDialog(
            onDismissRequest = { excluirId = null },
            title = { Text("Excluir moeda?") },
            text = { Text("Deixar de acompanhar \"${selecionada.apelido}\" (${selecionada.codigo})?") },
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
private fun ItemMoedaCard(
    moeda: MoedaAcompanhada,
    cotacao: CotacaoMoeda?,
    habilitado: Boolean,
    aoEditar: () -> Unit,
    aoExcluir: () -> Unit
) {
    val quantidade = BigDecimal.valueOf(moeda.quantidadeCentavos, 2)
    val cotacaoAtual = cotacao?.bid?.toBigDecimalOrNull()
    val cotacaoAlvo = BigDecimal.valueOf(moeda.cotacaoAlvoCentavos, 2)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F2F2)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = moeda.apelido,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.weight(1f)
                )
                Etiqueta(moeda.codigo)
            }
            Text(
                text = "${Formatos.centavosParaTexto(moeda.quantidadeCentavos)} ${moeda.codigo} • ${MoedaValidator.MOEDAS[moeda.codigo]}",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
            if (cotacaoAtual != null) {
                val variacao = cotacao.pctChange?.let { " ($it%)" } ?: ""
                Text(
                    text = "Cotação atual: ${Formatos.formatarReais(cotacaoAtual)}$variacao",
                    fontSize = 14.sp
                )
                Text(
                    text = "Valor em reais: ${Formatos.formatarReais(quantidade.multiply(cotacaoAtual).setScale(2, RoundingMode.HALF_EVEN))}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            } else {
                Text(text = "Cotação indisponível", color = Color.Gray, fontSize = 14.sp)
            }
            if (moeda.cotacaoAlvoCentavos > 0) {
                val atingida = cotacaoAtual != null && cotacaoAtual <= cotacaoAlvo
                Text(
                    text = "Cotação-alvo: ${Formatos.formatarMoeda(moeda.cotacaoAlvoCentavos)}" +
                        if (atingida) " — atingida!" else "",
                    color = if (atingida) Color(0xFF2E7D32) else Color.Gray,
                    fontSize = 14.sp
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = aoEditar, enabled = habilitado) { Text("Editar") }
                TextButton(onClick = aoExcluir, enabled = habilitado) { Text("Excluir") }
            }
        }
    }
}
