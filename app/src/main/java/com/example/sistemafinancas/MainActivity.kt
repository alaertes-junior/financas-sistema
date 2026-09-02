package com.example.sistemafinancas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppFinanceiro()
        }
    }
}

// --- ESTRUTURA DE DADOS ---
data class Meta(
    val id: Int,
    val nome: String,
    val categoria: String,
    val tipo: String,
    val valorGuardado: Double,
    val valorObjetivo: Double
) {
    val porcentagem: Int
        get() = if (valorObjetivo > 0) ((valorGuardado / valorObjetivo) * 100).toInt() else 0
}

// --- TELA PRINCIPAL QUE CONTROLA A NAVEGAÇÃO ---
@Composable
fun AppFinanceiro() {
    var telaAtual by remember { mutableStateOf("perfil") } // Mudei para iniciar no perfil para você testar direto

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
                        val novaMeta = Meta(id = proximoId, nome = nome, categoria = categoria, tipo = tipo, valorGuardado = guardado, valorObjetivo = objetivo)
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
                                listaMetas = listaMetas.map {
                                    if (it.id == metaAtualizada.id) metaAtualizada else it
                                }
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

// --- FUNÇÃO PARA PEGAR O ÍCONE ---
@Composable
fun obterIconePorCategoria(categoria: String): Int {
    return when (categoria) {
        "Reserva" -> R.drawable.ic_saving
        "Automóvel" -> R.drawable.ic_car
        "Viagem" -> R.drawable.ic_airplane
        "Casa própria" -> R.drawable.ic_home
        "Outros" -> R.drawable.ic_dots
        else -> R.drawable.ic_dots
    }
}

// --- TELA 1: METAS ---
@Composable
fun TelaMetas(
    metas: List<Meta>,
    aoClicarNovaMeta: () -> Unit,
    aoClicarNaMeta: (Meta) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Metas", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
        Text(
            text = "Suas poupanças em andamento",
            color = Color.Gray,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(metas) { meta ->
                ItemMetaCard(meta = meta, aoClicar = { aoClicarNaMeta(meta) })
            }
        }

        Button(
            onClick = aoClicarNovaMeta,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text("+ Nova poupança", fontSize = 16.sp)
        }
    }
}

// --- COMPONENTE VISUAL DA META UTILIZANDO CARD ---
@Composable
fun ItemMetaCard(meta: Meta, aoClicar: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { aoClicar() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F2F2)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(Color.White, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = obterIconePorCategoria(meta.categoria)),
                    contentDescription = "Ícone de ${meta.categoria}",
                    modifier = Modifier.size(32.dp),
                    tint = Color.Black
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = meta.nome,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.weight(1f)
                    )

                    if (meta.tipo.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFE0E0E0), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = meta.tipo.uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.DarkGray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "R$ ${meta.valorGuardado.toInt()} de R$ ${meta.valorObjetivo.toInt()}",
                    color = Color.Gray,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(Color(0xFFD6D6D6), RoundedCornerShape(4.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(
                                if(meta.valorObjetivo > 0)
                                    (meta.valorGuardado.toFloat() / meta.valorObjetivo.toFloat()).coerceIn(0f, 1f)
                                else 0f
                            )
                            .height(8.dp)
                            .background(Color.Black, RoundedCornerShape(4.dp))
                    )
                }
            }
        }
    }
}

