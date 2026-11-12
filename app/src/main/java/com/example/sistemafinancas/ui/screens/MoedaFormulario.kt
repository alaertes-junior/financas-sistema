package com.example.sistemafinancas.ui.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import com.example.sistemafinancas.ui.viewmodel.MoedaViewModel
import com.example.sistemafinancas.validation.MoedaValidator

@Composable
fun MoedaFormulario(viewModel: MoedaViewModel) {
    Text(text = "Moeda", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    OpcoesRadio(
        opcoes = MoedaValidator.MOEDAS.keys.toList(),
        selecionada = viewModel.codigo,
        habilitado = !viewModel.carregando,
        aoSelecionar = viewModel::mudarCodigo,
        rotulo = { "$it — ${MoedaValidator.MOEDAS[it]}" }
    )

    OutlinedTextField(
        value = viewModel.apelido, onValueChange = viewModel::mudarApelido,
        label = { Text("Apelido") },
        placeholder = { Text("Ex: Viagem Europa") },
        singleLine = true,
        enabled = !viewModel.carregando, modifier = Modifier.fillMaxWidth()
    )
    OutlinedTextField(
        value = viewModel.quantidade, onValueChange = viewModel::mudarQuantidade,
        label = { Text("Quantidade desejada em ${viewModel.codigo} (ex.: 1000,00)") }, singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        enabled = !viewModel.carregando, modifier = Modifier.fillMaxWidth()
    )
    OutlinedTextField(
        value = viewModel.cotacaoAlvo, onValueChange = viewModel::mudarCotacaoAlvo,
        label = { Text("Cotação-alvo em R$ (opcional)") }, singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        enabled = !viewModel.carregando, modifier = Modifier.fillMaxWidth()
    )
}
