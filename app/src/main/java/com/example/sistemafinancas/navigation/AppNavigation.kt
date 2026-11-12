package com.example.sistemafinancas.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.toRoute
import com.example.sistemafinancas.auth.SessaoUsuario
import com.example.sistemafinancas.ui.screens.CadastroScreen
import com.example.sistemafinancas.ui.screens.DespesaCadastroScreen
import com.example.sistemafinancas.ui.screens.DespesaEditarScreen
import com.example.sistemafinancas.ui.screens.DespesasScreen
import com.example.sistemafinancas.ui.screens.LoginScreen
import com.example.sistemafinancas.ui.screens.MetaCadastroScreen
import com.example.sistemafinancas.ui.screens.MetaEditarScreen
import com.example.sistemafinancas.ui.screens.MetasScreen
import com.example.sistemafinancas.ui.screens.MoedaCadastroScreen
import com.example.sistemafinancas.ui.screens.MoedaEditarScreen
import com.example.sistemafinancas.ui.screens.MoedasScreen
import com.example.sistemafinancas.ui.screens.ReceitaCadastroScreen
import com.example.sistemafinancas.ui.screens.ReceitaEditarScreen
import com.example.sistemafinancas.ui.screens.ReceitasScreen
import com.example.sistemafinancas.ui.screens.PerfilScreen
import com.example.sistemafinancas.ui.screens.UsuarioEditarScreen
import com.example.sistemafinancas.ui.screens.UsuarioScreen
import com.example.sistemafinancas.ui.viewmodel.AuthViewModel
import com.example.sistemafinancas.ui.viewmodel.DespesaViewModel
import com.example.sistemafinancas.ui.viewmodel.MetaViewModel
import com.example.sistemafinancas.ui.viewmodel.MoedaViewModel
import com.example.sistemafinancas.ui.viewmodel.ReceitaViewModel
import com.example.sistemafinancas.ui.viewmodel.UsuarioViewModel

