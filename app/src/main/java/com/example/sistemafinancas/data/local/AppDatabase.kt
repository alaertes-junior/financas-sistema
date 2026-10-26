package com.example.sistemafinancas.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.File

internal class AppDatabase(context: Context) : SQLiteOpenHelper(
    context.applicationContext,
    File(context.noBackupFilesDir, "financas.db").absolutePath,
    null,
    VERSAO
) {
    override fun onConfigure(db: SQLiteDatabase) {
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE usuarios(
                id    INTEGER PRIMARY KEY,
                nome  TEXT NOT NULL CHECK(length(nome) BETWEEN 3 AND 100),
                email TEXT NOT NULL UNIQUE CHECK(length(email) BETWEEN 3 AND 254),
                senha BLOB NOT NULL,
                salt  BLOB NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE metas(
                id          INTEGER PRIMARY KEY,
                usuario_id  INTEGER NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
                nome        TEXT    NOT NULL CHECK(length(nome) BETWEEN 3 AND 100),
                categoria   TEXT    NOT NULL CHECK(length(categoria) BETWEEN 1 AND 50),
                tipo        TEXT    NOT NULL DEFAULT '' CHECK(length(tipo) <= 30),
                valor_alvo  INTEGER NOT NULL CHECK(valor_alvo > 0),
                valor_atual INTEGER NOT NULL DEFAULT 0 CHECK(valor_atual >= 0)
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX idx_metas_usuario ON metas(usuario_id)")
        criarDespesas(db)
        adicionarParcelas(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Migrações incrementais. Cada bloco leva o banco da versão N-1 para N.
        if (oldVersion < 2) criarDespesas(db)
        if (oldVersion < 3) adicionarParcelas(db)
    }

    private fun criarDespesas(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE despesas(
                id         INTEGER PRIMARY KEY,
                usuario_id INTEGER NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
                descricao  TEXT    NOT NULL CHECK(length(descricao) BETWEEN 3 AND 100),
                tipo       TEXT    NOT NULL CHECK(tipo IN ('Fixa', 'Variável')),
                valor      INTEGER NOT NULL CHECK(valor > 0)
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX idx_despesas_usuario ON despesas(usuario_id)")
    }

    private fun adicionarParcelas(db: SQLiteDatabase) {
        db.execSQL("ALTER TABLE despesas ADD COLUMN parcelas INTEGER NOT NULL DEFAULT 1 CHECK(parcelas BETWEEN 1 AND 120)")
        db.execSQL("ALTER TABLE despesas ADD COLUMN parcelas_pagas INTEGER NOT NULL DEFAULT 0 CHECK(parcelas_pagas >= 0)")
    }

    private companion object {
        const val VERSAO = 3
    }
}
