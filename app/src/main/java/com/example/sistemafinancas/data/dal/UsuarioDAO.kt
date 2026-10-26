package com.example.sistemafinancas.data.dal

import android.content.ContentValues
import android.content.Context
import com.example.sistemafinancas.data.Usuario
import com.example.sistemafinancas.data.local.AppDatabase

internal class SenhaSalva(val hash: ByteArray, val salt: ByteArray, val usuarioId: Long)

internal class UsuarioDAO(context: Context) {
    private val appContext = context.applicationContext

    fun cadastrar(nome: String, email: String, hash: ByteArray, salt: ByteArray) {
        AppDatabase(appContext).use { db ->
            val dados = ContentValues().apply {
                put("nome", nome)
                put("email", email)
                put("senha", hash)
                put("salt", salt)
            }
            db.writableDatabase.insertOrThrow("usuarios", null, dados)
        }
    }

    fun buscarSenha(email: String): SenhaSalva? = AppDatabase(appContext).use { db ->
        db.readableDatabase.query(
            "usuarios", arrayOf("senha", "salt", "id"),
            "email = ?", arrayOf(email),
            null, null, null
        ).use { c ->
            if (c.moveToFirst()) SenhaSalva(c.getBlob(0), c.getBlob(1), c.getLong(2)) else null
        }
    }

    fun listar(): List<Usuario> = AppDatabase(appContext).use { db ->
        db.readableDatabase.query(
            "usuarios", arrayOf("id", "nome", "email"),
            null, null, null, null,
            "nome COLLATE NOCASE, id"
        ).use { c ->
            buildList {
                while (c.moveToNext()) add(Usuario(c.getLong(0), c.getString(1), c.getString(2)))
            }
        }
    }

    fun buscarPorId(id: Long): Usuario? = AppDatabase(appContext).use { db ->
        db.readableDatabase.query(
            "usuarios", arrayOf("id", "nome", "email"),
            "id = ?", arrayOf(id.toString()),
            null, null, null
        ).use { c ->
            if (c.moveToFirst()) Usuario(c.getLong(0), c.getString(1), c.getString(2)) else null
        }
    }

    fun alterar(id: Long, nome: String, hash: ByteArray? = null, salt: ByteArray? = null) {
        AppDatabase(appContext).use { db ->
            val valores = ContentValues().apply {
                put("nome", nome)
                if (hash != null) put("senha", hash)
                if (salt != null) put("salt", salt)
            }
            val alterado = db.writableDatabase.update(
                "usuarios", valores, "id = ?", arrayOf(id.toString())
            )
            require(alterado == 1) { "Usuário não encontrado. Atualize a lista." }
        }
    }

    fun excluir(id: Long, usuarioAtualId: Long) {
        require(usuarioAtualId > 0) { "Faça login para continuar." }
        require(id != usuarioAtualId) { "Você não pode excluir o seu usuário." }
        AppDatabase(appContext).use { db ->
            val excluido = db.writableDatabase.delete(
                "usuarios", "id = ? AND id <> ?",
                arrayOf(id.toString(), usuarioAtualId.toString())
            )
            require(excluido == 1) { "Usuário não encontrado. Atualize a lista." }
        }
    }
}
