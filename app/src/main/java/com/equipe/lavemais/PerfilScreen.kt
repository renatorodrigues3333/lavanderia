package com.equipe.lavemais

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun PerfilScreen(
    nome: String = "Ana Clara Lima",
    email: String = "ana.lima@exemplo.com",
    telefone: String? = "11998764820",
    salvandoPerfil: Boolean = false,
    erroPerfil: String? = null,
    /** Pedidos do cliente, para escolher o "pedido relacionado" da solicitação. */
    pedidos: List<UtilizacaoApi> = emptyList(),
    solicitacoes: List<SuporteResponse> = emptyList(),
    notificacoes: List<NotificacaoResponse> = emptyList(),
    enviandoSolicitacao: Boolean = false,
    erroSolicitacao: String? = null,
    /** Recebe o assunto e a descrição, e uma função que limpa o formulário quando der certo. */
    onEnviarSolicitacao: (tema: String, descricao: String, limpar: () -> Unit) -> Unit = { _, _, limpar -> limpar() },
    onAbrirNotificacoes: () -> Unit = {},
    onLerNotificacao: (NotificacaoResponse) -> Unit = {},
    /** Pede a exclusão da conta; a função recebida fecha o diálogo quando der certo. */
    onSolicitarExclusao: (concluir: () -> Unit) -> Unit = { concluir -> concluir() },
    /** Chamado ao abrir o diálogo, para limpar erros antigos. */
    onAbrirEdicao: () -> Unit = {},
    /** Recebe os dados editados e uma função para fechar o diálogo quando der certo. */
    onSalvarPerfil: (dados: EdicaoPerfil, fechar: () -> Unit) -> Unit = { _, fechar -> fechar() },
    onNavegar: (String) -> Unit = {},
    onVoltar: () -> Unit = {},
    onSair: () -> Unit = {}
) {
    val iniciais = nome.trim().split(" ").filter { it.isNotBlank() }
        .take(2).joinToString("") { it.first().uppercase() }
    var editando by remember { mutableStateOf(false) }
    var tipoSolicitacao by remember { mutableStateOf("Suporte") }
    var descricaoSolicitacao by remember { mutableStateOf("") }
    var pedidoSelecionado by remember { mutableStateOf<UtilizacaoApi?>(null) }
    var motivoSelecionado by remember { mutableStateOf<String?>(null) }
    var tentouEnviar by remember { mutableStateOf(false) }
    var mostrarNotificacoes by remember { mutableStateOf(false) }
    var mostrarTermos by remember { mutableStateOf(false) }
    var confirmarExclusao by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val escopo = rememberCoroutineScope()
    var posicaoFormulario by remember { mutableIntStateOf(0) }

    // Cores base
    val backgroundColor = Color(0xFFFAFAFA)
    val primaryPurple = Color(0xFF673AB7)
    val textDark = Color(0xFF212121)
    val textGrey = Color(0xFF757575)
    val lightPurpleCard = Color(0xFFF3E5F5)
    val strokeColor = Color(0xFFEEEEEE)

    if (editando) {
        EditarPerfilDialog(
            nome = nome,
            email = email,
            telefone = telefone,
            salvando = salvandoPerfil,
            erroServidor = erroPerfil,
            onSalvar = { dados -> onSalvarPerfil(dados) { editando = false } },
            onCancelar = { editando = false }
        )
    }

    if (mostrarNotificacoes) {
        NotificacoesDialog(
            notificacoes = notificacoes,
            onLer = onLerNotificacao,
            onFechar = { mostrarNotificacoes = false }
        )
    }

    if (mostrarTermos) {
        TermosDialog(onFechar = { mostrarTermos = false })
    }

    if (confirmarExclusao) {
        AlertDialog(
            onDismissRequest = { confirmarExclusao = false },
            title = { Text("Excluir conta e dados?") },
            text = {
                Text(
                    "Vamos abrir uma solicitação para a equipe da lavanderia remover a sua conta e os seus " +
                        "dados pessoais. Você pode acompanhar o andamento em \"Suas solicitações\"."
                )
            },
            confirmButton = {
                TextButton(
                    enabled = !enviandoSolicitacao,
                    onClick = { onSolicitarExclusao { confirmarExclusao = false } }
                ) {
                    Text(
                        if (enviandoSolicitacao) "Enviando..." else "Solicitar exclusão",
                        color = Color(0xFFC62828),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmarExclusao = false }) { Text("Cancelar", color = textGrey) }
            }
        )
    }

    Scaffold(
        containerColor = backgroundColor,
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.Home, contentDescription = "Início") },
                    label = { Text("Início") },
                    selected = false,
                    onClick = { onNavegar(Rotas.HOME) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.LocalLaundryService, contentDescription = "Máquinas") },
                    label = { Text("Máquinas") },
                    selected = false,
                    onClick = { onNavegar(Rotas.MAQUINAS) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.ReceiptLong, contentDescription = "Pedidos") },
                    label = { Text("Pedidos") },
                    selected = false,
                    onClick = { onNavegar(Rotas.HISTORICO) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.Person, contentDescription = "Perfil") },
                    label = { Text("Perfil") },
                    selected = true,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = primaryPurple,
                        selectedTextColor = primaryPurple,
                        indicatorColor = lightPurpleCard
                    ),
                    onClick = { onNavegar(Rotas.PERFIL) }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Cabeçalho Topo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onVoltar,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .border(1.dp, strokeColor, RoundedCornerShape(12.dp))
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = textDark)
                }
                Text(
                    text = LocalLavanderia.current.tituloUnidade(),
                    color = textGrey,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Título
            Text(text = "Seu espaço na Lila", color = textDark, fontWeight = FontWeight.Bold, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Seus dados, suas preferências e ajuda sempre que precisar.", color = textGrey, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(24.dp))

            // Cartão de Perfil
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, strokeColor, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(lightPurpleCard),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = iniciais, color = primaryPurple, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(text = nome, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textDark)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = "Conta de cliente", fontSize = 12.sp, color = textGrey)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    PerfilInfoRow("E-mail", mascararEmail(email))
                    Spacer(modifier = Modifier.height(12.dp))
                    PerfilInfoRow("Telefone", mascararTelefone(telefone))

                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            onAbrirEdicao()
                            editando = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = lightPurpleCard, contentColor = primaryPurple),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Editar informações", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Por questões de segurança, a edição de alguns dados exige confirmação.",
                        fontSize = 11.sp, color = textGrey, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text(text = "Privacidade e preferências", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textDark)
            Spacer(modifier = Modifier.height(16.dp))

            // Lista de Preferências
            Column(modifier = Modifier.background(Color.White, RoundedCornerShape(16.dp)).border(1.dp, strokeColor, RoundedCornerShape(16.dp))) {
                MenuListItem(Icons.Outlined.Notifications, "Permissões e notificações", "Como nos comunicamos com você e alertas pelo app.") {
                    onAbrirNotificacoes()
                    mostrarNotificacoes = true
                }
                HorizontalDivider(color = strokeColor)
                MenuListItem(Icons.Outlined.Policy, "Políticas e termos", "Acesse a Política de Privacidade e os Termos de Uso.") {
                    mostrarTermos = true
                }
                HorizontalDivider(color = strokeColor)
                MenuListItem(Icons.Outlined.DeleteOutline, "Excluir conta e dados", "Seu direito ao esquecimento e remoção de dados.") {
                    confirmarExclusao = true
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text(text = "Dúvidas frequentes", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textDark)
            Spacer(modifier = Modifier.height(16.dp))

            // FAQs
            FAQItem("Posso usar meu próprio sabão / amaciante?", "Não é necessário. Nossas máquinas já dosam sabão e amaciante profissionais automaticamente na quantidade ideal para sua roupa, garantindo o melhor resultado e cuidando da máquina.")
            Spacer(modifier = Modifier.height(12.dp))
            FAQItem("Posso lavar tênis ou calçados?", "Infelizmente não. Para manter a higiene para todos os clientes e não danificar os tambores, não é permitido lavar nenhum tipo de calçado nas máquinas.")
            Spacer(modifier = Modifier.height(12.dp))
            FAQItem("Posso lavar edredom ou cobertor?", "Sim, edredons até o tamanho Queen cabem perfeitamente nas máquinas de 12kg ou 17kg. Respeite sempre a linha de limite máximo no visor frontal.")

            Spacer(modifier = Modifier.height(32.dp))
            Text(text = "Ajuda de verdade, à distância", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textDark)
            Spacer(modifier = Modifier.height(16.dp))

            // Cartão Suporte
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, strokeColor, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(40.dp).background(lightPurpleCard, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.SupportAgent, contentDescription = null, tint = primaryPurple)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(text = "Precisa falar com a gente?", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = textDark)
                            Text(text = "Suporte online de segunda a sábado.", fontSize = 12.sp, color = textGrey)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            tipoSolicitacao = "Suporte"
                            motivoSelecionado = null
                            escopo.launch { scrollState.animateScrollTo(posicaoFormulario) }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryPurple),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Falar com suporte", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Tempo médio de resposta: 10 minutos", fontSize = 11.sp, color = textGrey, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Abrir uma solicitação",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = textDark,
                modifier = Modifier.onGloballyPositioned { posicaoFormulario = it.positionInParent().y.toInt() }
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Registre aqui problemas técnicos com máquinas ou solicite análise de estorno.", color = textGrey, fontSize = 13.sp, lineHeight = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))

            val reembolso = tipoSolicitacao == "Reembolso"
            val motivos = if (reembolso) motivosReembolso else motivosSuporte

            // Tab Suporte / Reembolso
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(24.dp))
                    .padding(4.dp)
            ) {
                SegmentedButton(
                    text = "Suporte",
                    icon = Icons.Outlined.HeadsetMic,
                    isSelected = tipoSolicitacao == "Suporte",
                    onClick = {
                        tipoSolicitacao = "Suporte"
                        motivoSelecionado = null
                    },
                    modifier = Modifier.weight(1f)
                )
                SegmentedButton(
                    text = "Reembolso / Estorno",
                    icon = Icons.Outlined.AttachMoney,
                    isSelected = tipoSolicitacao == "Reembolso",
                    onClick = {
                        tipoSolicitacao = "Reembolso"
                        motivoSelecionado = null
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Pedido relacionado (obrigatório no reembolso)
            Text(
                text = if (reembolso) "Pedido relacionado *" else "Pedido relacionado (opcional)",
                fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = textDark
            )
            Spacer(modifier = Modifier.height(8.dp))
            val rotulosPedidos = pedidos.map { "${codigoPedido(it.id)} - ${it.maquina}" }
            val deslocamento = if (reembolso) 0 else 1
            CampoSelecao(
                valor = pedidoSelecionado?.let { "${codigoPedido(it.id)} - ${it.maquina}" }.orEmpty(),
                placeholder = if (reembolso && pedidos.isEmpty()) "Você ainda não tem pedidos" else "Selecione um pedido",
                opcoes = (if (reembolso) emptyList() else listOf("Sem pedido relacionado")) + rotulosPedidos,
                onSelecionar = { indice ->
                    pedidoSelecionado = if (indice < deslocamento) null else pedidos[indice - deslocamento]
                },
                icone = Icons.Outlined.Receipt,
                erro = if (tentouEnviar && reembolso && pedidoSelecionado == null) "Escolha o pedido do reembolso." else null
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Motivo *", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = textDark)
            Spacer(modifier = Modifier.height(8.dp))
            CampoSelecao(
                valor = motivoSelecionado.orEmpty(),
                placeholder = "Selecione o motivo",
                opcoes = motivos,
                onSelecionar = { indice -> motivoSelecionado = motivos[indice] },
                icone = Icons.Outlined.Info,
                erro = if (tentouEnviar && motivoSelecionado == null) "Escolha um motivo." else null
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "O que aconteceu? *", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = textDark)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = descricaoSolicitacao,
                onValueChange = { if (it.length <= 500) descricaoSolicitacao = it },
                isError = tentouEnviar && descricaoSolicitacao.isBlank(),
                modifier = Modifier.fillMaxWidth().height(120.dp),
                shape = RoundedCornerShape(12.dp),
                placeholder = { Text("Descreva o problema com detalhes...") }
            )
            Text(text = "${descricaoSolicitacao.length}/500 caracteres", fontSize = 11.sp, color = textGrey, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth().padding(top = 4.dp))

            Spacer(modifier = Modifier.height(16.dp))

            if (reembolso) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Icon(Icons.Outlined.Info, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = "Análise pré-estorno", color = Color(0xFFE65100), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "A solicitação será avaliada pela equipe antes de qualquer estorno.",
                                color = Color(0xFFE65100), fontSize = 12.sp, lineHeight = 16.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (erroSolicitacao != null) {
                Text(text = erroSolicitacao, color = Color(0xFFC62828), fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp))
            }

            Button(
                onClick = {
                    tentouEnviar = true
                    val motivo = motivoSelecionado
                    val faltaPedido = reembolso && pedidoSelecionado == null
                    if (!faltaPedido && motivo != null && descricaoSolicitacao.isNotBlank()) {
                        val tema = listOfNotNull(tipoSolicitacao, pedidoSelecionado?.let { codigoPedido(it.id) }, motivo)
                            .joinToString(" • ")
                        onEnviarSolicitacao(tema, descricaoSolicitacao) {
                            descricaoSolicitacao = ""
                            pedidoSelecionado = null
                            motivoSelecionado = null
                            tentouEnviar = false
                        }
                    }
                },
                enabled = !enviandoSolicitacao,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryPurple),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Outlined.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (enviandoSolicitacao) "Enviando..." else "Enviar solicitação", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text(text = "Suas solicitações", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textDark)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Acompanhe o status dos seus pedidos de ajuda ou reembolso de valores.", color = textGrey, fontSize = 13.sp, lineHeight = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))

            // Lista de Solicitações
            if (solicitacoes.isEmpty()) {
                Text(text = "Você ainda não abriu nenhuma solicitação.", color = textGrey, fontSize = 13.sp)
            }
            solicitacoes.forEach { solicitacao ->
                val visual = visualSolicitacao(solicitacao.estado)
                SolicitacaoCard(
                    titulo = solicitacao.tema?.takeIf { it.isNotBlank() } ?: "Solicitação #${solicitacao.id}",
                    data = dataApi(solicitacao.criadoEm)?.let { dataHistorico(it) }.orEmpty(),
                    estado = visual.texto,
                    corFundo = visual.fundo,
                    corTexto = visual.cor,
                    icone = visual.icone,
                    descricao = solicitacao.descricao
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botão Sair
            TextButton(
                onClick = onSair,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = textDark, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sair da conta", color = textDark, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Lila Lavanderia · Versão 1.0.0", color = textGrey, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

// Componentes Auxiliares Específicos
@Composable
fun PerfilInfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, color = Color(0xFF757575), fontSize = 13.sp)
        Text(text = value, color = Color(0xFF212121), fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun MenuListItem(icone: ImageVector, titulo: String, descricao: String, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(icone, contentDescription = null, tint = Color(0xFF757575), modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = titulo, color = Color(0xFF212121), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = descricao, color = Color(0xFF757575), fontSize = 12.sp, lineHeight = 16.sp)
            }
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color(0xFFBDBDBD))
    }
}

@Composable
fun FAQItem(pergunta: String, resposta: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = pergunta, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF212121))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = resposta, fontSize = 13.sp, color = Color(0xFF757575), lineHeight = 18.sp)
        }
    }
}