// --- TELA 2: NOVA POUPANÇA ---
@Composable
fun TelaNovaPoupanca(aoCriarMeta: (String, String, String, Double, Double) -> Unit, aoCancelar: () -> Unit) {
    var nome by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("") }
    var categoriaSelecionada by remember { mutableStateOf("Casa própria") }
    var valorObjetivoStr by remember { mutableStateOf("") }
    var valorGuardadoStr by remember { mutableStateOf("") }

    val opcoesCategoria = listOf("Reserva", "Automóvel", "Viagem", "Casa própria", "Outros")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Text(text = "Nova poupança", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text(text = "Defina uma meta para guardar dinheiro", color = Color.Gray, modifier = Modifier.padding(bottom = 24.dp))

        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome da meta") },
            placeholder = { Text("Ex: Viagem Chile") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Selecione a categoria (Ícone)", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            opcoesCategoria.forEach { opcao ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { categoriaSelecionada = opcao }
                        .padding(vertical = 4.dp, horizontal = 4.dp)
                ) {
                    RadioButton(
                        selected = categoriaSelecionada == opcao,
                        onClick = { categoriaSelecionada = opcao }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = opcao, fontSize = 16.sp)
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = tipo,
            onValueChange = { tipo = it },
            label = { Text("Tag de identificação (Opcional)") },
            placeholder = { Text("Ex: Férias, Urgência") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = valorObjetivoStr,
            onValueChange = { digitado ->
                val texto = digitado.replace(",", ".")
                if (texto.isEmpty() || texto.toDoubleOrNull() != null) {
                    valorObjetivoStr = texto
                }
            },
            label = { Text("Valor objetivo") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = valorGuardadoStr,
            onValueChange = { digitado ->
                val texto = digitado.replace(",", ".")
                if (texto.isEmpty() || texto.toDoubleOrNull() != null) {
                    valorGuardadoStr = texto
                }
            },
            label = { Text("Valor já guardado") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            OutlinedButton(onClick = aoCancelar, modifier = Modifier.weight(1f)) {
                Text("Cancelar")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = {
                    val objetivo = valorObjetivoStr.toDoubleOrNull() ?: 0.0
                    val guardado = valorGuardadoStr.toDoubleOrNull() ?: 0.0
                    if (nome.isNotBlank() && objetivo > 0) {
                        aoCriarMeta(nome, categoriaSelecionada, tipo, guardado, objetivo)
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("Criar")
            }
        }
    }
}

// --- TELA DE EDIÇÃO ---
@Composable
fun TelaEditarMeta(metaOriginal: Meta, aoSalvar: (Meta) -> Unit, aoCancelar: () -> Unit) {
    var nome by remember { mutableStateOf(metaOriginal.nome) }
    var tipo by remember { mutableStateOf(metaOriginal.tipo) }
    var categoriaSelecionada by remember { mutableStateOf(metaOriginal.categoria) }
    var valorObjetivoStr by remember { mutableStateOf(metaOriginal.valorObjetivo.toString().removeSuffix(".0")) }
    var valorGuardadoStr by remember { mutableStateOf(metaOriginal.valorGuardado.toString().removeSuffix(".0")) }

    val opcoesCategoria = listOf("Reserva", "Automóvel", "Viagem", "Casa própria", "Outros")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Text(text = "Editar poupança", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text(text = "Atualize os valores da sua meta", color = Color.Gray, modifier = Modifier.padding(bottom = 24.dp))

        OutlinedTextField(value = nome, onValueChange = { nome = it }, label = { Text("Nome da meta") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Selecione a categoria (Ícone)", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            opcoesCategoria.forEach { opcao ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).clickable { categoriaSelecionada = opcao }.padding(vertical = 4.dp, horizontal = 4.dp)) {
                    RadioButton(selected = categoriaSelecionada == opcao, onClick = { categoriaSelecionada = opcao })
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = opcao, fontSize = 16.sp)
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = tipo, onValueChange = { tipo = it }, label = { Text("Tag de identificação (Opcional)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = valorObjetivoStr, onValueChange = { valorObjetivoStr = it.replace(",", ".") }, label = { Text("Valor objetivo") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = valorGuardadoStr, onValueChange = { valorGuardadoStr = it.replace(",", ".") }, label = { Text("Valor já guardado") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            OutlinedButton(onClick = aoCancelar, modifier = Modifier.weight(1f)) { Text("Cancelar") }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = { aoSalvar(Meta(metaOriginal.id, nome, categoriaSelecionada, tipo, valorGuardadoStr.toDoubleOrNull()?:0.0, valorObjetivoStr.toDoubleOrNull()?:0.0)) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color.Black)) { Text("Salvar") }
        }
    }
}

// --- TELA 3: PERFIL ---
@Composable
fun TelaPerfil(
    nome: String,
    email: String,
    aoMudarNome: (String) -> Unit,
    aoMudarEmail: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // FOTO DE PERFIL
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(Color(0xFFF2F2F2), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_profile),
                contentDescription = "Foto de perfil",
                modifier = Modifier.size(40.dp),
                tint = Color.DarkGray
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // INPUT DO NOME
        OutlinedTextField(
            value = nome,
            onValueChange = aoMudarNome,
            label = { Text("Nome de usuário") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(0.9f),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // INPUT DO E-MAIL
        OutlinedTextField(
            value = email,
            onValueChange = aoMudarEmail,
            label = { Text("E-mail") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(0.9f),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(28.dp))

        // ITENS DO MENU
        ItemMenuPerfil(
            texto = "Preferências da conta",
            icone = R.drawable.ic_settings,
            onClick = { }
        )

        ItemMenuPerfil(
            texto = "Notificações",
            icone = R.drawable.ic_bell,
            onClick = { }
        )

        ItemMenuPerfil(
            texto = "Metas e orçamento",
            icone = R.drawable.ic_flag,
            onClick = { }
        )

        ItemMenuPerfil(
            texto = "Contas conectadas",
            icone = R.drawable.ic_profile,
            onClick = { }
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedButton(
            onClick = { },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Sair da conta", color = Color.Red)
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// --- BOTÃO DO MENU DO PERFIL ---
@Composable
fun ItemMenuPerfil(texto: String, icone: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFF2F2F2))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Color.White, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = icone),
                    contentDescription = "Ícone de $texto",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Black
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = texto,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
        }
    }
}

// --- COMPONENTE: BARRA DE NAVEGAÇÃO ---
@Composable
fun BarraNavegacao(telaAtual: String, aoMudarTela: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)
            .background(Color.White),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {

        // --- ÍCONE METAS ---
        val corMetas = if (telaAtual in listOf("metas", "nova_meta", "editar_meta")) Color.Black else Color.Gray
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clickable { aoMudarTela("metas") },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_statistic),
                contentDescription = "Ícone de Metas",
                tint = corMetas,
                modifier = Modifier.size(28.dp)
            )
        }

        // --- ÍCONE PERFIL ---
        val corPerfil = if (telaAtual == "perfil") Color.Black else Color.Gray
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clickable { aoMudarTela("perfil") },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_profile),
                contentDescription = "Ícone de Perfil",
                tint = corPerfil,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}