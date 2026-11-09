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
    onVoltar: () -> Unit = {}
) {
    val decorrido = decorridoMin.coerceIn(0, maxOf(duracaoMin, 0))
    val restante = maxOf(duracaoMin - decorrido, 0)
    val progresso = if (duracaoMin > 0) decorrido.toFloat() / duracaoMin else 0f
    val porcentagem = (progresso * 100).toInt()
    val textoAgora = when {
        restante == 0 -> "Ciclo concluído - $decorrido min decorridos"
        servico.startsWith("secagem", ignoreCase = true) -> "Secando agora - $decorrido min decorridos"
        else -> "Lavando agora - $decorrido min decorridos"
    }

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

            // Título Principal
            Text(
                text = "Seu ciclo está\nem boas mãos.",
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
                                .background(lightPurpleCard, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.Sync, contentDescription = null, tint = primaryPurple, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = estado, color = primaryPurple, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
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
                            color = primaryPurple,
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

                    // Barra de Etapas (Lavagem / Secagem)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(modifier = Modifier.weight(1f)) {
                            Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(primaryPurple, RoundedCornerShape(2.dp)))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Lavagem - em andamento", fontSize = 10.sp, color = primaryPurple, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(Color(0xFFEEEEEE), RoundedCornerShape(2.dp)))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Secagem - próxima etapa", fontSize = 10.sp, color = textGrey)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Checks de Confirmação
                    CheckmarkItem("Pagamento confirmado", "${reais(valor)} recebidos.")
                    Spacer(modifier = Modifier.height(12.dp))
                    CheckmarkItem(
                        "Comando de início confirmado pela máquina",
                        "A máquina confirmou o início. Só após essa confirmação o ciclo ficou ativo."
                    )
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
                    DetalheRowAcompanhamento("Insumos", "Sabão e amaciante\nincluídos")
                    Spacer(modifier = Modifier.height(12.dp))
                    DetalheRowAcompanhamento("Duração estimada", "$duracaoMin min")
                    Spacer(modifier = Modifier.height(12.dp))
                    DetalheRowAcompanhamento("Término previsto", terminoPrevisto)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Banner "Tudo certo por aqui"
            Card(
                colors = CardDefaults.cardColors(containerColor = successBg),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Icon(Icons.Outlined.NotificationsActive, contentDescription = null, tint = successText, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "Tudo certo por aqui", color = successText, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "O ciclo está em andamento. Ao concluir, um aviso visual indicará que suas roupas podem ser retiradas. O tempo é estimado.",
                            color = successText, fontSize = 12.sp, lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botão Ver Comprovante
            Button(
                onClick = { /* Ação Comprovante */ },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = lightPurpleCard, contentColor = primaryPurple),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Outlined.Receipt, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ver comprovante", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)
            Spacer(modifier = Modifier.height(24.dp))

            // SECÇÃO: OUTROS ESTADOS DEMONSTRAÇÃO
            Text(text = "OUTROS ESTADOS - DEMONSTRAÇÃO", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = textGrey)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Cenários independentes. Não representam o pedido atual.", fontSize = 11.sp, color = textGrey)

            Spacer(modifier = Modifier.height(16.dp))

            // Estado: Aguardando início
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.background(warningBg, RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.HourglassEmpty, contentDescription = null, tint = warningText, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Aguardando início", color = warningText, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Pagamento confirmado. Máquina pendente.", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = textDark)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "A confirmação do comando de início pela máquina ainda não chegou. O ciclo não está confirmado como ativo. Aguarde e consulte o status antes de enviar uma nova solicitação.",
                        color = textGrey, fontSize = 12.sp, lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = lightPurpleCard, contentColor = primaryPurple),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Verificar confirmação de início", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Estado: Serviço finalizado
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.background(successBg, RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = successText, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Serviço finalizado", color = successText, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Ciclo concluído · 100%", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = textDark)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Suas roupas estão prontas para retirada. Confira o tambor e leve todos os seus pertences. O comprovante permanece no histórico.",
                        color = textGrey, fontSize = 12.sp, lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Estado: Falha detectada
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
                        Text(text = "Falha detectada", color = errorText, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Comunicação perdida: Estado da máquina não confirmado.", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = errorText)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Não é possível confirmar se a máquina iniciou, está em funcionamento ou concluiu o ciclo. Este aviso não significa início confirmado.\n\nConfirme a conexão e consulte o status novamente. Se persistir, fale com o suporte. Não haverá revisão nem nova cobrança automática.",
                        color = textGrey, fontSize = 12.sp, lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = lightPurpleCard, contentColor = primaryPurple),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Outlined.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Verificar conexão e status", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = { },
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Icon(Icons.Outlined.SupportAgent, contentDescription = null, tint = primaryPurple, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Falar com suporte", color = primaryPurple, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// Componentes Auxiliares
@Composable
fun CheckmarkItem(titulo: String, subtitulo: String) {
    Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
        Icon(Icons.Outlined.CheckCircleOutline, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(20.dp))
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
