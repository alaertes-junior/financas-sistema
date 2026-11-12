package com.example.sistemafinancas.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sistemafinancas.ui.viewmodel.ReceitaViewModel

@Composable
fun ReceitaEditarScreen(receitaId: Long, viewModel: ReceitaViewModel, voltar: () -> Unit) {
    LaunchedEffect(receitaId) { viewModel.buscarPorId(receitaId) }
    LaunchedEffect(viewModel.salvo) {
        if (viewModel.salvo) { viewModel.consumirSalvo(); voltar() }
    }
    if (viewModel.usuarioAtualId == null) { Text("Faça login para continuar."); return }

    Column(
        modifier = Modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Editar receita", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text(text = "Atualize os dados da receita", color = Color.Gray)
        viewModel.mensagem?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        if (viewModel.carregando) CircularProgressIndicator()

        if (viewModel.receita != null) {
            ReceitaFormulario(viewModel)
            Button(
                onClick = viewModel::salvar,
                enabled = !viewModel.carregando && !viewModel.salvo,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) { Text("Salvar") }
        } else if (!viewModel.carregando) {
            OutlinedButton(
                onClick = { viewModel.buscarPorId(receitaId) },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Tentar carregar novamente") }
        }
        OutlinedButton(
            onClick = voltar,
            enabled = !viewModel.carregando,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Voltar para receitas") }
    }
}
