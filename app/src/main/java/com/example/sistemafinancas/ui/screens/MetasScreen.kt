package com.example.sistemafinancas.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sistemafinancas.R
import com.example.sistemafinancas.data.Meta
import com.example.sistemafinancas.ui.viewmodel.MetaViewModel
import com.example.sistemafinancas.validation.Formatos

@Composable
fun MetasScreen(
    viewModel: MetaViewModel,
    irParaCadastro: () -> Unit,
    irParaEdicao: (Long) -> Unit
) {
    if (viewModel.usuarioAtualId == null) { Text("Faça login para continuar."); return }

    var excluirId by rememberSaveable { mutableStateOf<Long?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Metas", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
        Text(
            text = "Suas poupanças em andamento",
            color = Color.Gray,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        viewModel.mensagem?.let { Text(it) }
        if (viewModel.carregando) CircularProgressIndicator()
        if (!viewModel.carregando && viewModel.metas.isEmpty()) Text("Nenhuma meta cadastrada.")

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(viewModel.metas, key = { it.id }) { meta ->
                ItemMetaCard(
                    meta = meta,
                    habilitado = !viewModel.carregando,
                    aoEditar = { irParaEdicao(meta.id) },
                    aoExcluir = { excluirId = meta.id }
                )
            }
        }

        Button(
            onClick = irParaCadastro,
            enabled = !viewModel.carregando,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text("+ Nova poupança", fontSize = 16.sp)
        }
    }

    val selecionada = viewModel.metas.firstOrNull { it.id == excluirId }
    if (selecionada != null) {
        AlertDialog(
            onDismissRequest = { excluirId = null },
            title = { Text("Excluir meta?") },
            text = { Text("Excluir \"${selecionada.nome}\"? Esta ação não pode ser desfeita.") },
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
private fun ItemMetaCard(meta: Meta, habilitado: Boolean, aoEditar: () -> Unit, aoExcluir: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F2F2)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color.White, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = obterIconePorCategoria(meta.categoria)),
                        contentDescription = "Ícone de ${meta.categoria}",
                        modifier = Modifier.size(32.dp),
                        tint = Color.Black
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = meta.nome,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            modifier = Modifier.weight(1f)
                        )

                        if (meta.tipo.isNotBlank()) {
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFE0E0E0), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = meta.tipo.uppercase(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.DarkGray
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${Formatos.formatarMoeda(meta.valorAtualCentavos)} de ${Formatos.formatarMoeda(meta.valorAlvoCentavos)}",
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
                                    (meta.valorAtualCentavos.toFloat() / meta.valorAlvoCentavos.toFloat())
                                        .coerceIn(0f, 1f)
                                )
                                .height(8.dp)
                                .background(Color.Black, RoundedCornerShape(4.dp))
                        )
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = aoEditar, enabled = habilitado) { Text("Editar") }
                TextButton(onClick = aoExcluir, enabled = habilitado) { Text("Excluir") }
            }
        }
    }
}

private fun obterIconePorCategoria(categoria: String): Int = when (categoria) {
    "Reserva" -> R.drawable.ic_saving
    "Automóvel" -> R.drawable.ic_car
    "Viagem" -> R.drawable.ic_airplane
    "Casa própria" -> R.drawable.ic_home
    else -> R.drawable.ic_dots
}
