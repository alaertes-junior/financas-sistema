package com.example.sistemafinancas.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import com.example.sistemafinancas.data.Despesa
import com.example.sistemafinancas.ui.viewmodel.DespesaViewModel
import com.example.sistemafinancas.validation.DespesaValidator
import com.example.sistemafinancas.validation.Formatos

@Composable
fun DespesasScreen(
    viewModel: DespesaViewModel,
    irParaCadastro: () -> Unit,
    irParaEdicao: (Long) -> Unit
) {
    if (viewModel.usuarioAtualId == null) { Text("Faça login para continuar."); return }

    var excluirId by rememberSaveable { mutableStateOf<Long?>(null) }

    val totalFixas = viewModel.despesas
        .filter { it.tipo == DespesaValidator.TIPO_FIXA }.sumOf { it.valorCentavos }
    val totalVariaveis = viewModel.despesas
        .filter { it.tipo == DespesaValidator.TIPO_VARIAVEL }.sumOf { it.valorCentavos }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Despesas", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
        Text(
            text = "Fixas: ${Formatos.formatarMoeda(totalFixas)}  •  Variáveis: ${Formatos.formatarMoeda(totalVariaveis)}",
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        viewModel.mensagem?.let { Text(it) }
        if (viewModel.carregando) CircularProgressIndicator()
        if (!viewModel.carregando && viewModel.despesas.isEmpty()) Text("Nenhuma despesa cadastrada.")

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(viewModel.despesas, key = { it.id }) { despesa ->
                ItemDespesaCard(
                    despesa = despesa,
                    habilitado = !viewModel.carregando,
                    aoPagarParcela = { viewModel.pagarParcela(despesa.id) },
                    aoEditar = { irParaEdicao(despesa.id) },
                    aoExcluir = { excluirId = despesa.id }
                )
            }
        }

        Button(
            onClick = irParaCadastro,
            enabled = !viewModel.carregando,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text("+ Nova despesa", fontSize = 16.sp)
        }
    }

    val selecionada = viewModel.despesas.firstOrNull { it.id == excluirId }
    if (selecionada != null) {
        AlertDialog(
            onDismissRequest = { excluirId = null },
            title = { Text("Excluir despesa?") },
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
private fun ItemDespesaCard(
    despesa: Despesa,
    habilitado: Boolean,
    aoPagarParcela: () -> Unit,
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
                    text = despesa.descricao,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .background(Color(0xFFE0E0E0), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = despesa.tipo.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                }
            }
            Text(
                text = "${Formatos.formatarMoeda(despesa.valorPagoCentavos)} de ${Formatos.formatarMoeda(despesa.valorCentavos)}",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = if (despesa.parcelas == 1) "À vista"
                else "${despesa.parcelasPagas} de ${despesa.parcelas} parcelas pagas • ${Formatos.formatarMoeda(despesa.valorParcelaCentavos)} cada",
                color = Color.Gray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(Color(0xFFD6D6D6), RoundedCornerShape(4.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(
                            (despesa.parcelasPagas.toFloat() / despesa.parcelas.toFloat()).coerceIn(0f, 1f)
                        )
                        .height(8.dp)
                        .background(Color.Black, RoundedCornerShape(4.dp))
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (despesa.quitada) {
                    TextButton(onClick = {}, enabled = false) { Text("Quitada") }
                } else {
                    TextButton(onClick = aoPagarParcela, enabled = habilitado) {
                        Text(if (despesa.parcelas == 1) "Marcar como paga" else "Pagar parcela")
                    }
                }
                TextButton(onClick = aoEditar, enabled = habilitado) { Text("Editar") }
                TextButton(onClick = aoExcluir, enabled = habilitado) { Text("Excluir") }
            }
        }
    }
}
