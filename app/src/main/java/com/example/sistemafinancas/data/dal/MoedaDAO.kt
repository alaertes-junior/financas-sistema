package com.example.sistemafinancas.data.dal

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.sistemafinancas.data.MoedaAcompanhada
import com.example.sistemafinancas.data.local.AppDatabase

internal class MoedaDAO(context: Context) {
    private val appContext = context.applicationContext

    private val colunas = arrayOf(
        "id", "usuario_id", "codigo", "apelido", "quantidade", "cotacao_alvo"
    )

    fun inserir(usuarioId: Long, codigo: String, apelido: String, quantidade: Long, cotacaoAlvo: Long) {
        AppDatabase(appContext).use { db ->
            db.writableDatabase.insertOrThrow(
                "moedas_acompanhadas", null,
                valores(codigo, apelido, quantidade, cotacaoAlvo).apply { put("usuario_id", usuarioId) }
            )
        }
    }

    fun listar(usuarioId: Long): List<MoedaAcompanhada> = AppDatabase(appContext).use { db ->
        db.readableDatabase.query(
            "moedas_acompanhadas", colunas,
            "usuario_id = ?", arrayOf(usuarioId.toString()),
            null, null,
            "codigo, apelido COLLATE NOCASE, id"
        ).use { c ->
            buildList { while (c.moveToNext()) add(c.paraMoeda()) }
        }
    }

    fun buscarPorId(id: Long, usuarioId: Long): MoedaAcompanhada? = AppDatabase(appContext).use { db ->
        db.readableDatabase.query(
            "moedas_acompanhadas", colunas,
            "id = ? AND usuario_id = ?", arrayOf(id.toString(), usuarioId.toString()),
            null, null, null
        ).use { c -> if (c.moveToFirst()) c.paraMoeda() else null }
    }

    fun alterar(id: Long, usuarioId: Long, codigo: String, apelido: String, quantidade: Long, cotacaoAlvo: Long) {
        AppDatabase(appContext).use { db ->
            val alterado = db.writableDatabase.update(
                "moedas_acompanhadas", valores(codigo, apelido, quantidade, cotacaoAlvo),
                "id = ? AND usuario_id = ?", arrayOf(id.toString(), usuarioId.toString())
            )
            require(alterado == 1) { "Moeda não encontrada. Atualize a lista." }
        }
    }

    fun excluir(id: Long, usuarioId: Long) {
        AppDatabase(appContext).use { db ->
            val excluido = db.writableDatabase.delete(
                "moedas_acompanhadas", "id = ? AND usuario_id = ?",
                arrayOf(id.toString(), usuarioId.toString())
            )
            require(excluido == 1) { "Moeda não encontrada. Atualize a lista." }
        }
    }

    private fun valores(codigo: String, apelido: String, quantidade: Long, cotacaoAlvo: Long) =
        ContentValues().apply {
            put("codigo", codigo)
            put("apelido", apelido)
            put("quantidade", quantidade)
            put("cotacao_alvo", cotacaoAlvo)
        }

    private fun Cursor.paraMoeda() = MoedaAcompanhada(
        id = getLong(0),
        usuarioId = getLong(1),
        codigo = getString(2),
        apelido = getString(3),
        quantidadeCentavos = getLong(4),
        cotacaoAlvoCentavos = getLong(5)
    )
}