@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    val entradaAtual by navController.currentBackStackEntryAsState()
    val destino = entradaAtual?.destination
    val usuarioAtual = SessaoUsuario.usuarioId

    LaunchedEffect(destino, usuarioAtual) {
        if (destino != null && !destino.ehRotaPublica() && usuarioAtual == null) {
            navController.navigate(Login) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    NavHost(navController = navController, startDestination = Login, modifier = modifier) {
        composable<Login> {
            LoginScreen(
                authViewModel = viewModel(),
                irParaMetas = { navController.navigate(Metas) { popUpTo(Login) { inclusive = true } } },
                irParaCadastro = { navController.navigate(Cadastro) }
            )
        }
        composable<Cadastro> {
            CadastroScreen(authViewModel = viewModel(), aoConcluir = { navController.popBackStack() })
        }

        // ----- Metas -----
        composable<Metas> { entrada ->
            val metaViewModel: MetaViewModel = viewModel()
            RecarregarAoRetomar(entrada) { metaViewModel.listar() }
            MetasScreen(
                viewModel = metaViewModel,
                irParaCadastro = { navController.navigate(CadastrarMeta) },
                irParaEdicao = { navController.navigate(MetaEditar(it)) }
            )
        }
        composable<CadastrarMeta> {
            MetaCadastroScreen(viewModel = viewModel(), voltar = { navController.popBackStack() })
        }
        composable<MetaEditar> { entrada ->
            val rota = entrada.toRoute<MetaEditar>()
            MetaEditarScreen(metaId = rota.id, viewModel = viewModel(), voltar = { navController.popBackStack() })
        }

        // ----- Despesas -----
        composable<Despesas> { entrada ->
            val despesaViewModel: DespesaViewModel = viewModel()
            RecarregarAoRetomar(entrada) { despesaViewModel.listar() }
            DespesasScreen(
                viewModel = despesaViewModel,
                irParaCadastro = { navController.navigate(CadastrarDespesa) },
                irParaEdicao = { navController.navigate(DespesaEditar(it)) }
            )
        }
        composable<CadastrarDespesa> {
            DespesaCadastroScreen(viewModel = viewModel(), voltar = { navController.popBackStack() })
        }
        composable<DespesaEditar> { entrada ->
            val rota = entrada.toRoute<DespesaEditar>()
            DespesaEditarScreen(despesaId = rota.id, viewModel = viewModel(), voltar = { navController.popBackStack() })
        }

        // ----- Receitas -----
        composable<Receitas> { entrada ->
            val receitaViewModel: ReceitaViewModel = viewModel()
            RecarregarAoRetomar(entrada) { receitaViewModel.listar() }
            ReceitasScreen(
                viewModel = receitaViewModel,
                irParaCadastro = { navController.navigate(CadastrarReceita) },
                irParaEdicao = { navController.navigate(ReceitaEditar(it)) }
            )
        }
        composable<CadastrarReceita> {
            ReceitaCadastroScreen(viewModel = viewModel(), voltar = { navController.popBackStack() })
        }
        composable<ReceitaEditar> { entrada ->
            val rota = entrada.toRoute<ReceitaEditar>()
            ReceitaEditarScreen(receitaId = rota.id, viewModel = viewModel(), voltar = { navController.popBackStack() })
        }

        // ----- Moedas acompanhadas (SQLite + API via Retrofit) -----
        composable<Moedas> { entrada ->
            val moedaViewModel: MoedaViewModel = viewModel()
            RecarregarAoRetomar(entrada) { moedaViewModel.listar() }
            MoedasScreen(
                viewModel = moedaViewModel,
                irParaCadastro = { navController.navigate(CadastrarMoeda) },
                irParaEdicao = { navController.navigate(MoedaEditar(it)) }
            )
        }
        composable<CadastrarMoeda> {
            MoedaCadastroScreen(viewModel = viewModel(), voltar = { navController.popBackStack() })
        }
        composable<MoedaEditar> { entrada ->
            val rota = entrada.toRoute<MoedaEditar>()
            MoedaEditarScreen(moedaId = rota.id, viewModel = viewModel(), voltar = { navController.popBackStack() })
        }

        // ----- Usuários -----
        composable<Usuarios> { entrada ->
            val usuarioViewModel: UsuarioViewModel = viewModel()
            RecarregarAoRetomar(entrada) { usuarioViewModel.listar() }
            UsuarioScreen(
                viewModel = usuarioViewModel,
                irParaCadastro = { navController.navigate(CadastrarUsuario) },
                irParaEdicao = { navController.navigate(UsuarioEditar(it)) }
            )
        }
        composable<CadastrarUsuario> {
            // Usuário logado criando outra conta: ao concluir, volta para a lista (NÃO vai para o Login).
            val authViewModel: AuthViewModel = viewModel()
            CadastroScreen(authViewModel = authViewModel, aoConcluir = { navController.popBackStack() })
        }
        composable<UsuarioEditar> { entrada ->
            val rota = entrada.toRoute<UsuarioEditar>()
            UsuarioEditarScreen(usuarioId = rota.id, viewModel = viewModel(), voltar = { navController.popBackStack() })
        }

        composable<Perfil> {
            PerfilScreen(
                irParaMetas = { navController.navigate(Metas) { launchSingleTop = true } },
                irParaUsuarios = { navController.navigate(Usuarios) { launchSingleTop = true } },
                aoSairDaConta = {
                    SessaoUsuario.encerrar()
                    navController.navigate(Login) { popUpTo(0) { inclusive = true } }
                }
            )
        }
    }
}

@Composable
private fun RecarregarAoRetomar(entrada: NavBackStackEntry, acao: () -> Unit) {
    val acaoAtual by rememberUpdatedState(acao)
    DisposableEffect(entrada) {
        val observador = LifecycleEventObserver { _, evento ->
            if (evento == Lifecycle.Event.ON_RESUME) acaoAtual()
        }
        entrada.lifecycle.addObserver(observador)
        onDispose { entrada.lifecycle.removeObserver(observador) }
    }
}
