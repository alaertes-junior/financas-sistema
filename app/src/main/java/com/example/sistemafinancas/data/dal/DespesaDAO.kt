package com.example.sistemafinancas.data.dal

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.sistemafinancas.data.Despesa
import com.example.sistemafinancas.data.local.AppDatabase

internal class DespesaDAO(context: Context) {
    private val appContext = context.applicationContext

    private val colunas = arrayOf(
        "id", "usuario_id", "descricao", "tipo", "valor", "parcelas", "parcelas_pagas"
    )

    fun inserir(
        usuarioId: Long, descricao: String, tipo: String, valor: Long, parcelas: Int, parcelasPagas: Int
    ) {
        AppDatabase(appContext).use { db ->
            db.writableDatabase.insertOrThrow(
                "despesas", null,
                valores(descricao, tipo, valor, parcelas, parcelasPagas).apply { put("usuario_id", usuarioId) }
            )
        }
    }

    fun listar(usuarioId: Long): List<Despesa> = AppDatabase(appContext).use { db ->
        db.readableDatabase.query(
            "despesas", colunas,
            "usuario_id = ?", arrayOf(usuarioId.toString()),
            null, null,
            "tipo, descricao COLLATE NOCASE, id"
        ).use { c ->
            buildList { while (c.moveToNext()) add(c.paraDespesa()) }
        }
    }

    fun buscarPorId(id: Long, usuarioId: Long): Despesa? = AppDatabase(appContext).use { db ->
        db.readableDatabase.query(
            "despesas", colunas,
            "id = ? AND usuario_id = ?", arrayOf(id.toString(), usuarioId.toString()),
            null, null, null
        ).use { c -> if (c.moveToFirst()) c.paraDespesa() else null }
    }

    fun alterar(
        id: Long, usuarioId: Long, descricao: String, tipo: String, valor: Long,
        parcelas: Int, parcelasPagas: Int
    ) {
        AppDatabase(appContext).use { db ->
            val alterado = db.writableDatabase.update(
                "despesas", valores(descricao, tipo, valor, parcelas, parcelasPagas),
                "id = ? AND usuario_id = ?", arrayOf(id.toString(), usuarioId.toString())
            )
            require(alterado == 1) { "Despesa não encontrada. Atualize a lista." }
        }
    }

    fun pagarParcela(id: Long, usuarioId: Long) {
        AppDatabase(appContext).use { db ->
            val afetadas = db.writableDatabase.compileStatement(
                "UPDATE despesas SET parcelas_pagas = parcelas_pagas + 1 " +
                    "WHERE id = ? AND usuario_id = ? AND parcelas_pagas < parcelas"
            ).use { comando ->
                comando.bindLong(1, id)
                comando.bindLong(2, usuarioId)
                comando.executeUpdateDelete()
            }
            require(afetadas == 1) { "Não há parcela a pagar. Atualize a lista." }
        }
    }

    fun excluir(id: Long, usuarioId: Long) {
        AppDatabase(appContext).use { db ->
            val excluido = db.writableDatabase.delete(
                "despesas", "id = ? AND usuario_id = ?",
                arrayOf(id.toString(), usuarioId.toString())
            )
            require(excluido == 1) { "Despesa não encontrada. Atualize a lista." }
        }
    }

    private fun valores(
        descricao: String, tipo: String, valor: Long, parcelas: Int, parcelasPagas: Int
    ) = ContentValues().apply {
        put("descricao", descricao)
        put("tipo", tipo)
        put("valor", valor)
        put("parcelas", parcelas)
        put("parcelas_pagas", parcelasPagas)
    }

    private fun Cursor.paraDespesa() = Despesa(
        id = getLong(0),
        usuarioId = getLong(1),
        descricao = getString(2),
        tipo = getString(3),
        valorCentavos = getLong(4),
        parcelas = getInt(5),
        parcelasPagas = getInt(6)
    )
}
