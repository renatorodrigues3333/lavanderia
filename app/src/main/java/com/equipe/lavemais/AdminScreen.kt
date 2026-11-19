package com.equipe.lavemais

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.round

private val Roxo = Color(0xFF673AB7)
private val Fundo = Color(0xFFFAFAFA)
private val TextoEscuro = Color(0xFF212121)
private val TextoCinza = Color(0xFF757575)

/** Painel do administrador: cadastro e edição de máquinas e serviços. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    nome: String,
    maquinas: List<MaquinaResponse>,
    servicos: List<ServicoResponse>,
    lavanderia: LavanderiaResponse,
    salvandoLavanderia: Boolean,
    carregando: Boolean,
    mensagem: String?,
    onMensagemExibida: () -> Unit,
    onAtualizar: () -> Unit,
    onSalvarMaquina: (Int?, String, Double, String) -> Unit,
    onEstadoMaquina: (Int, String) -> Unit,
    onExcluirMaquina: (Int) -> Unit,
    onSalvarServico: (Int?, String, String, Double, Int) -> Unit,
    onExcluirServico: (Int) -> Unit,
    onSalvarLavanderia: (String, String) -> Unit,
    onSair: () -> Unit
) {
    var aba by remember { mutableIntStateOf(0) }
    val snackbar = remember { SnackbarHostState() }

    var mostrarFormMaquina by remember { mutableStateOf(false) }
    var maquinaEditando by remember { mutableStateOf<MaquinaResponse?>(null) }
    var maquinaExcluindo by remember { mutableStateOf<MaquinaResponse?>(null) }

    var mostrarFormServico by remember { mutableStateOf(false) }
    var servicoEditando by remember { mutableStateOf<ServicoResponse?>(null) }
    var servicoExcluindo by remember { mutableStateOf<ServicoResponse?>(null) }

    LaunchedEffect(mensagem) {
        if (mensagem != null) {
            snackbar.showSnackbar(mensagem)
            onMensagemExibida()
        }
    }

    Scaffold(
        containerColor = Fundo,
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Painel do administrador", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        if (nome.isNotBlank()) Text(nome, fontSize = 12.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Roxo,
                    titleContentColor = Color.White
                ),
                actions = {
                    TextButton(
                        onClick = onAtualizar,
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                    ) { Text("Atualizar") }
                    TextButton(
                        onClick = onSair,
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                    ) { Text("Sair") }
                }
            )
        },
        floatingActionButton = {
            if (aba != 2) ExtendedFloatingActionButton(
                onClick = {
                    if (aba == 0) {
                        maquinaEditando = null
                        mostrarFormMaquina = true
                    } else {
                        servicoEditando = null
                        mostrarFormServico = true
                    }
                },
                containerColor = Roxo,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text(if (aba == 0) "Nova máquina" else "Novo serviço") }
            )
        }
    ) { interno ->
        Column(modifier = Modifier.padding(interno).fillMaxSize()) {
            TabRow(selectedTabIndex = aba) {
                Tab(
                    selected = aba == 0,
                    onClick = { aba = 0 },
                    text = { Text("Máquinas (${maquinas.size})") }
                )
                Tab(
                    selected = aba == 1,
                    onClick = { aba = 1 },
                    text = { Text("Serviços (${servicos.size})") }
                )
                Tab(
                    selected = aba == 2,
                    onClick = { aba = 2 },
                    text = { Text("Lavanderia") }
                )
            }

            if (carregando) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = Roxo)
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (aba == 0) {
                    if (maquinas.isEmpty() && !carregando) {
                        item { TextoVazio("Nenhuma máquina cadastrada. Toque em \"Nova máquina\" para começar.") }
                    }
                    items(maquinas, key = { it.id }) { maquina ->
                        MaquinaCard(
                            maquina = maquina,
                            onEditar = {
                                maquinaEditando = maquina
                                mostrarFormMaquina = true
                            },
                            onEstado = { onEstadoMaquina(maquina.id, it) },
                            onExcluir = { maquinaExcluindo = maquina }
                        )
                    }
                } else if (aba == 1) {
                    if (servicos.isEmpty() && !carregando) {
                        item { TextoVazio("Nenhum serviço cadastrado. Toque em \"Novo serviço\" para começar.") }
                    }
                    items(servicos, key = { it.id }) { servico ->
                        ServicoCard(
                            servico = servico,
                            onEditar = {
                                servicoEditando = servico
                                mostrarFormServico = true
                            },
                            onExcluir = { servicoExcluindo = servico }
                        )
                    }
                } else {
                    item {
                        LavanderiaForm(
                            atual = lavanderia,
                            salvando = salvandoLavanderia,
                            onSalvar = onSalvarLavanderia
                        )
                    }
                }
            }
        }
    }

    if (mostrarFormMaquina) {
        MaquinaFormDialog(
            atual = maquinaEditando,
            onCancelar = { mostrarFormMaquina = false },
            onSalvar = { nomeMaquina, capacidade, descricao ->
                onSalvarMaquina(maquinaEditando?.id, nomeMaquina, capacidade, descricao)
                mostrarFormMaquina = false
            }
        )
    }

    if (mostrarFormServico) {
        ServicoFormDialog(
            atual = servicoEditando,
            onCancelar = { mostrarFormServico = false },
            onSalvar = { nomeServico, descricao, preco, duracao ->
                onSalvarServico(servicoEditando?.id, nomeServico, descricao, preco, duracao)
                mostrarFormServico = false
            }
        )
    }

    maquinaExcluindo?.let { alvo ->
        ConfirmarExclusao(
            titulo = "Excluir ${alvo.nome}?",
            texto = "A máquina deixa de aparecer para os clientes. O histórico de uso é mantido.",
            onCancelar = { maquinaExcluindo = null },
            onConfirmar = {
                onExcluirMaquina(alvo.id)
                maquinaExcluindo = null
            }
        )
    }

    servicoExcluindo?.let { alvo ->
        ConfirmarExclusao(
            titulo = "Excluir ${alvo.nome}?",
            texto = "O serviço deixa de aparecer para os clientes. Pedidos antigos continuam no histórico.",
            onCancelar = { servicoExcluindo = null },
            onConfirmar = {
                onExcluirServico(alvo.id)
                servicoExcluindo = null
            }
        )
    }
}

@Composable
private fun TextoVazio(texto: String) {
    Text(
        text = texto,
        color = TextoCinza,
        fontSize = 14.sp,
        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp)
    )
}

private fun textoEstado(estado: String): String = when (estado) {
    "livre" -> "Livre"
    "em_uso" -> "Em uso"
    "manutencao" -> "Em manutenção"
    "indisponivel" -> "Indisponível"
    else -> estado
}

private fun corEstado(estado: String): Color = when (estado) {
    "livre" -> Color(0xFF2E7D32)
    "em_uso" -> Color(0xFF1565C0)
    "manutencao" -> Color(0xFFEF6C00)
    else -> Color(0xFFC62828)
}

private fun numeroSimples(valor: Double): String =
    if (valor % 1.0 == 0.0) valor.toInt().toString() else valor.toString()

@Composable
private fun MaquinaCard(
    maquina: MaquinaResponse,
    onEditar: () -> Unit,
    onEstado: (String) -> Unit,
    onExcluir: () -> Unit
) {
    var menu by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(maquina.nome, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextoEscuro)
                Spacer(modifier = Modifier.height(2.dp))
                Text(kg(maquina.capacidade), fontSize = 13.sp, color = TextoCinza)
                if (!maquina.descricao.isNullOrBlank()) {
                    Text(maquina.descricao, fontSize = 13.sp, color = TextoCinza)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = textoEstado(maquina.estado),
                    color = corEstado(maquina.estado),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }
            Box {
                IconButton(onClick = { menu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Mais opções")
                }
                DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                    DropdownMenuItem(text = { Text("Editar") }, onClick = { menu = false; onEditar() })
                    if (maquina.estado != "livre") {
                        DropdownMenuItem(text = { Text("Marcar como livre") }, onClick = { menu = false; onEstado("livre") })
                    }
                    if (maquina.estado != "manutencao") {
                        DropdownMenuItem(text = { Text("Colocar em manutenção") }, onClick = { menu = false; onEstado("manutencao") })
                    }
                    if (maquina.estado != "indisponivel") {
                        DropdownMenuItem(text = { Text("Marcar como indisponível") }, onClick = { menu = false; onEstado("indisponivel") })
                    }
                    DropdownMenuItem(text = { Text("Excluir", color = Color(0xFFC62828)) }, onClick = { menu = false; onExcluir() })
                }
            }
        }
    }
}

@Composable
private fun ServicoCard(
    servico: ServicoResponse,
    onEditar: () -> Unit,
    onExcluir: () -> Unit
) {
    var menu by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(servico.nome, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextoEscuro)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    "${reais(servico.preco)}  ·  ${servico.duracaoEstimada} min",
                    fontSize = 13.sp,
                    color = Roxo,
                    fontWeight = FontWeight.SemiBold
                )
                if (!servico.descricao.isNullOrBlank()) {
                    Text(servico.descricao, fontSize = 13.sp, color = TextoCinza)
                }
            }
            Box {
                IconButton(onClick = { menu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Mais opções")
                }
                DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                    DropdownMenuItem(text = { Text("Editar") }, onClick = { menu = false; onEditar() })
                    DropdownMenuItem(text = { Text("Excluir", color = Color(0xFFC62828)) }, onClick = { menu = false; onExcluir() })
                }
            }
        }
    }
}

@Composable
private fun CampoAdmin(
    rotulo: String,
    valor: String,
    aoMudar: (String) -> Unit,
    erro: String?,
    teclado: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = valor,
        onValueChange = aoMudar,
        label = { Text(rotulo) },
        singleLine = true,
        isError = erro != null,
        supportingText = { if (erro != null) Text(erro) },
        keyboardOptions = KeyboardOptions(keyboardType = teclado),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun LavanderiaForm(
    atual: LavanderiaResponse,
    salvando: Boolean,
    onSalvar: (String, String) -> Unit
) {
    var nome by remember(atual) { mutableStateOf(atual.nome) }
    var endereco by remember(atual) { mutableStateOf(atual.endereco) }
    var tentou by remember { mutableStateOf(false) }

    val erroNome = if (nome.trim().length > 60) "Máximo de 60 caracteres." else null
    val erroEndereco = when {
        endereco.isBlank() -> "Informe o endereço."
        endereco.trim().length > 200 -> "Máximo de 200 caracteres."
        else -> null
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Dados da lavanderia", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextoEscuro)
            Text(
                "O nome e o endereço aparecem na tela inicial e no topo das telas dos clientes.",
                fontSize = 13.sp,
                color = TextoCinza
            )
            Spacer(modifier = Modifier.height(8.dp))
            CampoAdmin("Nome da unidade", nome, { nome = it }, if (tentou) erroNome else null)
            CampoAdmin("Endereço *", endereco, { endereco = it }, if (tentou) erroEndereco else null)
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    tentou = true
                    if (erroNome == null && erroEndereco == null) onSalvar(nome, endereco)
                },
                enabled = !salvando,
                colors = ButtonDefaults.buttonColors(containerColor = Roxo),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text(if (salvando) "Salvando..." else "Salvar", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun MaquinaFormDialog(
    atual: MaquinaResponse?,
    onCancelar: () -> Unit,
    onSalvar: (String, Double, String) -> Unit
) {
    var nome by remember { mutableStateOf(atual?.nome.orEmpty()) }
    var capacidade by remember { mutableStateOf(atual?.let { numeroSimples(it.capacidade) }.orEmpty()) }
    var descricao by remember { mutableStateOf(atual?.descricao.orEmpty()) }
    var tentou by remember { mutableStateOf(false) }

    val cap = capacidade.replace(',', '.').toDoubleOrNull()
    val erroNome = when {
        nome.isBlank() -> "Informe o nome."
        nome.trim().length > 30 -> "Máximo de 30 caracteres."
        else -> null
    }
    val erroCapacidade =
        if (cap == null || cap <= 0.0 || cap > 999.99) "Informe a capacidade em kg (até 999,99)." else null
    val erroDescricao = if (descricao.length > 200) "Máximo de 200 caracteres." else null

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(if (atual == null) "Nova máquina" else "Editar máquina") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                CampoAdmin("Nome *", nome, { nome = it }, if (tentou) erroNome else null)
                CampoAdmin(
                    "Capacidade (kg) *", capacidade, { capacidade = it },
                    if (tentou) erroCapacidade else null, KeyboardType.Decimal
                )
                CampoAdmin("Descrição", descricao, { descricao = it }, if (tentou) erroDescricao else null)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                tentou = true
                if (erroNome == null && erroCapacidade == null && erroDescricao == null && cap != null) {
                    onSalvar(nome.trim(), round(cap * 100) / 100.0, descricao)
                }
            }) { Text("Salvar", color = Roxo, fontWeight = FontWeight.Bold) }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar", color = TextoCinza) } }
    )
}

@Composable
private fun ServicoFormDialog(
    atual: ServicoResponse?,
    onCancelar: () -> Unit,
    onSalvar: (String, String, Double, Int) -> Unit
) {
    var nome by remember { mutableStateOf(atual?.nome.orEmpty()) }
    var descricao by remember { mutableStateOf(atual?.descricao.orEmpty()) }
    var preco by remember { mutableStateOf(atual?.let { numeroSimples(it.preco) }.orEmpty()) }
    var duracao by remember { mutableStateOf(atual?.duracaoEstimada?.toString().orEmpty()) }
    var tentou by remember { mutableStateOf(false) }

    val valor = preco.replace(',', '.').toDoubleOrNull()
    val minutos = duracao.trim().toIntOrNull()

    val erroNome = when {
        nome.isBlank() -> "Informe o nome."
        nome.trim().length > 50 -> "Máximo de 50 caracteres."
        else -> null
    }
    val erroPreco =
        if (valor == null || valor < 0.0 || valor > 9999.99) "Informe o preço em reais (ex.: 18,50)." else null
    val erroDuracao = if (minutos == null || minutos <= 0) "Informe a duração em minutos." else null

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(if (atual == null) "Novo serviço" else "Editar serviço") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                CampoAdmin("Nome *", nome, { nome = it }, if (tentou) erroNome else null)
                CampoAdmin("Descrição", descricao, { descricao = it }, null)
                CampoAdmin(
                    "Preço (R$) *", preco, { preco = it },
                    if (tentou) erroPreco else null, KeyboardType.Decimal
                )
                CampoAdmin(
                    "Duração (minutos) *", duracao, { duracao = it.filter { c -> c.isDigit() } },
                    if (tentou) erroDuracao else null, KeyboardType.Number
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                tentou = true
                if (erroNome == null && erroPreco == null && erroDuracao == null && valor != null && minutos != null) {
                    onSalvar(nome.trim(), descricao, round(valor * 100) / 100.0, minutos)
                }
            }) { Text("Salvar", color = Roxo, fontWeight = FontWeight.Bold) }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar", color = TextoCinza) } }
    )
}

@Composable
private fun ConfirmarExclusao(
    titulo: String,
    texto: String,
    onCancelar: () -> Unit,
    onConfirmar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(titulo) },
        text = { Text(texto) },
        confirmButton = {
            TextButton(onClick = onConfirmar) {
                Text("Excluir", color = Color(0xFFC62828), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar", color = TextoCinza) } }
    )
}
