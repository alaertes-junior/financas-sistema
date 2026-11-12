package com.example.sistemafinancas.validation

object MoedaValidator {
    /** Código -> nome exibido. Todas cotadas em BRL pela AwesomeAPI. */
    val MOEDAS = linkedMapOf(
        "USD" to "Dólar americano",
        "EUR" to "Euro",
        "GBP" to "Libra esterlina",
        "ARS" to "Peso argentino",
        "CAD" to "Dólar canadense",
        "BTC" to "Bitcoin"
    )

    fun validar(codigo: String, apelido: String, quantidadeCentavos: Long, cotacaoAlvoCentavos: Long) {
        require(codigo in MOEDAS) { "Selecione uma moeda." }
        require(apelido.length in 3..50) { "Informe um apelido entre 3 e 50 caracteres." }
        require(quantidadeCentavos in 1..MetaValidator.VALOR_MAXIMO_CENTAVOS) {
            "A quantidade deve ser maior que zero."
        }
        require(cotacaoAlvoCentavos in 0..MetaValidator.VALOR_MAXIMO_CENTAVOS) {
            "A cotação-alvo não pode ser negativa."
        }
    }
}
