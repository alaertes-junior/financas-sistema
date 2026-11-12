package com.example.sistemafinancas.data

data class MoedaAcompanhada(
    val id: Long,
    val usuarioId: Long,
    val codigo: String,
    val apelido: String,
    /** Montante desejado na moeda estrangeira, em centésimos (ex.: 150050 = 1.500,50 USD). */
    val quantidadeCentavos: Long,
    /** Cotação desejada em R$, em centavos. 0 = sem cotação-alvo. */
    val cotacaoAlvoCentavos: Long
)
