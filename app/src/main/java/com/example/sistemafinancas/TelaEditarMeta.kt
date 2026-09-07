package com.example.sistemafinancas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TelaEditarMeta(metaOriginal: Meta, aoSalvar: (Meta) -> Unit, aoCancelar: () -> Unit) {
    var nome by remember { mutableStateOf(metaOriginal.nome) }
    var tipo by remember { mutableStateOf(metaOriginal.tipo) }
    var categoriaSelecionada by remember { mutableStateOf(metaOriginal.categoria) }
    var valorObjetivoStr by remember { mutableStateOf(metaOriginal.valorObjetivo.toString().removeSuffix(".0")) }
    var valorGuardadoStr by remember { mutableStateOf(metaOriginal.valorGuardado.toString().removeSuffix(".0")) }

    val opcoesCategoria = listOf("Reserva", "Automóvel", "Viagem", "Casa própria", "Outros")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Text(text = "Editar poupança", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text(text = "Atualize os valores da sua meta", color = Color.Gray, modifier = Modifier.padding(bottom = 24.dp))

        OutlinedTextField(value = nome, onValueChange = { nome = it }, label = { Text("Nome da meta") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Selecione a categoria (Ícone)", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            opcoesCategoria.forEach { opcao ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { categoriaSelecionada = opcao }
                        .padding(vertical = 4.dp, horizontal = 4.dp)) {
                    RadioButton(selected = categoriaSelecionada == opcao, onClick = { categoriaSelecionada = opcao })
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = opcao, fontSize = 16.sp)
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = tipo,
            onValueChange = { tipo = it },
            label = { Text("Tag de identificação (Opcional)") },
            modifier = Modifier.fillMaxWidth(), singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = valorObjetivoStr,
            onValueChange = { valorObjetivoStr = it.replace(",", ".") },
            label = { Text("Valor objetivo") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(), singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = valorGuardadoStr,
            onValueChange = { valorGuardadoStr = it.replace(",", ".") },
            label = { Text("Valor já guardado") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(), singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            OutlinedButton(onClick = aoCancelar, modifier = Modifier.weight(1f)) { Text("Cancelar") }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = {
                    aoSalvar(Meta(metaOriginal.id, nome, categoriaSelecionada, tipo, valorGuardadoStr.toDoubleOrNull()?:0.0, valorObjetivoStr.toDoubleOrNull()?:0.0))
                          },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("Salvar")
            }
        }
    }
}