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
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ServicoScreen(
    onNavegar: (String) -> Unit = {},
    onVoltar: () -> Unit = {},
    onContinuar: () -> Unit = {}
) {
    var servicoSelecionado by remember { mutableStateOf("Lavagem e secagem") }

    // Cores base
    val backgroundColor = Color(0xFFFAFAFA)
    val primaryPurple = Color(0xFF673AB7)
    val textDark = Color(0xFF212121)
    val textGrey = Color(0xFF757575)
    val lightPurpleCard = Color(0xFFF3E5F5)
    val successGreen = Color(0xFF2E7D32)
    val successBg = Color(0xFFE8F5E9)

    Scaffold(
        containerColor = backgroundColor,
        bottomBar = {
            Column {
                // Área de Resumo e Botão Continuar
                Surface(
                    color = Color.White,
                    shadowElevation = 8.dp,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Tempo estimado", color = textGrey, fontSize = 14.sp)
                            Text(text = "70 min", color = textDark, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Total com insumos", color = textGrey, fontSize = 14.sp)
                            Text(text = "R$ 32,00", color = primaryPurple, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        }

                        Spacer(modifier = Modifier.height(20.dp))

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
                                Text("Continuar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Barra de Navegação Inferior
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 0.dp
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
            Text(text = "Como vamos cuidar\ndas suas roupas?", color = textDark, fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 30.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Escolha o serviço e confira os insumos.", color = textGrey, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(24.dp))

            // Stepper de Progresso
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StepItemServico("Máquina", estado = "concluido")
                StepItemServico("Serviço", estado = "ativo")
                StepItemServico("Resumo", estado = "pendente")
                StepItemServico("Pagamento", estado = "pendente")
            }
            HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp, modifier = Modifier.padding(top = 8.dp))

            Spacer(modifier = Modifier.height(24.dp))

            // Máquina Selecionada
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(16.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.LocalLaundryService, contentDescription = null, tint = primaryPurple, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = "Máquina 01", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = textDark)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = "Capacidade máxima: 12 kg", fontSize = 12.sp, color = textGrey)
                        }
                    }
                    Row(
                        modifier = Modifier
                            .background(successBg, RoundedCornerShape(16.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = successGreen, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Livre", color = successGreen, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Seu serviço", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textDark)
            Spacer(modifier = Modifier.height(16.dp))

            // Opções de Serviço
            ServiceOptionCard(
                titulo = "Lavagem", info = "35 min · R$ 18,00", icone = Icons.Outlined.WaterDrop,
                selecionado = servicoSelecionado == "Lavagem", onClick = { servicoSelecionado = "Lavagem" }
            )
            ServiceOptionCard(
                titulo = "Secagem", info = "35 min · R$ 14,00", icone = Icons.Outlined.Air,
                selecionado = servicoSelecionado == "Secagem", onClick = { servicoSelecionado = "Secagem" }
            )
            ServiceOptionCard(
                titulo = "Lavagem e secagem", info = "70 min · R$ 32,00", icone = Icons.Outlined.LocalLaundryService,
                selecionado = servicoSelecionado == "Lavagem e secagem", onClick = { servicoSelecionado = "Lavagem e secagem" }
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Cuidado na medida certa", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textDark)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Dosagem disponível para serviços com lavagem. Na secagem avulsa, não são usados insumos.", color = textGrey, fontSize = 12.sp, lineHeight = 16.sp)

            Spacer(modifier = Modifier.height(16.dp))

            // Bloco de Insumos (Sabão e Amaciante)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Sabão
                    InsumoItem(nome = "Sabão líquido", icone = Icons.Outlined.WaterDrop, detalhe = "Automática - padrão 30 mL")
                    Spacer(modifier = Modifier.height(16.dp))
                    // Amaciante
                    InsumoItem(nome = "Amaciante", icone = Icons.Outlined.AutoAwesome, detalhe = "Automática - padrão 15 mL")

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Sabão e amaciante incluídos. Não é necessário adicionar produtos à máquina.",
                        color = textGrey, fontSize = 11.sp, lineHeight = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Banner de Aviso
            Card(
                colors = CardDefaults.cardColors(containerColor = lightPurpleCard),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Icon(Icons.Outlined.Checkroom, contentDescription = null, tint = primaryPurple, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "Antes de continuar", color = primaryPurple, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Confira as etiquetas, esvazie os bolsos e respeite o limite de 12 kg da Máquina 01.",
                            color = primaryPurple, fontSize = 12.sp, lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// Componentes Auxiliares Específicos para este ecrã
@Composable
fun StepItemServico(label: String, estado: String) {
    val primaryPurple = Color(0xFF673AB7)
    val greyText = Color(0xFF9E9E9E)

    val color = if (estado == "concluido" || estado == "ativo") primaryPurple else greyText
    val weight = if (estado == "ativo") FontWeight.Bold else FontWeight.Normal

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = color, fontWeight = weight, fontSize = 13.sp)
        if (estado == "ativo") {
            Spacer(modifier = Modifier.height(4.dp))
            Box(modifier = Modifier.height(2.dp).width(30.dp).background(primaryPurple, RoundedCornerShape(2.dp)))
        }
    }
}

@Composable
fun ServiceOptionCard(titulo: String, info: String, icone: ImageVector, selecionado: Boolean, onClick: () -> Unit) {
    val primaryPurple = Color(0xFF673AB7)
    val borderColor = if (selecionado) primaryPurple else Color(0xFFEEEEEE)
    val bgColor = if (selecionado) Color(0xFFF3E5F5) else Color.White
    val borderWidth = if (selecionado) 2.dp else 1.dp

    Card(
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .border(borderWidth, borderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icone, contentDescription = null, tint = if (selecionado) primaryPurple else Color(0xFF757575), modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = titulo, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF212121))
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = info, fontSize = 13.sp, color = Color(0xFF757575))
                }
            }
            if (selecionado) {
                Icon(Icons.Filled.RadioButtonChecked, contentDescription = null, tint = primaryPurple, modifier = Modifier.size(24.dp))
            } else {
                Icon(Icons.Outlined.RadioButtonUnchecked, contentDescription = null, tint = Color(0xFFE0E0E0), modifier = Modifier.size(24.dp))
            }
        }
    }
}

@Composable
fun InsumoItem(nome: String, icone: ImageVector, detalhe: String) {
    val successGreen = Color(0xFF2E7D32)

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icone, contentDescription = null, tint = Color(0xFF757575), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = nome, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF212121))
            }
            Text(text = "Incluído", color = successGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        // "Falso" Dropdown para efeitos visuais
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = detalhe, fontSize = 13.sp, color = Color(0xFF212121))
            Icon(Icons.Outlined.KeyboardArrowDown, contentDescription = null, tint = Color(0xFF757575))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ServicoScreenPreview() {
    ServicoScreen()
}