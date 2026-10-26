package com.example.sistemafinancas.validation

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

object Formatos {
    private val LOCALE_BR: Locale = Locale.forLanguageTag("pt-BR")
    private val VALOR = Regex("""\d{1,9}([.,]\d{1,2})?""")

    /** "1500,50" ou "1500.50" -> 150050. Lança IllegalArgumentException se inválido. */
    fun paraCentavos(texto: String): Long {
        val limpo = texto.trim()
        require(VALOR.matches(limpo)) { "Informe um valor válido. Ex.: 1500,50" }
        return BigDecimal(limpo.replace(',', '.')).movePointRight(2).longValueExact()
    }

    /** 150050 -> "1500,50" (para preencher campo de edição). */
    fun centavosParaTexto(centavos: Long): String =
        BigDecimal.valueOf(centavos, 2).toPlainString().replace('.', ',')

    /** 150050 -> "R$ 1.500,50" (para exibição). */
    fun formatarMoeda(centavos: Long): String =
        NumberFormat.getCurrencyInstance(LOCALE_BR).format(BigDecimal.valueOf(centavos, 2))
}
