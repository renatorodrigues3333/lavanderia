package com.equipe.lavemais

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Enumeração para controlar os estados das máquinas
enum class MachineStatus(val texto: String, val corTexto: Color, val corFundo: Color, val icone: ImageVector) {
    LIVRE("Livre", Color(0xFF2E7D32), Color(0xFFE8F5E9), Icons.Outlined.CheckCircle),
    EM_USO("Em uso", Color(0xFFF57F17), Color(0xFFFFFDE7), Icons.Outlined.Schedule),
    FORA_SERVICO("Fora de serviço", Color(0xFFC62828), Color(0xFFFFEBEE), Icons.Outlined.Build)
}

@Composable
fun MaquinasScreen(
    maquinas: List<MaquinaApi> = maquinasExemplo,
    servicos: List<ServicoApi> = servicosExemplo,
    maquinaSelecionadaId: Int? = maquinas.firstOrNull { it.status() == MachineStatus.LIVRE }?.id,
    onSelecionar: (MaquinaApi) -> Unit = {},
    onNavegar: (String) -> Unit = {},
    onVoltar: () -> Unit = {},
    onContinuar: () -> Unit = {}
) {
    val maquinaSelecionada = maquinas.firstOrNull { it.id == maquinaSelecionadaId }?.identificacao ?: ""

    // Informações dos serviços, iguais para todas as máquinas
    val textoServicos = servicos.joinToString(" • ") { it.nome }
    val precoMinimo = servicos.minOfOrNull { it.preco }
    val tempoMin = servicos.minOfOrNull { it.duracao_estimada }
    val tempoMax = servicos.maxOfOrNull { it.duracao_estimada }
    val textoPreco = precoMinimo?.let { "A partir de ${reais(it)}" } ?: ""
    val textoTempo = when {
        tempoMin == null || tempoMax == null -> ""
        tempoMin == tempoMax -> "$tempoMin min"
        else -> "$tempoMin a $tempoMax min"
    }

    // Cores base
    val backgroundColor = Color(0xFFFAFAFA)
    val primaryPurple = Color(0xFF673AB7)
    val textDark = Color(0xFF212121)
    val textGrey = Color(0xFF757575)
    val lightPurpleCard = Color(0xFFF3E5F5)

    Scaffold(
        containerColor = backgroundColor,
        bottomBar = {
            Column {
                // Botão de Continuar Fixo
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)) {
                    Button(
                        onClick = onContinuar,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryPurple),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Continuar com $maquinaSelecionada", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Barra de Navegação Inferior
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
                        selected = true,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = primaryPurple,
                            selectedTextColor = primaryPurple,
                            indicatorColor = lightPurpleCard
                        ),
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
                        selected = false,
                        onClick = { onNavegar(Rotas.PERFIL) }
                    )
                }
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

            // Cabeçalho
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
            Text(text = "Escolha sua máquina", color = textDark, fontWeight = FontWeight.Bold, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Veja a capacidade e encontre o melhor ciclo para suas roupas.", color = textGrey, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(24.dp))

            // Stepper de Progresso
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StepItem("Máquina", isActive = true)
                StepItem("Serviço", isActive = false)
                StepItem("Resumo", isActive = false)
                StepItem("Pagamento", isActive = false)
            }
            Divider(color = Color(0xFFEEEEEE), thickness = 1.dp, modifier = Modifier.padding(top = 8.dp))

            Spacer(modifier = Modifier.height(16.dp))

            // Resumo de Status (Chips)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusChip("${maquinas.count { it.status() == MachineStatus.LIVRE }} Livre", MachineStatus.LIVRE)
                StatusChip("${maquinas.count { it.status() == MachineStatus.EM_USO }} Em uso", MachineStatus.EM_USO)
                StatusChip("${maquinas.count { it.status() == MachineStatus.FORA_SERVICO }} Fora de serviço", MachineStatus.FORA_SERVICO)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Banner Informativo
            Card(
                colors = CardDefaults.cardColors(containerColor = lightPurpleCard),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Icon(Icons.Outlined.Info, contentDescription = null, tint = primaryPurple, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "Escolha somente uma máquina livre", color = primaryPurple, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Máquinas em uso ou fora de serviço não aceitam novos pedidos e têm a seleção desabilitada.",
                            color = primaryPurple,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Lista de Máquinas
            maquinas.forEach { maquina ->
                val status = maquina.status()
                MachineCard(
                    nome = maquina.identificacao,
                    capacidade = kg(maquina.capacidade_kg),
                    status = status,
                    servicos = textoServicos,
                    preco = textoPreco,
                    tempo = textoTempo,
                    extraInfo = if (status == MachineStatus.FORA_SERVICO) {
                        maquina.motivo_bloqueio ?: "Em manutenção. Sem previsão de retorno."
                    } else null,
                    isSelected = maquina.id == maquinaSelecionadaId,
                    onSelect = { onSelecionar(maquina) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Preços fictícios. A duração varia conforme o serviço. A disponibilidade será validada novamente.",
                color = textGrey,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )
        }
    }
}

// Componentes Auxiliares
@Composable
fun StepItem(label: String, isActive: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            color = if (isActive) Color(0xFF673AB7) else Color(0xFF9E9E9E),
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
            fontSize = 13.sp
        )
        if (isActive) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(modifier = Modifier.height(2.dp).width(30.dp).background(Color(0xFF673AB7), RoundedCornerShape(2.dp)))
        }
    }
}