@Composable
fun SegmentedButton(text: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val bgColor = if (isSelected) Color.White else Color.Transparent
    val contentColor = if (isSelected) Color(0xFF673AB7) else Color(0xFF757575)

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = text, color = contentColor, fontSize = 13.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium)
        }
    }
}

@Composable
fun SolicitacaoCard(titulo: String, data: String, estado: String, corFundo: Color, corTexto: Color, icone: ImageVector, descricao: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = titulo, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF212121))
                Text(text = data, fontSize = 11.sp, color = Color(0xFF757575))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.background(corFundo, RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icone, contentDescription = null, tint = corTexto, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = estado, color = corTexto, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = descricao, fontSize = 12.sp, color = Color(0xFF757575), lineHeight = 16.sp)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PerfilScreenPreview() {
    PerfilScreen()
}

private val motivosSuporte = listOf(
    "Problema com a máquina",
    "Dúvida sobre pagamento",
    "Dúvida sobre o aplicativo",
    "Outro assunto"
)

private val motivosReembolso = listOf(
    "Não conclusão do serviço",
    "Cobrança em duplicidade",
    "Máquina com defeito",
    "Outro motivo"
)

private data class VisualSolicitacao(
    val texto: String,
    val fundo: Color,
    val cor: Color,
    val icone: ImageVector
)

