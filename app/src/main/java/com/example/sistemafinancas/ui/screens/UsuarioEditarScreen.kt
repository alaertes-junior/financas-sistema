package com.example.sistemafinancas.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sistemafinancas.ui.viewmodel.UsuarioViewModel

@Composable
fun UsuarioEditarScreen(usuarioId: Long, viewModel: UsuarioViewModel, voltar: () -> Unit) {
    var novaSenha by remember { mutableStateOf("") }
    var senhaVisivel by remember { mutableStateOf(false) }

    LaunchedEffect(usuarioId) { viewModel.buscarPorId(usuarioId) }
    LaunchedEffect(viewModel.salvo) {
        if (viewModel.salvo) { viewModel.consumirSalvo(); voltar() }
    }
    if (viewModel.usuarioAtualId == null) { Text("Faça login para continuar."); return }

    Column(
        modifier = Modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Editar usuário", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        viewModel.mensagem?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        if (viewModel.carregando) CircularProgressIndicator()

        val usuario = viewModel.usuario
        if (usuario != null) {
            OutlinedTextField(
                value = viewModel.nome, onValueChange = viewModel::mudarNome,
                label = { Text("Nome") }, singleLine = true,
                enabled = !viewModel.carregando, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = usuario.email, onValueChange = {},
                label = { Text("E-mail") }, singleLine = true,
                readOnly = true, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = novaSenha,
                onValueChange = { if (it.length <= 128) novaSenha = it },
                label = { Text("Nova senha (deixe em branco para manter)") },
                visualTransformation = if (senhaVisivel) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { senhaVisivel = !senhaVisivel }) {
                        Icon(
                            if (senhaVisivel) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (senhaVisivel) "Esconder senha" else "Mostrar senha"
                        )
                    }
                },
                singleLine = true,
                enabled = !viewModel.carregando, modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = { viewModel.salvar(novaSenha); novaSenha = "" },
                enabled = !viewModel.carregando && !viewModel.salvo,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) { Text("Salvar") }
        } else if (!viewModel.carregando) {
            OutlinedButton(
                onClick = { viewModel.buscarPorId(usuarioId) },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Tentar carregar novamente") }
        }
        OutlinedButton(
            onClick = voltar,
            enabled = !viewModel.carregando,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Voltar para usuários") }
    }
}
