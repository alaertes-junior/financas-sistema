package com.example.sistemafinancas.validation

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

object Formatos {
    private val LOCALE_BR: Locale = Locale.forLanguageTag("pt-BR")
    private val VALOR = Regex("""\d{1,9}([.,]\d{1,2})?""")

    val MESES_CURTOS = listOf(
        1 to "Jan", 2 to "Fev", 3 to "Mar", 4 to "Abr",
        5 to "Mai", 6 to "Jun", 7 to "Jul", 8 to "Ago",
        9 to "Set", 10 to "Out", 11 to "Nov", 12 to "Dez"
    )

    fun nomeMesCurto(mes: Int): String =
        MESES_CURTOS.firstOrNull { it.first == mes }?.second ?: mes.toString()

    fun formatarCompetencia(mes: Int, ano: Int): String = "${nomeMesCurto(mes)}/$ano"

    fun mesAtual(): Int = Calendar.getInstance().get(Calendar.MONTH) + 1

    fun anoAtual(): Int = Calendar.getInstance().get(Calendar.YEAR)

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

    /** BigDecimal em reais -> "R$ 1.500,50". */
    fun formatarReais(valor: BigDecimal): String =
        NumberFormat.getCurrencyInstance(LOCALE_BR).format(valor)
}
