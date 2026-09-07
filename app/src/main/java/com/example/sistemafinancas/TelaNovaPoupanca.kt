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
fun TelaNovaPoupanca(aoCriarMeta: (String, String, String, Double, Double) -> Unit, aoCancelar: () -> Unit) {
    var nome by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("") }
    var categoriaSelecionada by remember { mutableStateOf("Casa própria") }
    var valorObjetivoStr by remember { mutableStateOf("") }
    var valorGuardadoStr by remember { mutableStateOf("") }

    val opcoesCategoria = listOf("Reserva", "Automóvel", "Viagem", "Casa própria", "Outros")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Text(text = "Nova poupança", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text(text = "Defina uma meta para guardar dinheiro", color = Color.Gray, modifier = Modifier.padding(bottom = 24.dp))

        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome da meta") },
            placeholder = { Text("Ex: Viagem Chile") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
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
                        .padding(vertical = 4.dp, horizontal = 4.dp)
                ) {
                    RadioButton(
                        selected = categoriaSelecionada == opcao,
                        onClick = { categoriaSelecionada = opcao }
                    )
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
            placeholder = { Text("Ex: Férias, Urgência") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = valorObjetivoStr,
            onValueChange = { digitado ->
                val texto = digitado.replace(",", ".")
                if (texto.isEmpty() || texto.toDoubleOrNull() != null) {
                    valorObjetivoStr = texto
                }
            },
            label = { Text("Valor objetivo") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = valorGuardadoStr,
            onValueChange = { digitado ->
                val texto = digitado.replace(",", ".")
                if (texto.isEmpty() || texto.toDoubleOrNull() != null) {
                    valorGuardadoStr = texto
                }
            },
            label = { Text("Valor já guardado") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            OutlinedButton(onClick = aoCancelar, modifier = Modifier.weight(1f)) {
                Text("Cancelar")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = {
                    val objetivo = valorObjetivoStr.toDoubleOrNull() ?: 0.0
                    val guardado = valorGuardadoStr.toDoubleOrNull() ?: 0.0
                    if (nome.isNotBlank() && objetivo > 0) {
                        aoCriarMeta(nome, categoriaSelecionada, tipo, guardado, objetivo)
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("Criar")
            }
        }
    }
}

// --- FUNÇÃO PARA PEGAR O ÍCONE ---
@Composable
fun obterIconePorCategoria(categoria: String): Int {
    return when (categoria) {
        "Reserva" -> R.drawable.ic_saving
        "Automóvel" -> R.drawable.ic_car
        "Viagem" -> R.drawable.ic_airplane
        "Casa própria" -> R.drawable.ic_home
        "Outros" -> R.drawable.ic_dots
        else -> R.drawable.ic_dots
    }
}