/** Traduz o estado do chamado na API para o texto e as cores do cartão. */
private fun visualSolicitacao(estado: String): VisualSolicitacao = when (estado) {
    "aberto" -> VisualSolicitacao("Pendente", Color(0xFFFFF3E0), Color(0xFFE65100), Icons.Outlined.Schedule)
    "em_atendimento" -> VisualSolicitacao("Em atendimento", Color(0xFFE3F2FD), Color(0xFF1565C0), Icons.Outlined.SupportAgent)
    "resolvido" -> VisualSolicitacao("Resolvido", Color(0xFFE8F5E9), Color(0xFF2E7D32), Icons.Outlined.CheckCircle)
    else -> VisualSolicitacao("Cancelado", Color(0xFFFFEBEE), Color(0xFFC62828), Icons.Outlined.Cancel)
}

/** Campo somente leitura que abre uma lista de opções ao ser tocado. */
@Composable
private fun CampoSelecao(
    valor: String,
    placeholder: String,
    opcoes: List<String>,
    onSelecionar: (Int) -> Unit,
    icone: ImageVector,
    erro: String? = null
) {
    var aberto by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = valor,
            onValueChange = {},
            readOnly = true,
            isError = erro != null,
            placeholder = { Text(placeholder) },
            leadingIcon = { Icon(icone, contentDescription = null) },
            trailingIcon = { Icon(Icons.Outlined.KeyboardArrowDown, contentDescription = null) },
            supportingText = { if (erro != null) Text(erro) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        // Camada transparente que recebe o toque (campo somente leitura não repassa o clique)
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(enabled = opcoes.isNotEmpty()) { aberto = true }
        )
        DropdownMenu(expanded = aberto, onDismissRequest = { aberto = false }) {
            opcoes.forEachIndexed { indice, texto ->
                DropdownMenuItem(
                    text = { Text(texto) },
                    onClick = {
                        aberto = false
                        onSelecionar(indice)
                    }
                )
            }
        }
    }
}

