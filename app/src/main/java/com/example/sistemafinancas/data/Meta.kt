package com.example.sistemafinancas.data

data class Meta(
    val id: Long,
    val usuarioId: Long,
    val nome: String,
    val categoria: String,
    val tipo: String,
    val valorAlvoCentavos: Long,
    val valorAtualCentavos: Long,
    /** Caminho (em context.filesDir) da foto escolhida na galeria, ou null. */
    val imagemUri: String? = null
)
