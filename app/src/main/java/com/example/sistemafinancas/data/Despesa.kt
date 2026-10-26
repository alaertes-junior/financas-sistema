package com.example.sistemafinancas.data

data class Despesa(
    val id: Long,
    val usuarioId: Long,
    val descricao: String,
    val tipo: String,
    val valorCentavos: Long,
    val parcelas: Int,
    val parcelasPagas: Int
) {
    val valorParcelaCentavos: Long get() = (valorCentavos + parcelas / 2) / parcelas
    val quitada: Boolean get() = parcelasPagas >= parcelas
    val valorPagoCentavos: Long
        get() = if (quitada) valorCentavos else minOf(valorParcelaCentavos * parcelasPagas, valorCentavos)
}
