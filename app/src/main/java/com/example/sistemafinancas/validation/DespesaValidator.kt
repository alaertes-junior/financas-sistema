package com.example.sistemafinancas.validation

object DespesaValidator {
    const val TIPO_FIXA = "Fixa"
    const val TIPO_VARIAVEL = "Variável"
    const val MAX_PARCELAS = 120
    val TIPOS = listOf(TIPO_FIXA, TIPO_VARIAVEL)

    fun validar(
        descricao: String, tipo: String, valorCentavos: Long, parcelas: Int, parcelasPagas: Int
    ) {
        require(descricao.length in 3..100) { "Informe uma descrição entre 3 e 100 caracteres." }
        require(tipo in TIPOS) { "Selecione o tipo da despesa." }
        require(valorCentavos in 1..MetaValidator.VALOR_MAXIMO_CENTAVOS) {
            "O valor da despesa deve ser maior que zero."
        }
        require(parcelas in 1..MAX_PARCELAS) { "O número de parcelas deve ficar entre 1 e $MAX_PARCELAS." }
        require(parcelasPagas in 0..parcelas) { "As parcelas pagas não podem passar do total de parcelas." }
    }
}
