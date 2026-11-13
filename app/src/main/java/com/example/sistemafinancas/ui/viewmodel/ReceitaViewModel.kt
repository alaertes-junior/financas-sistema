package com.example.sistemafinancas.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sistemafinancas.auth.SessaoUsuario
import com.example.sistemafinancas.data.Receita
import com.example.sistemafinancas.data.dal.ReceitaDAO
import com.example.sistemafinancas.validation.Formatos
import com.example.sistemafinancas.validation.ReceitaValidator
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReceitaViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = ReceitaDAO(application)
    val usuarioAtualId: Long? get() = SessaoUsuario.usuarioId

    // Lista
    var receitas by mutableStateOf<List<Receita>>(emptyList())
        private set
    // Receita em edição
    var receita by mutableStateOf<Receita?>(null)
        private set

    // Filtro mensal da listagem (filtroMes == null exibe todos os meses do ano selecionado)
    var filtroMes by mutableStateOf<Int?>(Formatos.mesAtual())
        private set
    var filtroAno by mutableStateOf(Formatos.anoAtual())
        private set

    // Formulário
    var descricao by mutableStateOf("")
        private set
    var categoria by mutableStateOf(ReceitaValidator.CATEGORIAS.first())
        private set
    var tipo by mutableStateOf(ReceitaValidator.TIPO_FIXA)
        private set
    var valor by mutableStateOf("")
        private set
    var recebida by mutableStateOf(false)
        private set
    var mes by mutableStateOf(Formatos.mesAtual())
        private set
    var ano by mutableStateOf(Formatos.anoAtual().toString())
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
    fun mudarCategoria(v: String) { if (!carregando) categoria = v }
    fun mudarTipo(v: String)      { if (!carregando) tipo = v }
    fun mudarValor(v: String)     { if (!carregando && v.length <= 13) valor = v }
    fun mudarRecebida(v: Boolean) { if (!carregando) recebida = v }
    fun mudarMes(v: Int)          { if (!carregando && v in 1..12) mes = v }
    fun mudarAno(v: String)       { if (!carregando && v.length <= 4 && v.all(Char::isDigit)) ano = v }

    // ---------- Filtro Mensal ----------
    fun selecionarMesFiltro(novoMes: Int?) {
        if (carregando) return
        filtroMes = novoMes
        listar()
    }

    fun alterarAnoFiltro(delta: Int) {
        val novoAno = (filtroAno + delta).coerceIn(2000, 2100)
        if (carregando || novoAno == filtroAno) return
        filtroAno = novoAno
        listar()
    }

    private class FormularioValidado(
        val descricao: String, val categoria: String, val tipo: String,
        val valor: Long, val recebida: Boolean, val mes: Int, val ano: Int
    )

    private fun validarFormulario(): FormularioValidado {
        val descricaoLimpa = descricao.trim()
        val centavos = Formatos.paraCentavos(valor)
        val anoInt = requireNotNull(ano.toIntOrNull()) { "Informe um ano válido (2000 a 2100)." }
        ReceitaValidator.validar(descricaoLimpa, categoria, tipo, centavos, mes, anoInt)
        return FormularioValidado(descricaoLimpa, categoria, tipo, centavos, recebida, mes, anoInt)
    }

    // ---------- CRUD ----------
    fun listar() = executar {
        val usuarioId = SessaoUsuario.exigirUsuario()
        val m = filtroMes
        val a = filtroAno
        receitas = withContext(Dispatchers.IO) { dao.listar(usuarioId, m, a) }
    }

    fun buscarPorId(id: Long) {
        if (receita?.id == id) return
        executar {
            val usuarioId = SessaoUsuario.exigirUsuario()
            val encontrada = withContext(Dispatchers.IO) { dao.buscarPorId(id, usuarioId) }
            val r = requireNotNull(encontrada) { "Receita não encontrada." }
            receita = r
            descricao = r.descricao
            categoria = r.categoria
            tipo = r.tipo
            valor = Formatos.centavosParaTexto(r.valorCentavos)
            recebida = r.recebida
            mes = r.mes
            ano = r.ano.toString()
        }
    }

    fun cadastrar() = executar {
        val usuarioId = SessaoUsuario.exigirUsuario()
        val f = validarFormulario()
        withContext(Dispatchers.IO) {
            dao.inserir(usuarioId, f.descricao, f.categoria, f.tipo, f.valor, f.recebida, f.mes, f.ano)
        }
        salvo = true
    }

    fun salvar() = executar {
        val usuarioId = SessaoUsuario.exigirUsuario()
        val selecionada = requireNotNull(receita) { "Selecione uma receita." }
        val f = validarFormulario()
        withContext(Dispatchers.IO) {
            dao.alterar(selecionada.id, usuarioId, f.descricao, f.categoria, f.tipo, f.valor, f.recebida, f.mes, f.ano)
        }
        salvo = true
    }

    fun alternarRecebida(r: Receita) = executar {
        val usuarioId = SessaoUsuario.exigirUsuario()
        val m = filtroMes
        val a = filtroAno
        receitas = withContext(Dispatchers.IO) {
            dao.marcarRecebida(r.id, usuarioId, !r.recebida)
            dao.listar(usuarioId, m, a)
        }
    }

    fun excluir(id: Long) = executar {
        val usuarioId = SessaoUsuario.exigirUsuario()
        withContext(Dispatchers.IO) { dao.excluir(id, usuarioId) }
        receitas = receitas.filterNot { it.id == id }
        mensagem = "Receita excluída."
    }

    fun consumirSalvo() {
        salvo = false
    }
}
