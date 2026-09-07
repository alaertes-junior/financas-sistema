package com.example.sistemafinancas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlin.collections.plus

// --- TELA PRINCIPAL QUE CONTROLA A NAVEGAÇÃO ---
@Composable
fun AppFinanceiro() {
    var telaAtual by remember { mutableStateOf("perfil") }

    var proximoId by remember { mutableIntStateOf(5) }

    var listaMetas by remember { mutableStateOf(
        listOf(
            Meta(1, "Casa Própria", "Casa própria", "Imóvel", 25000.0, 90000.0),
            Meta(2, "Nivus 2024", "Automóvel", "Veículo", 18500.0, 30000.0),
            Meta(3, "Casamento", "Viagem", "Evento", 7425.0, 20000.0),
            Meta(4, "Emergência", "Reserva", "Reserva", 12000.0, 15000.0)
        )
    ) }

    var metaEmEdicao by remember { mutableStateOf<Meta?>(null) }

    var nomePerfil by remember { mutableStateOf("Lorenzo Sorrentino") }
    var emailPerfil by remember { mutableStateOf("lorenzo@podiapp.com.br") }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {

        Box(modifier = Modifier.weight(1f)) {
            when (telaAtual) {
                "metas" -> TelaMetas(
                    metas = listaMetas,
                    aoClicarNovaMeta = { telaAtual = "nova_meta" },
                    aoClicarNaMeta = { metaClicada ->
                        metaEmEdicao = metaClicada
                        telaAtual = "editar_meta"
                    }
                )

                "nova_meta" -> TelaNovaPoupanca(
                    aoCriarMeta = { nome, categoria, tipo, guardado, objetivo ->
                        val novaMeta = Meta(
                            id = proximoId,
                            nome = nome,
                            categoria = categoria,
                            tipo = tipo,
                            valorGuardado = guardado,
                            valorObjetivo = objetivo
                        )
                        proximoId++
                        listaMetas = listaMetas + novaMeta
                        telaAtual = "metas"
                    },
                    aoCancelar = { telaAtual = "metas" }
                )

                "editar_meta" -> {
                    metaEmEdicao?.let { meta ->
                        TelaEditarMeta(
                            metaOriginal = meta,
                            aoSalvar = { metaAtualizada ->
                                val novaLista = mutableListOf<Meta>()

                                listaMetas.forEach { item ->
                                    if (item.id == metaAtualizada.id) {
                                        novaLista.add(metaAtualizada)
                                    } else {
                                        novaLista.add(item)
                                    }
                                }

                                listaMetas = novaLista
                                telaAtual = "metas"
                            },
                            aoCancelar = { telaAtual = "metas" }
                        )
                    }
                }

                "perfil" -> TelaPerfil(
                    nome = nomePerfil,
                    email = emailPerfil,
                    aoMudarNome = { nomePerfil = it },
                    aoMudarEmail = { emailPerfil = it }
                )
            }
        }

        BarraNavegacao(
            telaAtual = telaAtual,
            aoMudarTela = { telaAtual = it }
        )
    }
}