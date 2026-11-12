package com.example.sistemafinancas.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val URL_BASE = "https://economia.awesomeapi.com.br/"

    val cotacaoApi: CotacaoApi by lazy {
        Retrofit.Builder()
            .baseUrl(URL_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CotacaoApi::class.java)
    }
}
