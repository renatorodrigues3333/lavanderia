package com.equipe.lavemais

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HistoricoScreen(
    pedidos: List<UtilizacaoApi> = pedidosExemplo,
    onNavegar: (String) -> Unit = {},
    onVoltar: () -> Unit = {}
) {
    var filtroSelecionado by remember { mutableStateOf("Todos") }

    val pedidosFiltrados = pedidos.filter { pedido ->
        when (filtroSelecionado) {
            "Em andamento" -> categoriaPedido(pedido.estado) == CategoriaPedido.EM_ANDAMENTO
            "Concluídos" -> categoriaPedido(pedido.estado) == CategoriaPedido.CONCLUIDO
            "Não pagos" -> categoriaPedido(pedido.estado) == CategoriaPedido.NAO_PAGO
            else -> true
        }
    }
    // Agrupa por mês, mantendo a ordem (mais recentes primeiro)
    val porMes = pedidosFiltrados.groupBy { pedido -> dataApi(pedido.criado_em)?.let { mesAno(it) } ?: "Sem data" }

    // Cores base
    val backgroundColor = Color(0xFFFAFAFA)
    val primaryPurple = Color(0xFF673AB7)
    val textDark = Color(0xFF212121)
    val textGrey = Color(0xFF757575)
    val lightPurpleCard = Color(0xFFF3E5F5)

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
                    selected = true,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = primaryPurple,
                        selectedTextColor = primaryPurple,
                        indicatorColor = lightPurpleCard
                    ),
                    onClick = { onNavegar(Rotas.HISTORICO) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.Person, contentDescription = "Perfil") },
                    label = { Text("Perfil") },
                    selected = false,
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
                .verticalScroll(rememberScrollState())
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
                        .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(12.dp))
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = textDark)
                }
                Text(
                    text = "Unidade Jardim Aurora",
                    color = textGrey,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Título
            Text(text = "Pedidos e histórico", color = textDark, fontWeight = FontWeight.Bold, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Seus ciclos, pagamentos e comprovantes, organizados em um só lugar.",
                color = textGrey, fontSize = 14.sp, lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Área de Filtros
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.FilterList, contentDescription = null, tint = textDark, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Filtrar pedidos", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = textDark)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Período", fontSize = 12.sp, color = textGrey)
                    Spacer(modifier = Modifier.height(4.dp))

                    // Campo de Data
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "01 a 31 de outubro de 2026", fontSize = 13.sp, color = textDark)
                        Icon(Icons.Outlined.CalendarToday, contentDescription = null, tint = primaryPurple, modifier = Modifier.size(18.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Status", fontSize = 12.sp, color = textGrey)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Chips de Status
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FiltroChip("Todos", selecionado = filtroSelecionado == "Todos") { filtroSelecionado = "Todos" }
                        FiltroChip("Em andamento", selecionado = filtroSelecionado == "Em andamento") { filtroSelecionado = "Em andamento" }
                        FiltroChip("Concluídos", selecionado = filtroSelecionado == "Concluídos") { filtroSelecionado = "Concluídos" }
                        FiltroChip("Não pagos", selecionado = filtroSelecionado == "Não pagos") { filtroSelecionado = "Não pagos" }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Banner Informativo
            Card(
                colors = CardDefaults.cardColors(containerColor = lightPurpleCard),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Icon(Icons.Outlined.Sync, contentDescription = null, tint = primaryPurple, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "Um novo serviço é um novo pedido", color = primaryPurple, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Usar como referência não reserva nem inicia uma máquina. O preço e a disponibilidade serão verificados novamente antes de confirmar.",
                            color = primaryPurple, fontSize = 12.sp, lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (porMes.isEmpty()) {
                Text(text = "Nenhum pedido", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textDark)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "0 registros", fontSize = 12.sp, color = textGrey)
                Spacer(modifier = Modifier.height(16.dp))
            }

            porMes.forEach { (mes, lista) ->
                // Cabeçalho do Mês
                Text(text = mes, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textDark)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = if (lista.size == 1) "1 registro" else "${lista.size} registros", fontSize = 12.sp, color = textGrey)

                Spacer(modifier = Modifier.height(16.dp))

                // Lista de Pedidos
                lista.forEach { pedido -> PedidoCardApi(pedido) }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Rodapé
            Text(
                text = "Pagamentos recusados aparecem como tentativas, nunca como valores pagos.",
                color = textGrey, fontSize = 10.sp, textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                lineHeight = 14.sp
            )
        }
    }
}

// Modelos e Componentes Auxiliares
enum class CategoriaPedido { EM_ANDAMENTO, CONCLUIDO, NAO_PAGO }

