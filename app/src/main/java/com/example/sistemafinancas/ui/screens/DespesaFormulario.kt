package com.example.sistemafinancas.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sistemafinancas.ui.viewmodel.DespesaViewModel
import com.example.sistemafinancas.validation.DespesaValidator

@Composable
fun DespesaFormulario(viewModel: DespesaViewModel) {
    OutlinedTextField(
        value = viewModel.descricao, onValueChange = viewModel::mudarDescricao,
        label = { Text("Descrição") },
        placeholder = { Text("Ex: Aluguel, Mercado") },
        singleLine = true,
        enabled = !viewModel.carregando, modifier = Modifier.fillMaxWidth()
    )

    Text(text = "Tipo da despesa", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    Column(modifier = Modifier.fillMaxWidth()) {
        DespesaValidator.TIPOS.forEach { opcao ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(enabled = !viewModel.carregando) { viewModel.mudarTipo(opcao) }
                    .padding(vertical = 4.dp, horizontal = 4.dp)
            ) {
                RadioButton(
                    selected = viewModel.tipo == opcao,
                    onClick = { viewModel.mudarTipo(opcao) },
                    enabled = !viewModel.carregando
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = opcao, fontSize = 16.sp)
            }
        }
    }

    OutlinedTextField(
        value = viewModel.valor, onValueChange = viewModel::mudarValor,
        label = { Text("Valor total (ex.: 1500,50)") }, singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        enabled = !viewModel.carregando, modifier = Modifier.fillMaxWidth()
    )
    OutlinedTextField(
        value = viewModel.parcelas, onValueChange = viewModel::mudarParcelas,
        label = { Text("Número de parcelas (1 = à vista)") }, singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        enabled = !viewModel.carregando, modifier = Modifier.fillMaxWidth()
    )
    OutlinedTextField(
        value = viewModel.parcelasPagas, onValueChange = viewModel::mudarParcelasPagas,
        label = { Text("Parcelas já pagas (opcional)") }, singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        enabled = !viewModel.carregando, modifier = Modifier.fillMaxWidth()
    )
}
