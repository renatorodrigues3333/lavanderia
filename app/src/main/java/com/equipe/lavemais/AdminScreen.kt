package com.equipe.lavemais

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.LocalLaundryService
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Schedule
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
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.round

// Paleta Oficial de Cores do Aplicativo (Padronizada com HomeScreen e PerfilScreen)
private val backgroundColor = Color(0xFFFAFAFA)
private val primaryPurple = Color(0xFF673AB7)
private val lightPurpleCard = Color(0xFFF3E5F5)
private val textDark = Color(0xFF212121)
private val textGrey = Color(0xFF757575)
private val strokeColor = Color(0xFFEEEEEE)

/** Painel do administrador: cadastro e edição de máquinas, serviços e lavanderia. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    nome: String,
    maquinas: List<MaquinaResponse>,
    servicos: List<ServicoResponse>,
    chamados: List<SuporteResponse>,
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
    onEstadoChamado: (Int, String) -> Unit,
    onRemoverUsuario: (SuporteResponse) -> Unit,
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
    var chamadoRemovendo by remember { mutableStateOf<SuporteResponse?>(null) }

    val chamadosAbertos = chamados.count { it.estado == "aberto" || it.estado == "em_atendimento" }

    LaunchedEffect(mensagem) {
        if (mensagem != null) {
            snackbar.showSnackbar(mensagem)
            onMensagemExibida()
        }
    }

    Scaffold(
        containerColor = backgroundColor,
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Painel Admin",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = textDark
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(lightPurpleCard)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("Lave +", color = primaryPurple, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        if (nome.isNotBlank()) {
                            Text(nome, fontSize = 12.sp, color = textGrey)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = textDark
                ),
                actions = {
                    IconButton(onClick = onAtualizar) {
                        Icon(Icons.Outlined.Refresh, contentDescription = "Atualizar", tint = primaryPurple)
                    }
                    IconButton(onClick = onSair) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Sair", tint = textDark)
                    }
                }
            )
        },
        floatingActionButton = {
            if (aba < 2) ExtendedFloatingActionButton(
                onClick = {
                    if (aba == 0) {
                        maquinaEditando = null
                        mostrarFormMaquina = true
                    } else {
                        servicoEditando = null
                        mostrarFormServico = true
                    }
                },
                containerColor = primaryPurple,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text(if (aba == 0) "Nova máquina" else "Novo serviço", fontWeight = FontWeight.Bold) }
            )
        }
    ) { interno ->
        Column(modifier = Modifier.padding(interno).fillMaxSize()) {
            ScrollableTabRow(
                selectedTabIndex = aba,
                containerColor = Color.White,
                contentColor = primaryPurple,
                edgePadding = 0.dp,
                indicator = { tabPositions ->
                    if (aba < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[aba]),
                            color = primaryPurple
                        )
                    }
                }
            ) {
                Tab(
                    selected = aba == 0,
                    onClick = { aba = 0 },
                    selectedContentColor = primaryPurple,
                    unselectedContentColor = textGrey,
                    text = {
                        Text(
                            "Máquinas (${maquinas.size})",
                            fontWeight = if (aba == 0) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                )
                Tab(
                    selected = aba == 1,
                    onClick = { aba = 1 },
                    selectedContentColor = primaryPurple,
                    unselectedContentColor = textGrey,
                    text = {
                        Text(
                            "Serviços (${servicos.size})",
                            fontWeight = if (aba == 1) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                )
                Tab(
                    selected = aba == 2,
                    onClick = { aba = 2 },
                    selectedContentColor = primaryPurple,
                    unselectedContentColor = textGrey,
                    text = {
                        Text(
                            "Unidade",
                            fontWeight = if (aba == 2) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                )
                Tab(
                    selected = aba == 3,
                    onClick = { aba = 3 },
                    selectedContentColor = primaryPurple,
                    unselectedContentColor = textGrey,
                    text = {
                        Text(
                            if (chamadosAbertos > 0) "Chamados ($chamadosAbertos)" else "Chamados",
                            fontWeight = if (aba == 3) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                )
            }

            if (carregando) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = primaryPurple)
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (aba == 0) {
                    if (maquinas.isEmpty() && !carregando) {
                        item {
                            TextoVazio(
                                icone = Icons.Outlined.LocalLaundryService,
                                titulo = "Nenhuma máquina cadastrada",
                                mensagem = "Toque no botão \"Nova máquina\" abaixo para cadastrar a primeira unidade de lavagem ou secagem."
                            )
                        }
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
                        item {
                            TextoVazio(
                                icone = Icons.Outlined.Build,
                                titulo = "Nenhum serviço cadastrado",
                                mensagem = "Toque no botão \"Novo serviço\" abaixo para incluir tipos de ciclo, lavagem ou secagem."
                            )
                        }
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
                } else if (aba == 2) {
                    item {
                        LavanderiaForm(
                            atual = lavanderia,
                            salvando = salvandoLavanderia,
                            onSalvar = onSalvarLavanderia
                        )
                    }
                } else {
                    if (chamados.isEmpty() && !carregando) {
                        item {
                            TextoVazio(
                                icone = Icons.Outlined.CheckCircle,
                                titulo = "Nenhum chamado por aqui",
                                mensagem = "Quando um cliente enviar uma solicitação de suporte, reembolso ou exclusão de conta, ela aparece nesta lista."
                            )
                        }
                    }
                    items(chamados, key = { it.id }) { chamado ->
                        ChamadoCard(
                            chamado = chamado,
                            onEstado = { onEstadoChamado(chamado.id, it) },
                            onRemoverUsuario = { chamadoRemovendo = chamado }
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

    chamadoRemovendo?.let { alvo ->
        AlertDialog(
            onDismissRequest = { chamadoRemovendo = null },
            title = { Text("Remover ${alvo.usuarioNome}?") },
            text = {
                Text(
                    "A conta ${alvo.usuarioEmail} será apagada, junto com as notificações e os códigos de " +
                        "recuperação de senha. Pedidos, pagamentos e chamados ficam no histórico, sem nome " +
                        "nem e-mail. Essa ação não pode ser desfeita."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onRemoverUsuario(alvo)
                    chamadoRemovendo = null
                }) { Text("Remover", color = Color(0xFFC62828), fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { chamadoRemovendo = null }) { Text("Cancelar", color = textGrey) }
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
private fun TextoVazio(icone: ImageVector, titulo: String, mensagem: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, strokeColor),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icone,
                contentDescription = null,
                tint = textGrey,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = titulo,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = textDark
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = mensagem,
                fontSize = 13.sp,
                color = textGrey,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
        }
    }
}

private fun textoEstado(estado: String): String = when (estado.lowercase()) {
    "livre" -> "Livre"
    "em_uso" -> "Em uso"
    "manutencao" -> "Em manutenção"
    "indisponivel" -> "Indisponível"
    else -> estado
}

private fun corEstado(estado: String): Color = when (estado.lowercase()) {
    "livre" -> Color(0xFF2E7D32)
    "em_uso" -> Color(0xFF1565C0)
    "manutencao" -> Color(0xFFE65100)
    else -> Color(0xFFC62828)
}

private fun fundoEstado(estado: String): Color = when (estado.lowercase()) {
    "livre" -> Color(0xFFE8F5E9)
    "em_uso" -> Color(0xFFE3F2FD)
    "manutencao" -> Color(0xFFFFF3E0)
    else -> Color(0xFFFFEBEE)
}

private fun iconeEstado(estado: String): ImageVector = when (estado.lowercase()) {
    "livre" -> Icons.Outlined.CheckCircle
    "em_uso" -> Icons.Outlined.Schedule
    "manutencao" -> Icons.Outlined.Build
    else -> Icons.Outlined.Block
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
        border = BorderStroke(1.dp, strokeColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = maquina.nome,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = textDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // Pílula de Status Colorida (Seguindo o design da HomeScreen)
                    Row(
                        modifier = Modifier
                            .background(fundoEstado(maquina.estado), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            iconeEstado(maquina.estado),
                            contentDescription = null,
                            tint = corEstado(maquina.estado),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = textoEstado(maquina.estado),
                            color = corEstado(maquina.estado),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = kg(maquina.capacidade),
                    fontSize = 13.sp,
                    color = textGrey,
                    fontWeight = FontWeight.Medium
                )

                if (!maquina.descricao.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = maquina.descricao,
                        fontSize = 12.sp,
                        color = textGrey,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 16.sp
                    )
                }
            }

            Box {
                IconButton(onClick = { menu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Mais opções", tint = textGrey)
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
        border = BorderStroke(1.dp, strokeColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = servico.nome,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = textDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${reais(servico.preco)}  ·  ${servico.duracaoEstimada} min",
                    fontSize = 13.sp,
                    color = primaryPurple,
                    fontWeight = FontWeight.Bold
                )
                if (!servico.descricao.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = servico.descricao,
                        fontSize = 12.sp,
                        color = textGrey,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 16.sp
                    )
                }
            }
            Box {
                IconButton(onClick = { menu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Mais opções", tint = textGrey)
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
        shape = RoundedCornerShape(12.dp),
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
        border = BorderStroke(1.dp, strokeColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Dados da unidade de lavanderia", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = textDark)
            Text(
                "O nome e o endereço aparecem na tela inicial e no topo das telas dos clientes.",
                fontSize = 13.sp,
                color = textGrey,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            CampoAdmin("Nome da unidade", nome, { nome = it }, if (tentou) erroNome else null)
            CampoAdmin("Endereço *", endereco, { endereco = it }, if (tentou) erroEndereco else null)
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    tentou = true
                    if (erroNome == null && erroEndereco == null) onSalvar(nome, endereco)
                },
                enabled = !salvando,
                colors = ButtonDefaults.buttonColors(containerColor = primaryPurple),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text(if (salvando) "Salvando..." else "Salvar alterações", fontWeight = FontWeight.Bold)
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
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp),
        title = { Text(if (atual == null) "Nova máquina" else "Editar máquina", fontWeight = FontWeight.Bold, color = textDark) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
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
            }) { Text("Salvar", color = primaryPurple, fontWeight = FontWeight.Bold) }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar", color = textGrey) } }
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
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp),
        title = { Text(if (atual == null) "Novo serviço" else "Editar serviço", fontWeight = FontWeight.Bold, color = textDark) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
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
            }) { Text("Salvar", color = primaryPurple, fontWeight = FontWeight.Bold) }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar", color = textGrey) } }
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
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp),
        title = { Text(titulo, fontWeight = FontWeight.Bold, color = textDark) },
        text = { Text(texto, color = textGrey, fontSize = 13.sp, lineHeight = 18.sp) },
        confirmButton = {
            TextButton(onClick = onConfirmar) {
                Text("Excluir", color = Color(0xFFC62828), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar", color = textGrey) } }
    )
}

private data class VisualChamado(val texto: String, val cor: Color, val fundo: Color, val icone: ImageVector)

private fun visualChamado(estado: String): VisualChamado = when (estado) {
    "aberto" -> VisualChamado("Pendente", Color(0xFFE65100), Color(0xFFFFF3E0), Icons.Outlined.Schedule)
    "em_atendimento" -> VisualChamado("Em atendimento", Color(0xFF1565C0), Color(0xFFE3F2FD), Icons.Outlined.Build)
    "resolvido" -> VisualChamado("Resolvido", Color(0xFF2E7D32), Color(0xFFE8F5E9), Icons.Outlined.CheckCircle)
    else -> VisualChamado("Cancelado", Color(0xFFC62828), Color(0xFFFFEBEE), Icons.Outlined.Block)
}

/** Chamado aberto por um cliente: suporte, reembolso ou pedido de exclusão de conta. */
@Composable
private fun ChamadoCard(
    chamado: SuporteResponse,
    onEstado: (String) -> Unit,
    onRemoverUsuario: () -> Unit
) {
    val visual = visualChamado(chamado.estado)
    val encerrado = chamado.estado == "resolvido" || chamado.estado == "cancelado"
    val exclusao = chamado.tema == TEMA_EXCLUSAO_CONTA
    val usuarioExiste = chamado.usuarioNome != null

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, strokeColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = chamado.tema?.takeIf { it.isNotBlank() } ?: "Chamado #${chamado.id}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = textDark,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Row(
                    modifier = Modifier
                        .background(visual.fundo, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(visual.icone, contentDescription = null, tint = visual.cor, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = visual.texto, color = visual.cor, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (usuarioExiste) "${chamado.usuarioNome} · ${chamado.usuarioEmail}" else "Usuário removido",
                fontSize = 13.sp,
                color = textGrey
            )
            dataApi(chamado.criadoEm)?.let {
                Text(text = dataHistorico(it), fontSize = 12.sp, color = textGrey)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = chamado.descricao, fontSize = 14.sp, color = textDark, lineHeight = 20.sp)

            if (!encerrado) {
                Spacer(modifier = Modifier.height(12.dp))
                if (exclusao && usuarioExiste) {
                    Button(
                        onClick = onRemoverUsuario,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    ) {
                        Text("Remover usuário e concluir", fontWeight = FontWeight.Bold)
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    if (chamado.estado == "aberto") {
                        TextButton(onClick = { onEstado("em_atendimento") }) {
                            Text("Atender", color = primaryPurple, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    if (!exclusao || !usuarioExiste) {
                        TextButton(onClick = { onEstado("resolvido") }) {
                            Text("Resolver", color = Color(0xFF2E7D32), fontWeight = FontWeight.SemiBold)
                        }
                    }
                    TextButton(onClick = { onEstado("cancelado") }) {
                        Text(if (exclusao) "Recusar" else "Cancelar", color = Color(0xFFC62828), fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