@Composable
private fun NotificacoesDialog(
    notificacoes: List<NotificacaoResponse>,
    onLer: (NotificacaoResponse) -> Unit,
    onFechar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onFechar,
        title = { Text("Notificações") },
        text = {
            if (notificacoes.isEmpty()) {
                Text("Você não tem notificações.")
            } else {
                LazyColumn(modifier = Modifier.heightIn(max = 360.dp)) {
                    items(notificacoes, key = { it.id }) { notificacao ->
                        val naoLida = notificacao.lidoEm == null && notificacao.usuarioId != null
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onLer(notificacao) }
                                .padding(vertical = 10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (naoLida) {
                                    Box(modifier = Modifier.size(8.dp).background(Color(0xFF673AB7), CircleShape))
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                                Text(text = notificacao.titulo, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF212121))
                            }
                            Text(text = notificacao.mensagem, fontSize = 12.sp, color = Color(0xFF757575), lineHeight = 16.sp)
                            val data = dataApi(notificacao.criadoEm)
                            if (data != null) {
                                Text(text = dataHistorico(data), fontSize = 11.sp, color = Color(0xFF9E9E9E))
                            }
                        }
                        HorizontalDivider(color = Color(0xFFEEEEEE))
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onFechar) { Text("Fechar", color = Color(0xFF673AB7)) } }
    )
}

