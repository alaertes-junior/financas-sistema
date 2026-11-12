package com.example.sistemafinancas.ui.screens

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale

/** Decodifica a foto salva em filesDir. Retorna null se o arquivo não existir mais. */
@Composable
internal fun lembrarImagem(caminho: String?): ImageBitmap? = remember(caminho) {
    caminho?.let { BitmapFactory.decodeFile(it)?.asImageBitmap() }
}

@Composable
internal fun ImagemMeta(imagem: ImageBitmap, descricao: String, modifier: Modifier = Modifier) {
    Image(
        bitmap = imagem,
        contentDescription = descricao,
        contentScale = ContentScale.Crop,
        modifier = modifier
    )
}
