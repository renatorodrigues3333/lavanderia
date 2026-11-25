package com.equipe.lavemais

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AcompanhamentoScreen(
    codigo: String = "LLA-1048",
    maquina: String = "Máquina 01",
    servico: String = "Lavagem e secagem",
    estado: String = "Em funcionamento",
    valor: Double = 32.0,
    duracaoMin: Int = 70,
    decorridoMin: Int = 28,
    terminoPrevisto: String = "Hoje, às 10:23",
    onNavegar: (String) -> Unit = {},
    onVoltar: () -> Unit = {},
    /** Pedido completo, usado no comprovante. */
    pedido: UtilizacaoApi? = null,
    /** Forma de pagamento escolhida, ex.: "Cartão de crédito Visa •••• 4242". */
    formaPagamento: String? = null,
    /** Estado do pedido na API: "pendente", "em_andamento", "concluida" ou "cancelada". */
    estadoApi: String = "em_andamento",
    /** true se a última consulta ao servidor falhou. */
    falhaAtualizacao: Boolean = false,
    onAtualizar: () -> Unit = {}
) {
    var mostrarComprovante by remember { mutableStateOf(false) }
    // Cores base
    val backgroundColor = Color(0xFFFAFAFA)
    val primaryPurple = Color(0xFF673AB7)
    val textDark = Color(0xFF212121)
    val textGrey = Color(0xFF757575)
    val lightPurpleCard = Color(0xFFF3E5F5)

    // Cores de status
    val warningBg = Color(0xFFFFF3E0)
    val warningText = Color(0xFFE65100)
    val successBg = Color(0xFFE8F5E9)
    val successText = Color(0xFF2E7D32)
    val errorBg = Color(0xFFFFEBEE)
    val errorText = Color(0xFFC62828)

    // Situação real do pedido
    val emAndamento = estadoApi == "em_andamento"
    val concluida = estadoApi == "concluida"
    val cancelada = estadoApi == "cancelada"
    val pendente = !emAndamento && !concluida && !cancelada

    val decorrido = if (concluida) duracaoMin else decorridoMin.coerceIn(0, maxOf(duracaoMin, 0))
    val restante = when {
        concluida -> 0
        emAndamento -> maxOf(duracaoMin - decorrido, 0)
        else -> duracaoMin
    }
    val progresso = when {
        concluida -> 1f
        emAndamento && duracaoMin > 0 -> decorrido.toFloat() / duracaoMin
        else -> 0f
    }
    val porcentagem = (progresso * 100).toInt()
    val etapas = etapasDoCiclo(servico, progresso, emAndamento, concluida)
    val etapaAtual = etapas.firstOrNull { it.situacao == SituacaoEtapa.EM_ANDAMENTO }?.nome
    val textoAgora = when {
        concluida -> "Ciclo concluído - suas roupas estão prontas"
        cancelada -> "Pedido cancelado"
        pendente -> "Aguardando o início do ciclo"
        restante == 0 -> "Finalizando o ciclo - $decorrido min decorridos"
        etapaAtual == "Lavagem" -> "Lavando agora - $decorrido min decorridos"
        etapaAtual == "Secagem" -> "Secando agora - $decorrido min decorridos"
        else -> "Ciclo em andamento - $decorrido min decorridos"
    }

    val corEstado = when { concluida -> successText; cancelada -> errorText; pendente -> warningText; else -> primaryPurple }
    val fundoEstado = when { concluida -> successBg; cancelada -> errorBg; pendente -> warningBg; else -> lightPurpleCard }
    val iconeEstado = when {
        concluida -> Icons.Outlined.CheckCircle
        cancelada -> Icons.Outlined.Cancel
        pendente -> Icons.Outlined.HourglassEmpty
        else -> Icons.Outlined.Sync
    }
    val aviso = when {
        concluida -> AvisoCiclo(successBg, successText, Icons.Outlined.CheckCircle, "Suas roupas estão prontas",
            "Confira o tambor e leve todos os seus pertences. O comprovante permanece no histórico.")
        cancelada -> AvisoCiclo(errorBg, errorText, Icons.Outlined.Cancel, "Pedido cancelado",
            "Este pedido foi cancelado e nenhum ciclo foi iniciado. Se houve cobrança, abra uma solicitação de reembolso no Perfil.")
        pendente -> AvisoCiclo(warningBg, warningText, Icons.Outlined.HourglassEmpty, "Aguardando início",
            "A máquina ainda não confirmou o início do ciclo, então ele não está confirmado como ativo. Aguarde e consulte o status antes de enviar uma nova solicitação.")
        else -> AvisoCiclo(successBg, successText, Icons.Outlined.NotificationsActive, "Tudo certo por aqui",
            "O ciclo está em andamento. Ao concluir, um aviso indicará que suas roupas podem ser retiradas. O tempo é estimado.")
    }

    if (mostrarComprovante && pedido != null) {
        DetalhePedidoDialog(
            pedido = pedido,
            formaPagamento = formaPagamento,
            onFechar = { mostrarComprovante = false }
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
                    text = LocalLavanderia.current.tituloUnidade(),
                    color = textGrey,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Título Principal
            Text(
                text = when {
                    concluida -> "Seu ciclo\nfoi concluído."
                    cancelada -> "Este pedido\nfoi cancelado."
                    pendente -> "Aguardando o\ninício do ciclo."
                    else -> "Seu ciclo está\nem boas mãos."
                },
                color = textDark,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                lineHeight = 30.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Pedido $codigo - $maquina",
                color = textGrey,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Cartão Principal de Monitorização
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    // Status Badge e Máquina
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier
                                .background(fundoEstado, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(iconeEstado, contentDescription = null, tint = corEstado, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = estado, color = corEstado, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Text(text = maquina, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = textDark)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Gráfico Circular de Progresso
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(160.dp)) {
                        CircularProgressIndicator(
                            progress = { 1f },
                            modifier = Modifier.fillMaxSize(),
                            color = Color(0xFFF5F5F5),
                            strokeWidth = 12.dp,
                        )
                        CircularProgressIndicator(
                            progress = { progresso },
                            modifier = Modifier.fillMaxSize(),
                            color = if (concluida) successText else primaryPurple,
                            strokeWidth = 12.dp,
                            strokeCap = StrokeCap.Round
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "$restante min", fontWeight = FontWeight.Bold, fontSize = 32.sp, color = textDark)
                            Text(text = "restante de $duracaoMin min", fontSize = 11.sp, color = textGrey)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "$porcentagem% concluído", fontSize = 12.sp, color = primaryPurple, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Text(text = textoAgora, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = textDark)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Barra de etapas (conforme o serviço contratado)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        etapas.forEach { etapa ->
                            val ativa = etapa.situacao != SituacaoEtapa.PROXIMA
                            Column(modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .background(if (ativa) primaryPurple else Color(0xFFEEEEEE), RoundedCornerShape(2.dp))
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "${etapa.nome} - " + when (etapa.situacao) {
                                        SituacaoEtapa.CONCLUIDA -> "concluída"
                                        SituacaoEtapa.EM_ANDAMENTO -> "em andamento"
                                        SituacaoEtapa.PROXIMA -> when {
                                            cancelada -> "não iniciada"
                                            pendente -> "aguardando"
                                            else -> "próxima etapa"
                                        }
                                    },
                                    fontSize = 10.sp,
                                    color = if (ativa) primaryPurple else textGrey,
                                    fontWeight = if (ativa) FontWeight.SemiBold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Confirmações (conforme a situação real do pedido)
                    when {
                        cancelada -> CheckmarkItem(
                            "Pedido cancelado", "Nenhum ciclo foi iniciado para este pedido.", concluido = false
                        )
                        pendente -> {
                            CheckmarkItem("Pagamento", "Aguardando a confirmação do pagamento.", concluido = false)
                            Spacer(modifier = Modifier.height(12.dp))
                            CheckmarkItem("Início do ciclo", "A máquina ainda não confirmou o início.", concluido = false)
                        }
                        else -> {
                            CheckmarkItem("Pagamento confirmado", "${reais(valor)} recebidos.")
                            Spacer(modifier = Modifier.height(12.dp))
                            CheckmarkItem(
                                if (concluida) "Ciclo concluído" else "Comando de início confirmado pela máquina",
                                if (concluida) "A máquina finalizou o ciclo. O comprovante fica no histórico."
                                else "A máquina confirmou o início. Só após essa confirmação o ciclo ficou ativo."
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Detalhes do Serviço
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    DetalheRowAcompanhamento("Serviço contratado", servico)
                    Spacer(modifier = Modifier.height(12.dp))
                    DetalheRowAcompanhamento("Valor", reais(valor))
                    Spacer(modifier = Modifier.height(12.dp))
                    DetalheRowAcompanhamento("Duração estimada", "$duracaoMin min")
                    Spacer(modifier = Modifier.height(12.dp))
                    DetalheRowAcompanhamento(if (concluida) "Concluído em" else "Término previsto", terminoPrevisto)
                    if (!formaPagamento.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        DetalheRowAcompanhamento("Pagamento", formaPagamento)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Aviso conforme a situação real do pedido
            Card(
                colors = CardDefaults.cardColors(containerColor = aviso.fundo),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Icon(aviso.icone, contentDescription = null, tint = aviso.cor, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = aviso.titulo, color = aviso.cor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = aviso.mensagem, color = aviso.cor, fontSize = 12.sp, lineHeight = 16.sp)
                    }
                }
            }

            if (emAndamento || pendente) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onAtualizar,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = lightPurpleCard, contentColor = primaryPurple),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Outlined.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (pendente) "Verificar confirmação de início" else "Atualizar status",
                        fontWeight = FontWeight.SemiBold, fontSize = 13.sp
                    )
                }
            }

            if (cancelada) {
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = { onNavegar(Rotas.PERFIL) },
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Icon(Icons.Outlined.SupportAgent, contentDescription = null, tint = primaryPurple, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Falar com suporte", color = primaryPurple, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botão Ver Comprovante
            Button(
                onClick = { mostrarComprovante = true },
                enabled = pedido != null,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = lightPurpleCard, contentColor = primaryPurple),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Outlined.Receipt, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ver comprovante", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }

            // Falha real ao consultar o status (sem internet, servidor fora do ar...)
            if (falhaAtualizacao) {
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().border(1.dp, errorBg, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.background(errorBg, RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.WarningAmber, contentDescription = null, tint = errorText, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Falha ao atualizar", color = errorText, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "Não foi possível consultar o status do pedido.", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = errorText)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Os dados abaixo podem estar desatualizados. Confira a conexão com a internet e tente de novo. Se o problema continuar, fale com o suporte.",
                            color = textGrey, fontSize = 12.sp, lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onAtualizar,
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = lightPurpleCard, contentColor = primaryPurple),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Outlined.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Tentar novamente", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(
                            onClick = { onNavegar(Rotas.PERFIL) },
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Icon(Icons.Outlined.SupportAgent, contentDescription = null, tint = primaryPurple, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Falar com suporte", color = primaryPurple, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// Componentes Auxiliares
@Composable
fun CheckmarkItem(titulo: String, subtitulo: String, concluido: Boolean = true) {
    Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
        Icon(
            if (concluido) Icons.Outlined.CheckCircleOutline else Icons.Outlined.HourglassEmpty,
            contentDescription = null,
            tint = if (concluido) Color(0xFF2E7D32) else Color(0xFFE65100),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = titulo, color = Color(0xFF212121), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitulo, color = Color(0xFF757575), fontSize = 11.sp, lineHeight = 14.sp)
        }
    }
}

@Composable
fun DetalheRowAcompanhamento(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(text = label, color = Color(0xFF757575), fontSize = 13.sp)
        Text(
            text = value,
            color = Color(0xFF212121),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.End
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AcompanhamentoScreenPreview() {
    AcompanhamentoScreen()
}

private enum class SituacaoEtapa { PROXIMA, EM_ANDAMENTO, CONCLUIDA }

private data class EtapaCiclo(val nome: String, val situacao: SituacaoEtapa)

private data class AvisoCiclo(
    val fundo: Color,
    val cor: Color,
    val icone: androidx.compose.ui.graphics.vector.ImageVector,
    val titulo: String,
    val mensagem: String
)

/**
 * Etapas do ciclo conforme o nome do serviço ("Lavagem", "Secagem" ou os dois) e o progresso:
 * cada etapa ocupa uma parte igual do tempo total.
 */
private fun etapasDoCiclo(servico: String, progresso: Float, emAndamento: Boolean, concluida: Boolean): List<EtapaCiclo> {
    val lava = servico.contains("lavagem", ignoreCase = true)
    val seca = servico.contains("secagem", ignoreCase = true)
    val nomes = when {
        lava && seca -> listOf("Lavagem", "Secagem")
        seca -> listOf("Secagem")
        lava -> listOf("Lavagem")
        else -> listOf("Ciclo")
    }
    return nomes.mapIndexed { i, nome ->
        val inicio = i.toFloat() / nomes.size
        val fim = (i + 1).toFloat() / nomes.size
        val situacao = when {
            concluida -> SituacaoEtapa.CONCLUIDA
            !emAndamento -> SituacaoEtapa.PROXIMA
            progresso >= fim -> SituacaoEtapa.CONCLUIDA
            progresso >= inicio -> SituacaoEtapa.EM_ANDAMENTO
            else -> SituacaoEtapa.PROXIMA
        }
        EtapaCiclo(nome, situacao)
    }
}
