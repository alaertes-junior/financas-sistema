package com.example.sistemafinancas.navigation

import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import kotlinx.serialization.Serializable

@Serializable data object Login
@Serializable data object Cadastro

@Serializable data object Metas
@Serializable data object CadastrarMeta
@Serializable data class MetaEditar(val id: Long)

@Serializable data object Despesas
@Serializable data object CadastrarDespesa
@Serializable data class DespesaEditar(val id: Long)

@Serializable data object Usuarios
@Serializable data object CadastrarUsuario
@Serializable data class UsuarioEditar(val id: Long)

@Serializable data object Perfil

/** Rotas acessíveis sem login. Fonte única de verdade. */
fun NavDestination.ehRotaPublica(): Boolean = hasRoute<Login>() || hasRoute<Cadastro>()
