package com.example.sistemafinancas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.sistemafinancas.navigation.AppNavigation
import com.example.sistemafinancas.navigation.Menu
import com.example.sistemafinancas.navigation.ehRotaPublica
import com.example.sistemafinancas.ui.theme.SistemaFinancasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SistemaFinancasTheme(darkTheme = false, dynamicColor = false) {
                val navController = rememberNavController()
                val entradaAtual by navController.currentBackStackEntryAsState()
                val destino = entradaAtual?.destination
                val mostrarMenu = destino != null && !destino.ehRotaPublica()

                Scaffold(bottomBar = { if (mostrarMenu) Menu(navController) }) { innerPadding ->
                    AppNavigation(navController = navController, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
