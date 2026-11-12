package com.example.sistemafinancas.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
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
import java.io.File
import java.util.UUID

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
    /** Caminho da foto personalizada (arquivo em filesDir/metas), ou null para usar o ícone da categoria. */
    var imagemUri by mutableStateOf<String?>(null)
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
    fun removerImagem()            { if (!carregando) imagemUri = null }

    /** Recebe a imagem escolhida na galeria e copia (reduzida) para o armazenamento interno do app. */
    fun selecionarImagem(uri: Uri) {
        if (carregando || salvo) return
        carregando = true
        mensagem = null
        viewModelScope.launch {
            try {
                imagemUri = withContext(Dispatchers.IO) { copiarImagem(uri) }
            } catch (erro: CancellationException) {
                throw erro
            } catch (erro: Exception) {
                mensagem = "Não foi possível carregar a imagem."
            } finally {
                carregando = false
            }
        }
    }

    private fun copiarImagem(uri: Uri): String {
        val app = getApplication<Application>()
        val resolver = app.contentResolver
        // 1) Lê só as dimensões para calcular a redução (evita estourar memória com fotos grandes).
        val limites = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        resolver.openInputStream(uri).use { BitmapFactory.decodeStream(it, null, limites) }
        var amostra = 1
        while (limites.outWidth / (amostra * 2) >= TAMANHO_IMAGEM && limites.outHeight / (amostra * 2) >= TAMANHO_IMAGEM) {
            amostra *= 2
        }
        // 2) Decodifica reduzida e salva como JPEG em filesDir/metas.
        val bitmap = resolver.openInputStream(uri).use {
            BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = amostra })
        }
        requireNotNull(bitmap) { "Imagem inválida." }
        val pasta = File(app.filesDir, "metas").apply { mkdirs() }
        val arquivo = File(pasta, "${UUID.randomUUID()}.jpg")
        arquivo.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 85, it) }
        bitmap.recycle()
        return arquivo.absolutePath
    }

    private fun apagarImagem(caminho: String?) {
        if (caminho != null) File(caminho).delete()
    }

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
            imagemUri = m.imagemUri
        }
    }

    fun cadastrar() = executar {
        val usuarioId = SessaoUsuario.exigirUsuario()
        val f = validarFormulario()
        withContext(Dispatchers.IO) {
            dao.inserir(usuarioId, f.nome, f.categoria, f.tipo, f.valorAlvo, f.valorAtual, imagemUri)
        }
        salvo = true
    }

    fun salvar() = executar {
        val usuarioId = SessaoUsuario.exigirUsuario()
        val selecionada = requireNotNull(meta) { "Selecione uma meta." }
        val f = validarFormulario()
        withContext(Dispatchers.IO) {
            dao.alterar(selecionada.id, usuarioId, f.nome, f.categoria, f.tipo, f.valorAlvo, f.valorAtual, imagemUri)
            // Foto trocada ou removida: apaga o arquivo antigo.
            if (selecionada.imagemUri != imagemUri) apagarImagem(selecionada.imagemUri)
        }
        salvo = true
    }

    /** Soma um valor (texto como "150,00") ao total já guardado da meta. */
    fun guardarValor(id: Long, valorTexto: String) = executar {
        val usuarioId = SessaoUsuario.exigirUsuario()
        val centavos = Formatos.paraCentavos(valorTexto)
        require(centavos > 0) { "Informe um valor maior que zero." }
        metas = withContext(Dispatchers.IO) {
            dao.guardarValor(id, usuarioId, centavos)
            dao.listar(usuarioId)
        }
        mensagem = "Valor de ${Formatos.formatarMoeda(centavos)} guardado."
    }

    fun excluir(id: Long) = executar {
        val usuarioId = SessaoUsuario.exigirUsuario()
        val caminho = metas.firstOrNull { it.id == id }?.imagemUri
        withContext(Dispatchers.IO) {
            dao.excluir(id, usuarioId)
            apagarImagem(caminho)
        }
        metas = metas.filterNot { it.id == id }
        mensagem = "Meta excluída."
    }

    fun consumirSalvo() {
        salvo = false
    }

    private companion object {
        const val TAMANHO_IMAGEM = 512
    }
}
