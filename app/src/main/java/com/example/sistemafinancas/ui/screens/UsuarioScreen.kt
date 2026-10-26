package com.example.sistemafinancas.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sistemafinancas.ui.viewmodel.UsuarioViewModel

@Composable
fun UsuarioScreen(
    viewModel: UsuarioViewModel,
    irParaCadastro: () -> Unit,
    irParaEdicao: (Long) -> Unit
) {
    if (viewModel.usuarioAtualId == null) { Text("Faça login para continuar."); return }

    var excluirId by rememberSaveable { mutableStateOf<Long?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Usuários", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                Button(
                    onClick = irParaCadastro,
                    enabled = !viewModel.carregando,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) { Text("Novo usuário") }
                TextButton(onClick = viewModel::listar, enabled = !viewModel.carregando) { Text("Atualizar lista") }
                viewModel.mensagem?.let { Text(it) }
                if (viewModel.carregando) CircularProgressIndicator()
                if (!viewModel.carregando && viewModel.usuarios.isEmpty()) Text("Nenhum usuário cadastrado.")
            }
        }
        items(viewModel.usuarios, key = { it.id }) { usuario ->
            val ehOProprio = usuario.id == viewModel.usuarioAtualId
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F2F2)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(usuario.nome, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text(usuario.email, color = Color.Gray)
                    if (ehOProprio) Text("Você está conectado.")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick = { irParaEdicao(usuario.id) }, enabled = !viewModel.carregando) { Text("Editar") }
                        TextButton(
                            onClick = { excluirId = usuario.id },
                            enabled = !viewModel.carregando && !ehOProprio
                        ) { Text("Excluir") }
                    }
                }
            }
        }
    }

    val selecionado = viewModel.usuarios.firstOrNull { it.id == excluirId }
    if (selecionado != null) {
        AlertDialog(
            onDismissRequest = { excluirId = null },
            title = { Text("Excluir usuário?") },
            text = {
                Text(
                    "Excluir \"${selecionado.nome}\"? Todas as metas deste usuário também serão apagadas. " +
                        "Esta ação não pode ser desfeita."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { excluirId = null; viewModel.excluir(selecionado.id) },
                    enabled = !viewModel.carregando
                ) { Text("Excluir") }
            },
            dismissButton = { TextButton(onClick = { excluirId = null }) { Text("Cancelar") } }
        )
    }
}
