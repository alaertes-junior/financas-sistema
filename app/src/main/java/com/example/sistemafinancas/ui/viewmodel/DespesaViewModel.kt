package com.example.sistemafinancas.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sistemafinancas.auth.SessaoUsuario
import com.example.sistemafinancas.data.Despesa
import com.example.sistemafinancas.data.dal.DespesaDAO
import com.example.sistemafinancas.validation.DespesaValidator
import com.example.sistemafinancas.validation.Formatos
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DespesaViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = DespesaDAO(application)
    val usuarioAtualId: Long? get() = SessaoUsuario.usuarioId

    // Lista
    var despesas by mutableStateOf<List<Despesa>>(emptyList())
        private set
    // Despesa em edição
    var despesa by mutableStateOf<Despesa?>(null)
        private set

    // Formulário
    var descricao by mutableStateOf("")
        private set
    var tipo by mutableStateOf(DespesaValidator.TIPO_FIXA)
        private set
    var valor by mutableStateOf("")
        private set
    var parcelas by mutableStateOf("1")
        private set
    var parcelasPagas by mutableStateOf("")
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
    fun mudarDescricao(v: String) { if (!carregando && v.length <= 100) descricao = v }
    fun mudarTipo(v: String)      { if (!carregando) tipo = v }
    fun mudarValor(v: String)     { if (!carregando && v.length <= 13) valor = v }
    fun mudarParcelas(v: String)  { if (!carregando && v.length <= 3 && v.all(Char::isDigit)) parcelas = v }
    fun mudarParcelasPagas(v: String) { if (!carregando && v.length <= 3 && v.all(Char::isDigit)) parcelasPagas = v }

    private class FormularioValidado(
        val descricao: String, val tipo: String, val valor: Long, val parcelas: Int, val parcelasPagas: Int
    )

    private fun validarFormulario(): FormularioValidado {
        val descricaoLimpa = descricao.trim()
        val centavos = Formatos.paraCentavos(valor)
        val totalParcelas = requireNotNull(parcelas.toIntOrNull()) { "Informe o número de parcelas." }
        val pagas = if (parcelasPagas.isBlank()) 0 else requireNotNull(parcelasPagas.toIntOrNull()) {
            "Informe as parcelas pagas."
        }
        DespesaValidator.validar(descricaoLimpa, tipo, centavos, totalParcelas, pagas)
        return FormularioValidado(descricaoLimpa, tipo, centavos, totalParcelas, pagas)
    }

    // ---------- CRUD ----------
    fun listar() = executar {
        val usuarioId = SessaoUsuario.exigirUsuario()
        despesas = withContext(Dispatchers.IO) { dao.listar(usuarioId) }
    }

    fun buscarPorId(id: Long) {
        if (despesa?.id == id) return
        executar {
            val usuarioId = SessaoUsuario.exigirUsuario()
            val encontrada = withContext(Dispatchers.IO) { dao.buscarPorId(id, usuarioId) }
            val d = requireNotNull(encontrada) { "Despesa não encontrada." }
            despesa = d
            descricao = d.descricao
            tipo = d.tipo
            valor = Formatos.centavosParaTexto(d.valorCentavos)
            parcelas = d.parcelas.toString()
            parcelasPagas = d.parcelasPagas.toString()
        }
    }

    fun cadastrar() = executar {
        val usuarioId = SessaoUsuario.exigirUsuario()
        val f = validarFormulario()
        withContext(Dispatchers.IO) { dao.inserir(usuarioId, f.descricao, f.tipo, f.valor, f.parcelas, f.parcelasPagas) }
        salvo = true
    }

    fun salvar() = executar {
        val usuarioId = SessaoUsuario.exigirUsuario()
        val selecionada = requireNotNull(despesa) { "Selecione uma despesa." }
        val f = validarFormulario()
        withContext(Dispatchers.IO) { dao.alterar(selecionada.id, usuarioId, f.descricao, f.tipo, f.valor, f.parcelas, f.parcelasPagas) }
        salvo = true
    }

    fun pagarParcela(id: Long) = executar {
        val usuarioId = SessaoUsuario.exigirUsuario()
        despesas = withContext(Dispatchers.IO) {
            dao.pagarParcela(id, usuarioId)
            dao.listar(usuarioId)
        }
    }

    fun excluir(id: Long) = executar {
        val usuarioId = SessaoUsuario.exigirUsuario()
        withContext(Dispatchers.IO) { dao.excluir(id, usuarioId) }
        despesas = despesas.filterNot { it.id == id }
        mensagem = "Despesa excluída."
    }

    fun consumirSalvo() {
        salvo = false
    }
}
