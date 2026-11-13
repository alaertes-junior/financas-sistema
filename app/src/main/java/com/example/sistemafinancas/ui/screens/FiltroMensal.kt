package com.example.sistemafinancas.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sistemafinancas.validation.Formatos

@Composable
internal fun FiltroMensal(
    mesSelecionado: Int?,
    anoSelecionado: Int,
    habilitado: Boolean,
    aoSelecionarMes: (Int?) -> Unit,
    aoAlterarAno: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Período: ${if (mesSelecionado == null) "Todos de $anoSelecionado" else Formatos.formatarCompetencia(mesSelecionado, anoSelecionado)}",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = { aoAlterarAno(-1) }, enabled = habilitado && anoSelecionado > 2000) {
                    Text("<", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                }
                Text(text = anoSelecionado.toString(), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                TextButton(onClick = { aoAlterarAno(1) }, enabled = habilitado && anoSelecionado < 2100) {
                    Text(">", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            BotaoMesChip(
                rotulo = "Todos",
                selecionado = mesSelecionado == null,
                habilitado = habilitado,
                aoClicar = { aoSelecionarMes(null) }
            )
            Formatos.MESES_CURTOS.forEach { (numero, nomeCurto) ->
                BotaoMesChip(
                    rotulo = nomeCurto,
                    selecionado = mesSelecionado == numero,
                    habilitado = habilitado,
                    aoClicar = { aoSelecionarMes(numero) }
                )
            }
        }
    }
}

@Composable
internal fun CamposCompetenciaMensal(
    mes: Int,
    ano: String,
    habilitado: Boolean,
    aoMudarMes: (Int) -> Unit,
    aoMudarAno: (String) -> Unit
) {
    Text(text = "Mês de referência", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Formatos.MESES_CURTOS.forEach { (numero, nomeCurto) ->
            BotaoMesChip(
                rotulo = nomeCurto,
                selecionado = mes == numero,
                habilitado = habilitado,
                aoClicar = { aoMudarMes(numero) }
            )
        }
    }

    Spacer(modifier = Modifier.height(4.dp))

    OutlinedTextField(
        value = ano,
        onValueChange = aoMudarAno,
        label = { Text("Ano (ex.: 2026)") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        enabled = habilitado,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun BotaoMesChip(
    rotulo: String,
    selecionado: Boolean,
    habilitado: Boolean,
    aoClicar: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (selecionado) Color.Black else Color(0xFFE0E0E0))
            .clickable(enabled = habilitado) { aoClicar() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = rotulo,
            color = if (selecionado) Color.White else Color.DarkGray,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp
        )
    }
}

