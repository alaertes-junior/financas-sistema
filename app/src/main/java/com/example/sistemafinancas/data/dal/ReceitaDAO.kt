package com.example.sistemafinancas.data.dal

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.sistemafinancas.data.Receita
import com.example.sistemafinancas.data.local.AppDatabase

internal class ReceitaDAO(context: Context) {
    private val appContext = context.applicationContext

    private val colunas = arrayOf(
        "id", "usuario_id", "descricao", "categoria", "tipo", "valor", "recebida"
    )

    fun inserir(
        usuarioId: Long, descricao: String, categoria: String, tipo: String, valor: Long, recebida: Boolean
    ) {
        AppDatabase(appContext).use { db ->
            db.writableDatabase.insertOrThrow(
                "receitas", null,
                valores(descricao, categoria, tipo, valor, recebida).apply { put("usuario_id", usuarioId) }
            )
        }
    }

    fun listar(usuarioId: Long): List<Receita> = AppDatabase(appContext).use { db ->
        db.readableDatabase.query(
            "receitas", colunas,
            "usuario_id = ?", arrayOf(usuarioId.toString()),
            null, null,
            "recebida, descricao COLLATE NOCASE, id"
        ).use { c ->
            buildList { while (c.moveToNext()) add(c.paraReceita()) }
        }
    }

    fun buscarPorId(id: Long, usuarioId: Long): Receita? = AppDatabase(appContext).use { db ->
        db.readableDatabase.query(
            "receitas", colunas,
            "id = ? AND usuario_id = ?", arrayOf(id.toString(), usuarioId.toString()),
            null, null, null
        ).use { c -> if (c.moveToFirst()) c.paraReceita() else null }
    }

    fun alterar(
        id: Long, usuarioId: Long, descricao: String, categoria: String, tipo: String,
        valor: Long, recebida: Boolean
    ) {
        AppDatabase(appContext).use { db ->
            val alterado = db.writableDatabase.update(
                "receitas", valores(descricao, categoria, tipo, valor, recebida),
                "id = ? AND usuario_id = ?", arrayOf(id.toString(), usuarioId.toString())
            )
            require(alterado == 1) { "Receita não encontrada. Atualize a lista." }
        }
    }

    fun marcarRecebida(id: Long, usuarioId: Long, recebida: Boolean) {
        AppDatabase(appContext).use { db ->
            val alterado = db.writableDatabase.update(
                "receitas", ContentValues().apply { put("recebida", if (recebida) 1 else 0) },
                "id = ? AND usuario_id = ?", arrayOf(id.toString(), usuarioId.toString())
            )
            require(alterado == 1) { "Receita não encontrada. Atualize a lista." }
        }
    }

    fun excluir(id: Long, usuarioId: Long) {
        AppDatabase(appContext).use { db ->
            val excluido = db.writableDatabase.delete(
                "receitas", "id = ? AND usuario_id = ?",
                arrayOf(id.toString(), usuarioId.toString())
            )
            require(excluido == 1) { "Receita não encontrada. Atualize a lista." }
        }
    }

    private fun valores(
        descricao: String, categoria: String, tipo: String, valor: Long, recebida: Boolean
    ) = ContentValues().apply {
        put("descricao", descricao)
        put("categoria", categoria)
        put("tipo", tipo)
        put("valor", valor)
        put("recebida", if (recebida) 1 else 0)
    }

    private fun Cursor.paraReceita() = Receita(
        id = getLong(0),
        usuarioId = getLong(1),
        descricao = getString(2),
        categoria = getString(3),
        tipo = getString(4),
        valorCentavos = getLong(5),
        recebida = getInt(6) == 1
    )
}
