package com.example.sistemafinancas.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.sistemafinancas.Meta
import com.example.sistemafinancas.screens.CadastroScreen
import com.example.sistemafinancas.screens.LoginScreen
import com.example.sistemafinancas.screens.TelaEditarMeta
import com.example.sistemafinancas.screens.TelaMetas
import com.example.sistemafinancas.screens.TelaNovaPoupanca
import com.example.sistemafinancas.screens.TelaPerfil

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val rotaAtual = navBackStackEntry?.destination?.route ?: Routes.LOGIN

   
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


    var usuariosCadastrados by remember { mutableStateOf(mapOf<String, String>()) }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Box(modifier = Modifier.weight(1f)) {
            NavHost(navController = navController, startDestination = Routes.LOGIN) {

                
                composable(Routes.LOGIN) {
                    LoginScreen(
                        aoEntrar = { email, senha ->
                            if (usuariosCadastrados.containsKey(email) && usuariosCadastrados[email] == senha) {
                                navController.navigate(Routes.METAS) {
                                    popUpTo(Routes.LOGIN) { inclusive = true }
                                }
                                ""
                            } else {
                                "E-mail ou senha inválidos."
                            }
                        },
                        aoIrParaCadastro = { navController.navigate(Routes.CADASTRO) }
                    )
                }

         
                composable(Routes.CADASTRO) {
                    CadastroScreen(
                        aoSalvar = { email, senha ->
                            if (email.isBlank() || senha.isBlank()) {
                                "E-mail e senha não podem ficar em branco."
                            } else if (usuariosCadastrados.containsKey(email)) {
                                "Este e-mail já está cadastrado."
                            } else {
                                usuariosCadastrados = usuariosCadastrados + (email to senha)
                                navController.popBackStack()
                                ""
                            }
                        },
                        aoVoltar = { navController.popBackStack() }
                    )
                }

            
                composable(Routes.METAS) {
                    TelaMetas(
                        metas = listaMetas,
                        aoClicarNovaMeta = { navController.navigate(Routes.NOVA_META) },
                        aoClicarNaMeta = { metaClicada ->
                            metaEmEdicao = metaClicada
                            navController.navigate(Routes.EDITAR_META)
                        }
                    )
                }

              
                composable(Routes.NOVA_META) {
                    TelaNovaPoupanca(
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
                            navController.popBackStack()
                        },
                        aoCancelar = { navController.popBackStack() }
                    )
                }

                
                composable(Routes.EDITAR_META) {
                    metaEmEdicao?.let { meta ->
                        TelaEditarMeta(
                            metaOriginal = meta,
                            aoSalvar = { metaAtualizada ->
                                val novaLista = mutableListOf<Meta>()
                                listaMetas.forEach { item ->
                                    if (item.id == metaAtualizada.id) novaLista.add(metaAtualizada)
                                    else novaLista.add(item)
                                }
                                listaMetas = novaLista
                                navController.popBackStack()
                            },
                            aoCancelar = { navController.popBackStack() }
                        )
                    }
                }

                
                composable(Routes.PERFIL) {
                    TelaPerfil(
                        aoSairDaConta = {
                            navController.navigate(Routes.LOGIN) {
                                popUpTo(Routes.METAS) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }

        if (rotaAtual != Routes.LOGIN && rotaAtual != Routes.CADASTRO) {
            BarraNavegacao(
                telaAtual = rotaAtual,
                aoMudarTela = { novaRota ->
                    navController.navigate(novaRota) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(Routes.METAS) { saveState = true }
                    }
                }
            )
        }
    }
}
