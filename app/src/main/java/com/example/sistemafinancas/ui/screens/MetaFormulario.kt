package com.example.sistemafinancas.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.ui.graphics.Color
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
import com.example.sistemafinancas.ui.viewmodel.MetaViewModel
import com.example.sistemafinancas.validation.MetaValidator

@Composable
fun MetaFormulario(viewModel: MetaViewModel) {
    // Recurso nativo: abre a biblioteca de imagens (galeria) do celular.
    val abrirGaleria = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) viewModel.selecionarImagem(uri)
    }
    val imagem = lembrarImagem(viewModel.imagemUri)

    Text(text = "Foto da meta (opcional)", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF2F2F2)),
            contentAlignment = Alignment.Center
        ) {
            if (imagem != null) {
                ImagemMeta(imagem, "Foto da meta", Modifier.size(96.dp))
            } else {
                Text("Sem foto", color = Color.Gray, fontSize = 12.sp)
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            OutlinedButton(
                onClick = { abrirGaleria.launch("image/*") },
                enabled = !viewModel.carregando
            ) { Text(if (imagem != null) "Trocar foto" else "Escolher da galeria") }
            if (viewModel.imagemUri != null) {
                TextButton(onClick = viewModel::removerImagem, enabled = !viewModel.carregando) {
                    Text("Remover foto")
                }
            }
        }
    }

    OutlinedTextField(
        value = viewModel.nome, onValueChange = viewModel::mudarNome,
        label = { Text("Nome da meta") },
        placeholder = { Text("Ex: Viagem Chile") },
        singleLine = true,
        enabled = !viewModel.carregando, modifier = Modifier.fillMaxWidth()
    )

    Text(text = "Selecione a categoria (Ícone)", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    Column(modifier = Modifier.fillMaxWidth()) {
        MetaValidator.CATEGORIAS.forEach { opcao ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(enabled = !viewModel.carregando) { viewModel.mudarCategoria(opcao) }
                    .padding(vertical = 4.dp, horizontal = 4.dp)
            ) {
                RadioButton(
                    selected = viewModel.categoria == opcao,
                    onClick = { viewModel.mudarCategoria(opcao) },
                    enabled = !viewModel.carregando
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = opcao, fontSize = 16.sp)
            }
        }
    }

    OutlinedTextField(
        value = viewModel.tipo, onValueChange = viewModel::mudarTipo,
        label = { Text("Tag de identificação (Opcional)") },
        placeholder = { Text("Ex: Férias, Urgência") },
        singleLine = true,
        enabled = !viewModel.carregando, modifier = Modifier.fillMaxWidth()
    )
    OutlinedTextField(
        value = viewModel.valorAlvo, onValueChange = viewModel::mudarValorAlvo,
        label = { Text("Valor objetivo (ex.: 1500,50)") }, singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        enabled = !viewModel.carregando, modifier = Modifier.fillMaxWidth()
    )
    OutlinedTextField(
        value = viewModel.valorAtual, onValueChange = viewModel::mudarValorAtual,
        label = { Text("Valor já guardado (opcional)") }, singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        enabled = !viewModel.carregando, modifier = Modifier.fillMaxWidth()
    )
}
