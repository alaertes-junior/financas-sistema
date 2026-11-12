package com.example.sistemafinancas.data

data class Receita(
    val id: Long,
    val usuarioId: Long,
    val descricao: String,
    val categoria: String,
    val tipo: String,
    val valorCentavos: Long,
    val recebida: Boolean
)
