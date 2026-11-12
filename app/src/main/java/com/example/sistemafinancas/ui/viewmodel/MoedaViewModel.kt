package com.example.sistemafinancas.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sistemafinancas.auth.SessaoUsuario
import com.example.sistemafinancas.data.CotacaoMoeda
import com.example.sistemafinancas.data.MoedaAcompanhada
import com.example.sistemafinancas.data.dal.MoedaDAO
import com.example.sistemafinancas.data.remote.RetrofitClient
import com.example.sistemafinancas.validation.Formatos
import com.example.sistemafinancas.validation.MoedaValidator
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MoedaViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = MoedaDAO(application)
    private val api = RetrofitClient.cotacaoApi
    val usuarioAtualId: Long? get() = SessaoUsuario.usuarioId

    // Lista (SQLite)
    var moedas by mutableStateOf<List<MoedaAcompanhada>>(emptyList())
        private set
    // Cotações (API) indexadas pelo código da moeda, ex.: "USD"
    var cotacoes by mutableStateOf<Map<String, CotacaoMoeda>>(emptyMap())
        private set
    var buscandoCotacoes by mutableStateOf(false)
        private set
    var erroCotacao by mutableStateOf<String?>(null)
        private set

    // Moeda em edição
    var moeda by mutableStateOf<MoedaAcompanhada?>(null)
        private set

    // Formulário
    var codigo by mutableStateOf(MoedaValidator.MOEDAS.keys.first())
        private set
    var apelido by mutableStateOf("")
        private set
    var quantidade by mutableStateOf("")
        private set
    var cotacaoAlvo by mutableStateOf("")
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
    fun mudarCodigo(v: String)      { if (!carregando) codigo = v }
    fun mudarApelido(v: String)     { if (!carregando && v.length <= 50) apelido = v }
    fun mudarQuantidade(v: String)  { if (!carregando && v.length <= 13) quantidade = v }
    fun mudarCotacaoAlvo(v: String) { if (!carregando && v.length <= 13) cotacaoAlvo = v }

    private class FormularioValidado(
        val codigo: String, val apelido: String, val quantidade: Long, val cotacaoAlvo: Long
    )

    private fun validarFormulario(): FormularioValidado {
        val apelidoLimpo = apelido.trim()
        val qtd = Formatos.paraCentavos(quantidade)
        val alvo = if (cotacaoAlvo.isBlank()) 0L else Formatos.paraCentavos(cotacaoAlvo)
        MoedaValidator.validar(codigo, apelidoLimpo, qtd, alvo)
        return FormularioValidado(codigo, apelidoLimpo, qtd, alvo)
    }

    // ---------- CRUD (SQLite) ----------
    fun listar() = executar {
        val usuarioId = SessaoUsuario.exigirUsuario()
        moedas = withContext(Dispatchers.IO) { dao.listar(usuarioId) }
        atualizarCotacoes()
    }

    fun buscarPorId(id: Long) {
        if (moeda?.id == id) return
        executar {
            val usuarioId = SessaoUsuario.exigirUsuario()
            val encontrada = withContext(Dispatchers.IO) { dao.buscarPorId(id, usuarioId) }
            val m = requireNotNull(encontrada) { "Moeda não encontrada." }
            moeda = m
            codigo = m.codigo
            apelido = m.apelido
            quantidade = Formatos.centavosParaTexto(m.quantidadeCentavos)
            cotacaoAlvo = if (m.cotacaoAlvoCentavos == 0L) "" else Formatos.centavosParaTexto(m.cotacaoAlvoCentavos)
        }
    }

    fun cadastrar() = executar {
        val usuarioId = SessaoUsuario.exigirUsuario()
        val f = validarFormulario()
        withContext(Dispatchers.IO) { dao.inserir(usuarioId, f.codigo, f.apelido, f.quantidade, f.cotacaoAlvo) }
        salvo = true
    }

    fun salvar() = executar {
        val usuarioId = SessaoUsuario.exigirUsuario()
        val selecionada = requireNotNull(moeda) { "Selecione uma moeda." }
        val f = validarFormulario()
        withContext(Dispatchers.IO) {
            dao.alterar(selecionada.id, usuarioId, f.codigo, f.apelido, f.quantidade, f.cotacaoAlvo)
        }
        salvo = true
    }

    fun excluir(id: Long) = executar {
        val usuarioId = SessaoUsuario.exigirUsuario()
        withContext(Dispatchers.IO) { dao.excluir(id, usuarioId) }
        moedas = moedas.filterNot { it.id == id }
        mensagem = "Moeda excluída."
    }

    fun consumirSalvo() {
        salvo = false
    }

    // ---------- API (Retrofit) ----------
    /** Busca na AwesomeAPI as cotações das moedas da lista. Falha de rede não bloqueia o CRUD. */
    fun atualizarCotacoes() {
        val codigos = moedas.map { it.codigo }.distinct()
        if (codigos.isEmpty() || buscandoCotacoes) return
        buscandoCotacoes = true
        erroCotacao = null
        viewModelScope.launch {
            try {
                val resposta = api.buscarCotacoes(codigos.joinToString(",") { "$it-BRL" })
                cotacoes = resposta.values
                    .filter { it.code != null }
                    .associateBy { it.code!! }
            } catch (erro: CancellationException) {
                throw erro
            } catch (erro: Exception) {
                erroCotacao = "Não foi possível obter as cotações. Verifique sua conexão."
            } finally {
                buscandoCotacoes = false
            }
        }
    }
}