@Composable
private fun TermosDialog(onFechar: () -> Unit) {
    AlertDialog(
        onDismissRequest = onFechar,
        title = { Text("Políticas e termos") },
        text = {
            Column(modifier = Modifier.heightIn(max = 380.dp).verticalScroll(rememberScrollState())) {
                TrechoTermo("Política de Privacidade", "")
                TrechoTermo(
                    "Dados que coletamos",
                    "Nome, e-mail e telefone informados no cadastro, além do histórico de pedidos e pagamentos feitos no app."
                )
                TrechoTermo(
                    "Para que usamos",
                    "Para liberar as máquinas, registrar pagamentos, avisar sobre o andamento dos ciclos e atender pedidos de suporte. Não vendemos os seus dados."
                )
                TrechoTermo(
                    "Segurança",
                    "A sua senha é guardada de forma criptografada e o acesso ao app usa um token temporário."
                )
                TrechoTermo(
                    "Seus direitos",
                    "Você pode corrigir os seus dados em \"Editar informações\" e pedir a exclusão da conta e dos dados em \"Excluir conta e dados\"."
                )
                Spacer(modifier = Modifier.height(8.dp))
                TrechoTermo("Termos de Uso", "")
                TrechoTermo(
                    "Uso das máquinas",
                    "O ciclo só começa depois da confirmação do pagamento. Respeite o limite de carga indicado em cada máquina e não lave calçados."
                )
                TrechoTermo(
                    "Estornos",
                    "Pedidos de reembolso são analisados pela equipe a partir da solicitação aberta no app."
                )
            }
        },
        confirmButton = { TextButton(onClick = onFechar) { Text("Entendi", color = Color(0xFF673AB7)) } }
    )
}

@Composable
private fun TrechoTermo(titulo: String, texto: String) {
    Text(text = titulo, fontWeight = FontWeight.Bold, fontSize = if (texto.isEmpty()) 15.sp else 13.sp, color = Color(0xFF212121))
    if (texto.isNotEmpty()) {
        Text(text = texto, fontSize = 13.sp, color = Color(0xFF616161), lineHeight = 18.sp)
        Spacer(modifier = Modifier.height(10.dp))
    }
}
