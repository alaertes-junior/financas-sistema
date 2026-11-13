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
import androidx.compose.material3.Checkbox
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
import com.example.sistemafinancas.ui.viewmodel.ReceitaViewModel
import com.example.sistemafinancas.validation.ReceitaValidator

@Composable
fun ReceitaFormulario(viewModel: ReceitaViewModel) {
    OutlinedTextField(
        value = viewModel.descricao, onValueChange = viewModel::mudarDescricao,
        label = { Text("Descrição") },
        placeholder = { Text("Ex: Salário empresa X") },
        singleLine = true,
        enabled = !viewModel.carregando, modifier = Modifier.fillMaxWidth()
    )

    Text(text = "Categoria", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    OpcoesRadio(ReceitaValidator.CATEGORIAS, viewModel.categoria, !viewModel.carregando, viewModel::mudarCategoria)

    Text(text = "Tipo da receita", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    OpcoesRadio(ReceitaValidator.TIPOS, viewModel.tipo, !viewModel.carregando, viewModel::mudarTipo)

    OutlinedTextField(
        value = viewModel.valor, onValueChange = viewModel::mudarValor,
        label = { Text("Valor (ex.: 3500,00)") }, singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        enabled = !viewModel.carregando, modifier = Modifier.fillMaxWidth()
    )

    CamposCompetenciaMensal(
        mes = viewModel.mes,
        ano = viewModel.ano,
        habilitado = !viewModel.carregando,
        aoMudarMes = viewModel::mudarMes,
        aoMudarAno = viewModel::mudarAno
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(enabled = !viewModel.carregando) { viewModel.mudarRecebida(!viewModel.recebida) }
    ) {
        Checkbox(
            checked = viewModel.recebida,
            onCheckedChange = viewModel::mudarRecebida,
            enabled = !viewModel.carregando
        )
        Text(text = "Já recebida", fontSize = 16.sp)
    }
}

@Composable
internal fun OpcoesRadio(
    opcoes: List<String>,
    selecionada: String,
    habilitado: Boolean,
    aoSelecionar: (String) -> Unit,
    rotulo: (String) -> String = { it }
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        opcoes.forEach { opcao ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(enabled = habilitado) { aoSelecionar(opcao) }
                    .padding(vertical = 4.dp, horizontal = 4.dp)
            ) {
                RadioButton(selected = selecionada == opcao, onClick = { aoSelecionar(opcao) }, enabled = habilitado)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = rotulo(opcao), fontSize = 16.sp)
            }
        }
    }
}