@Composable
fun StatusChip(text: String, status: MachineStatus) {
    Row(
        modifier = Modifier
            .background(status.corFundo, RoundedCornerShape(16.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(status.icone, contentDescription = null, tint = status.corTexto, modifier = Modifier.size(12.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, color = status.corTexto, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun MachineCard(
    nome: String,
    capacidade: String,
    status: MachineStatus,
    servicos: String,
    preco: String,
    tempo: String,
    extraInfo: String? = null,
    isSelected: Boolean = false,
    onSelect: () -> Unit = {}
) {
    val primaryPurple = Color(0xFF673AB7)
    val isAvailable = status == MachineStatus.LIVRE
    val borderColor = if (isSelected) primaryPurple else Color(0xFFEEEEEE)
    val borderWidth = if (isSelected) 2.dp else 1.dp

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .border(borderWidth, borderColor, RoundedCornerShape(16.dp))
            .clickable(enabled = isAvailable) { onSelect() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.LocalLaundryService, contentDescription = null, tint = primaryPurple)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = nome, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF212121))
                        Spacer(modifier = Modifier.height(4.dp))
                        StatusChip(text = status.texto, status = status)
                    }
                }
                Text(text = capacidade, fontSize = 12.sp, color = Color(0xFF757575))
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = servicos, fontSize = 13.sp, color = Color(0xFF212121), fontWeight = FontWeight.Medium)

            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = preco, fontSize = 12.sp, color = Color(0xFF757575))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Schedule, contentDescription = null, tint = Color(0xFF757575), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = tempo, fontSize = 12.sp, color = Color(0xFF757575))
                }
            }

            if (extraInfo != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = extraInfo, fontSize = 11.sp, color = Color(0xFF757575))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botão de seleção dinâmico
            if (isAvailable) {
                Button(
                    onClick = onSelect,
                    modifier = Modifier.fillMaxWidth().height(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) Color(0xFFF3E5F5) else Color.Transparent,
                        contentColor = primaryPurple
                    ),
                    shape = RoundedCornerShape(8.dp),
                    border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE)) else null
                ) {
                    if (isSelected) {
                        Icon(Icons.Outlined.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Selecionada para seu pedido", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    } else {
                        Text("Selecionar máquina", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            } else {
                Button(
                    onClick = { },
                    enabled = false,
                    modifier = Modifier.fillMaxWidth().height(40.dp),
                    colors = ButtonDefaults.buttonColors(disabledContainerColor = Color(0xFFF5F5F5), disabledContentColor = Color(0xFF9E9E9E)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Outlined.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Seleção indisponível", fontSize = 13.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MaquinasScreenPreview() {
    MaquinasScreen()
}
