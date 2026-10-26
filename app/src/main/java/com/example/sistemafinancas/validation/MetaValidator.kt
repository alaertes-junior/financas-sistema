package com.example.sistemafinancas.validation

object MetaValidator {
    const val VALOR_MAXIMO_CENTAVOS = 99_999_999_999L   // R$ 999.999.999,99

    val CATEGORIAS = listOf("Reserva", "Automóvel", "Viagem", "Casa própria", "Outros")

    fun validar(
        nome: String, categoria: String, tipo: String,
        valorAlvoCentavos: Long, valorAtualCentavos: Long
    ) {
        require(nome.length in 3..100) { "Informe um nome entre 3 e 100 caracteres." }
        require(categoria in CATEGORIAS) { "Selecione uma categoria." }
        require(tipo.length <= 30) { "A tag deve ter no máximo 30 caracteres." }
        require(valorAlvoCentavos in 1..VALOR_MAXIMO_CENTAVOS) { "O valor objetivo deve ser maior que zero." }
        require(valorAtualCentavos in 0..VALOR_MAXIMO_CENTAVOS) { "O valor guardado não pode ser negativo." }
    }
}
