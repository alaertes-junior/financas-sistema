package com.example.sistemafinancas.validation

object ReceitaValidator {
    const val TIPO_FIXA = "Fixa"
    const val TIPO_VARIAVEL = "Variável"
    val TIPOS = listOf(TIPO_FIXA, TIPO_VARIAVEL)
    val CATEGORIAS = listOf("Salário", "Freelance", "Rendimentos", "Outros")

    fun validar(
        descricao: String, categoria: String, tipo: String, valorCentavos: Long,
        mes: Int, ano: Int
    ) {
        require(descricao.length in 3..100) { "Informe uma descrição entre 3 e 100 caracteres." }
        require(categoria in CATEGORIAS) { "Selecione uma categoria." }
        require(tipo in TIPOS) { "Selecione o tipo da receita." }
        require(valorCentavos in 1..MetaValidator.VALOR_MAXIMO_CENTAVOS) {
            "O valor da receita deve ser maior que zero."
        }
        require(mes in 1..12) { "Selecione um mês válido (1 a 12)." }
        require(ano in 2000..2100) { "Informe um ano válido (2000 a 2100)." }
    }
}
