package com.example.sistemafinancas.ui.viewmodel

import android.app.Application
import android.database.sqlite.SQLiteConstraintException
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sistemafinancas.auth.AuthManager
import com.example.sistemafinancas.auth.SessaoUsuario
import com.example.sistemafinancas.data.dal.UsuarioDAO
import com.example.sistemafinancas.validation.CadastroValidator
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = UsuarioDAO(application)

    var carregando by mutableStateOf(false)
        private set
    var mensagem by mutableStateOf<String?>(null)
        private set
    var concluido by mutableStateOf(false)
        private set

    private fun executar(operacao: suspend () -> Boolean) {
        if (carregando || concluido) return
        carregando = true
        mensagem = null
        viewModelScope.launch {
            try {
                concluido = operacao()
                if (!concluido) mensagem = "E-mail ou senha inválidos."
            } catch (erro: CancellationException) {
                throw erro
            } catch (erro: SQLiteConstraintException) {
                mensagem = "Não foi possível cadastrar. Confira se o e-mail já está em uso."
            } catch (erro: IllegalArgumentException) {
                mensagem = erro.message
            } catch (erro: Exception) {
                mensagem = "Ocorreu um erro inesperado."
            } finally {
                carregando = false
            }
        }
    }

    fun cadastrar(nome: String, email: String, senha: String) = executar {
        val nomeLimpo = nome.trim()
        val emailLimpo = email.trim().lowercase(Locale.ROOT)
        CadastroValidator.validarCadastro(nomeLimpo, emailLimpo, senha)
        withContext(Dispatchers.IO) {
            val salt = AuthManager.criarSalt()
            val hash = AuthManager.gerarHash(senha, salt)
            try {
                dao.cadastrar(nomeLimpo, emailLimpo, hash, salt)
            } finally {
                hash.fill(0)
            }
        }
        true
    }

    fun login(email: String, senha: String) = executar {
        val emailLimpo = email.trim().lowercase(Locale.ROOT)
        require(emailLimpo.length <= 254 && senha.length in 6..128) { "E-mail ou senha inválidos." }
        val idAutenticado = withContext(Dispatchers.IO) {
            val dados = dao.buscarSenha(emailLimpo)
            // Usuário inexistente ainda calcula um hash: mesmo tempo de resposta (evita enumeração de e-mails).
            val hashSalvo = dados?.hash ?: ByteArray(32)
            val salt = dados?.salt ?: ByteArray(16)
            try {
                if (AuthManager.validarSenha(senha, salt, hashSalvo)) dados?.usuarioId else null
            } finally {
                hashSalvo.fill(0)
            }
        }
        if (idAutenticado != null) {
            SessaoUsuario.iniciar(idAutenticado)
            true
        } else false
    }

    fun consumirResultado() {
        concluido = false
    }
}