fun categoriaPedido(estado: String): CategoriaPedido {
    val e = estado.lowercase()
    return when {
        "conclu" in e || "finaliz" in e -> CategoriaPedido.CONCLUIDO
        "cancel" in e || "recus" in e || "falh" in e -> CategoriaPedido.NAO_PAGO
        else -> CategoriaPedido.EM_ANDAMENTO
    }
}

/** Monta o cartão do histórico a partir de um pedido da API. */
@Composable
fun PedidoCardApi(pedido: UtilizacaoApi) {
    val categoria = categoriaPedido(pedido.estado)
    val aguardando = "pend" in pedido.estado.lowercase() || "aguard" in pedido.estado.lowercase()

    val tag = when {
        categoria == CategoriaPedido.CONCLUIDO ->
            TagData("Serviço finalizado", Color(0xFF2E7D32), Color(0xFFE8F5E9), Icons.Outlined.CheckCircle)
        categoria == CategoriaPedido.NAO_PAGO ->
            TagData(estadoTexto(pedido.estado), Color(0xFFC62828), Color(0xFFFFEBEE), Icons.Outlined.Cancel)
        aguardando ->
            TagData(estadoTexto(pedido.estado), Color(0xFFE65100), Color(0xFFFFF3E0), Icons.Outlined.HourglassEmpty)
        else ->
            TagData(estadoTexto(pedido.estado), Color(0xFF673AB7), Color(0xFFF3E5F5), Icons.Outlined.Sync)
    }
    val tags = if (categoria == CategoriaPedido.NAO_PAGO) {
        listOf(TagData("Não iniciado", Color(0xFF757575), Color(0xFFF5F5F5), Icons.Outlined.NotInterested), tag)
    } else {
        listOf(tag)
    }

    PedidoCard(
        data = dataApi(pedido.criado_em)?.let { dataHistorico(it) } ?: "",
        id = codigoPedido(pedido.id),
        maquina = pedido.maquina,
        valor = reais(pedido.valor_total),
        subtituloValor = if (categoria == CategoriaPedido.NAO_PAGO) "não pago" else "valor do pedido",
        servico = "${pedido.servico} - ${pedido.duracao_estimada} min",
        tags = tags,
        descricao = if (categoria == CategoriaPedido.NAO_PAGO) "Nenhum ciclo foi iniciado para este pedido." else null,
        textoBotaoPrincipal = if (categoria == CategoriaPedido.NAO_PAGO) "Ver detalhe da tentativa" else "Ver comprovante",
        iconeBotaoPrincipal = if (categoria == CategoriaPedido.NAO_PAGO) Icons.Outlined.Info else Icons.Outlined.ReceiptLong,
        isError = categoria == CategoriaPedido.NAO_PAGO
    )
}

data class TagData(val texto: String, val corTexto: Color, val corFundo: Color, val icone: androidx.compose.ui.graphics.vector.ImageVector)

@Composable
fun FiltroChip(texto: String, selecionado: Boolean, onClick: () -> Unit) {
    val containerColor = if (selecionado) Color(0xFFF3E5F5) else Color(0xFFF5F5F5)
    val contentColor = if (selecionado) Color(0xFF673AB7) else Color(0xFF757575)

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
        modifier = Modifier.height(32.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(text = texto, color = contentColor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun PedidoCard(
    data: String, id: String, maquina: String, valor: String, subtituloValor: String, servico: String,
    tags: List<TagData>, descricao: String? = null, textoBotaoPrincipal: String, iconeBotaoPrincipal: androidx.compose.ui.graphics.vector.ImageVector,
    isError: Boolean = false
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Data e ID
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = data, fontSize = 11.sp, color = Color(0xFF757575))
                Text(text = id, fontSize = 11.sp, color = Color(0xFF757575))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Máquina e Valor
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column {
                    Text(text = maquina, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF212121))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = servico, fontSize = 12.sp, color = Color(0xFF757575))
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = valor, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = if (isError) Color(0xFFC62828) else Color(0xFF212121))
                    Text(text = subtituloValor, fontSize = 10.sp, color = if (isError) Color(0xFFC62828) else Color(0xFF757575))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tags
            Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                tags.forEach { tag ->
                    Row(
                        modifier = Modifier
                            .background(tag.corFundo, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(tag.icone, contentDescription = null, tint = tag.corTexto, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = tag.texto, color = tag.corTexto, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            if (descricao != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = descricao, fontSize = 11.sp, color = if (isError) Color(0xFFC62828) else Color(0xFF757575), lineHeight = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botões
            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth().height(40.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3E5F5), contentColor = Color(0xFF673AB7)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(iconeBotaoPrincipal, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(textoBotaoPrincipal, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = { },
                modifier = Modifier.fillMaxWidth().height(40.dp)
            ) {
                Text("Usar como referência para novo serviço", color = Color(0xFF673AB7), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HistoricoScreenPreview() {
    HistoricoScreen()
}
