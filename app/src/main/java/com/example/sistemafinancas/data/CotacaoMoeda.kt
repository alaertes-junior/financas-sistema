package com.example.sistemafinancas.data

/** DTO da resposta da AwesomeAPI (economia.awesomeapi.com.br). Preenchido pelo Gson. */
data class CotacaoMoeda(
    val code: String?,
    val codein: String?,
    val name: String?,
    val bid: String?,
    val ask: String?,
    val pctChange: String?
)
