package com.example.sistemafinancas.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sistemafinancas.auth.AuthManager
import com.example.sistemafinancas.auth.SessaoUsuario
import com.example.sistemafinancas.data.Usuario
import com.example.sistemafinancas.data.dal.UsuarioDAO
import com.example.sistemafinancas.validation.CadastroValidator
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UsuarioViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = UsuarioDAO(application)
    val usuarioAtualId: Long? get() = SessaoUsuario.usuarioId

    var usuarios by mutableStateOf<List<Usuario>>(emptyList())
        private set
    var usuario by mutableStateOf<Usuario?>(null)
        private set
    var nome by mutableStateOf("")
        private set
    var mensagem by mutableStateOf<String?>(null)
        private set
    var carregando by mutableStateOf(false)
        private set
    var salvo by mutableStateOf(false)
        private set

    private fun executar(operacao: suspend () -> Unit) {
        if (carregando || salvo) return
        carregando = true
        mensagem = null
        viewModelScope.launch {
            try {
                SessaoUsuario.exigirUsuario()
                operacao()
            } catch (erro: CancellationException) {
                throw erro
            } catch (erro: IllegalArgumentException) {
                mensagem = erro.message
            } catch (erro: Exception) {
                mensagem = "Ocorreu um erro inesperado."
            } finally {
                carregando = false
            }
        }
    }

    fun listar() = executar {
        usuarios = withContext(Dispatchers.IO) { dao.listar() }
    }

    fun buscarPorId(id: Long) {
        if (usuario?.id == id) return
        executar {
            val encontrado = withContext(Dispatchers.IO) { dao.buscarPorId(id) }
            usuario = requireNotNull(encontrado) { "Usuário não encontrado." }
            nome = encontrado.nome
        }
    }

    fun mudarNome(valor: String) {
        if (!carregando && valor.length <= 100) nome = valor   // sem trim aqui
    }

    fun salvar(novaSenha: String) = executar {
        val selecionado = requireNotNull(usuario) { "Selecione um usuário." }
        val nomeLimpo = nome.trim()
        CadastroValidator.validarEdicao(nomeLimpo, novaSenha)
        withContext(Dispatchers.IO) {
            if (novaSenha.isEmpty()) {
                dao.alterar(selecionado.id, nomeLimpo)
            } else {
                val salt = AuthManager.criarSalt()
                val hash = AuthManager.gerarHash(novaSenha, salt)   // sem trim
                try {
                    dao.alterar(selecionado.id, nomeLimpo, hash, salt)
                } finally {
                    hash.fill(0)
                }
            }
        }
        salvo = true
    }

    fun excluir(id: Long) = executar {
        val atual = SessaoUsuario.exigirUsuario()
        require(id != atual) { "Você não pode excluir o seu usuário." }
        withContext(Dispatchers.IO) { dao.excluir(id, atual) }
        usuarios = usuarios.filterNot { it.id == id }
        mensagem = "Usuário excluído."
    }

    fun consumirSalvo() {
        salvo = false
    }
}
