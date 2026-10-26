package com.example.sistemafinancas.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sistemafinancas.auth.SessaoUsuario
import com.example.sistemafinancas.data.Meta
import com.example.sistemafinancas.data.dal.MetaDAO
import com.example.sistemafinancas.validation.Formatos
import com.example.sistemafinancas.validation.MetaValidator
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MetaViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = MetaDAO(application)
    val usuarioAtualId: Long? get() = SessaoUsuario.usuarioId

    // Lista
    var metas by mutableStateOf<List<Meta>>(emptyList())
        private set
    // Meta em edição
    var meta by mutableStateOf<Meta?>(null)
        private set

    // Formulário (mantido no ViewModel para sobreviver à rotação)
    var nome by mutableStateOf("")
        private set
    var categoria by mutableStateOf(MetaValidator.CATEGORIAS.first())
        private set
    var tipo by mutableStateOf("")
        private set
    var valorAlvo by mutableStateOf("")
        private set
    var valorAtual by mutableStateOf("")
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

    // ---------- Formulário ----------
    fun mudarNome(v: String)       { if (!carregando && v.length <= 100) nome = v }
    fun mudarCategoria(v: String)  { if (!carregando) categoria = v }
    fun mudarTipo(v: String)       { if (!carregando && v.length <= 30) tipo = v }
    fun mudarValorAlvo(v: String)  { if (!carregando && v.length <= 13) valorAlvo = v }
    fun mudarValorAtual(v: String) { if (!carregando && v.length <= 13) valorAtual = v }

    private class FormularioValidado(
        val nome: String, val categoria: String, val tipo: String,
        val valorAlvo: Long, val valorAtual: Long
    )

    private fun validarFormulario(): FormularioValidado {
        val nomeLimpo = nome.trim()
        val tipoLimpo = tipo.trim()
        val alvo = Formatos.paraCentavos(valorAlvo)
        val atual = if (valorAtual.isBlank()) 0L else Formatos.paraCentavos(valorAtual)
        MetaValidator.validar(nomeLimpo, categoria, tipoLimpo, alvo, atual)
        return FormularioValidado(nomeLimpo, categoria, tipoLimpo, alvo, atual)
    }

    // ---------- CRUD ----------
    fun listar() = executar {
        val usuarioId = SessaoUsuario.exigirUsuario()
        metas = withContext(Dispatchers.IO) { dao.listar(usuarioId) }
    }

    fun buscarPorId(id: Long) {
        if (meta?.id == id) return
        executar {
            val usuarioId = SessaoUsuario.exigirUsuario()
            val encontrada = withContext(Dispatchers.IO) { dao.buscarPorId(id, usuarioId) }
            val m = requireNotNull(encontrada) { "Meta não encontrada." }
            meta = m
            nome = m.nome
            categoria = m.categoria
            tipo = m.tipo
            valorAlvo = Formatos.centavosParaTexto(m.valorAlvoCentavos)
            valorAtual = Formatos.centavosParaTexto(m.valorAtualCentavos)
        }
    }

    fun cadastrar() = executar {
        val usuarioId = SessaoUsuario.exigirUsuario()
        val f = validarFormulario()
        withContext(Dispatchers.IO) {
            dao.inserir(usuarioId, f.nome, f.categoria, f.tipo, f.valorAlvo, f.valorAtual)
        }
        salvo = true
    }

    fun salvar() = executar {
        val usuarioId = SessaoUsuario.exigirUsuario()
        val selecionada = requireNotNull(meta) { "Selecione uma meta." }
        val f = validarFormulario()
        withContext(Dispatchers.IO) {
            dao.alterar(selecionada.id, usuarioId, f.nome, f.categoria, f.tipo, f.valorAlvo, f.valorAtual)
        }
        salvo = true
    }

    fun excluir(id: Long) = executar {
        val usuarioId = SessaoUsuario.exigirUsuario()
        withContext(Dispatchers.IO) { dao.excluir(id, usuarioId) }
        metas = metas.filterNot { it.id == id }
        mensagem = "Meta excluída."
    }

    fun consumirSalvo() {
        salvo = false
    }
}
