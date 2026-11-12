package com.example.sistemafinancas.data.remote

import com.example.sistemafinancas.data.CotacaoMoeda
import retrofit2.http.GET
import retrofit2.http.Path

interface CotacaoApi {
    /**
     * Ex.: moedas = "USD-BRL,EUR-BRL".
     * Resposta: { "USDBRL": { ... }, "EURBRL": { ... } }
     */
    @GET("json/last/{moedas}")
    suspend fun buscarCotacoes(@Path("moedas") moedas: String): Map<String, CotacaoMoeda>
}
