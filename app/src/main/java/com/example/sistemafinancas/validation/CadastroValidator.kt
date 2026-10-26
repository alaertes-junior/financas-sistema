package com.example.sistemafinancas.validation

import android.util.Patterns

object CadastroValidator {
    fun validarCadastro(nome: String, email: String, senha: String) {
        validarNome(nome)
        validarEmail(email)
        validarSenha(senha)
    }

    /** Senha vazia = manter a senha atual. */
    fun validarEdicao(nome: String, novaSenha: String) {
        validarNome(nome)
        if (novaSenha.isNotEmpty()) validarSenha(novaSenha)
    }

    private fun validarNome(nome: String) =
        require(nome.length in 3..100) { "Informe um nome entre 3 e 100 caracteres." }

    private fun validarEmail(email: String) =
        require(email.length <= 254 && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            "Informe um e-mail válido."
        }

    private fun validarSenha(senha: String) =
        require(senha.length in 6..128 && senha.isNotBlank()) {
            "Use uma senha entre 6 e 128 caracteres."
        }
}
