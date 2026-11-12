package com.example.sistemafinancas.data.dal

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.sistemafinancas.data.Meta
import com.example.sistemafinancas.data.local.AppDatabase
import com.example.sistemafinancas.validation.MetaValidator.VALOR_MAXIMO_CENTAVOS as VALOR_MAXIMO

internal class MetaDAO(context: Context) {
    private val appContext = context.applicationContext

    private val colunas = arrayOf(
        "id", "usuario_id", "nome", "categoria", "tipo", "valor_alvo", "valor_atual", "imagem_uri"
    )

    fun inserir(
        usuarioId: Long, nome: String, categoria: String, tipo: String,
        valorAlvo: Long, valorAtual: Long, imagemUri: String?
    ) {
        AppDatabase(appContext).use { db ->
            db.writableDatabase.insertOrThrow(
                "metas", null,
                valores(nome, categoria, tipo, valorAlvo, valorAtual, imagemUri).apply { put("usuario_id", usuarioId) }
            )
        }
    }

    fun listar(usuarioId: Long): List<Meta> = AppDatabase(appContext).use { db ->
        db.readableDatabase.query(
            "metas", colunas,
            "usuario_id = ?", arrayOf(usuarioId.toString()),
            null, null,
            "nome COLLATE NOCASE, id"
        ).use { c ->
            buildList { while (c.moveToNext()) add(c.paraMeta()) }
        }
    }

    fun buscarPorId(id: Long, usuarioId: Long): Meta? = AppDatabase(appContext).use { db ->
        db.readableDatabase.query(
            "metas", colunas,
            "id = ? AND usuario_id = ?", arrayOf(id.toString(), usuarioId.toString()),
            null, null, null
        ).use { c -> if (c.moveToFirst()) c.paraMeta() else null }
    }

    fun alterar(
        id: Long, usuarioId: Long, nome: String, categoria: String, tipo: String,
        valorAlvo: Long, valorAtual: Long, imagemUri: String?
    ) {
        AppDatabase(appContext).use { db ->
            val alterado = db.writableDatabase.update(
                "metas", valores(nome, categoria, tipo, valorAlvo, valorAtual, imagemUri),
                "id = ? AND usuario_id = ?", arrayOf(id.toString(), usuarioId.toString())
            )
            require(alterado == 1) { "Meta não encontrada. Atualize a lista." }
        }
    }

    /** Soma [valor] ao valor já guardado (operação atômica no SQLite). */
    fun guardarValor(id: Long, usuarioId: Long, valor: Long) {
        AppDatabase(appContext).use { db ->
            val afetadas = db.writableDatabase.compileStatement(
                "UPDATE metas SET valor_atual = valor_atual + ? " +
                    "WHERE id = ? AND usuario_id = ? AND valor_atual + ? <= $VALOR_MAXIMO"
            ).use { comando ->
                comando.bindLong(1, valor)
                comando.bindLong(2, id)
                comando.bindLong(3, usuarioId)
                comando.bindLong(4, valor)
                comando.executeUpdateDelete()
            }
            require(afetadas == 1) { "Não foi possível guardar o valor. Atualize a lista." }
        }
    }

    fun excluir(id: Long, usuarioId: Long) {
        AppDatabase(appContext).use { db ->
            val excluido = db.writableDatabase.delete(
                "metas", "id = ? AND usuario_id = ?",
                arrayOf(id.toString(), usuarioId.toString())
            )
            require(excluido == 1) { "Meta não encontrada. Atualize a lista." }
        }
    }

    private fun valores(
        nome: String, categoria: String, tipo: String, valorAlvo: Long, valorAtual: Long, imagemUri: String?
    ) = ContentValues().apply {
        put("nome", nome)
        put("categoria", categoria)
        put("tipo", tipo)
        put("valor_alvo", valorAlvo)
        put("valor_atual", valorAtual)
        put("imagem_uri", imagemUri)
    }

    private fun Cursor.paraMeta() = Meta(
        id = getLong(0),
        usuarioId = getLong(1),
        nome = getString(2),
        categoria = getString(3),
        tipo = getString(4),
        valorAlvoCentavos = getLong(5),
        valorAtualCentavos = getLong(6),
        imagemUri = if (isNull(7)) null else getString(7)
    )
}
