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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ResumoScreen(
    onNavegar: (String) -> Unit = {},
    onVoltar: () -> Unit = {},
    onConfirmar: () -> Unit = {}
) {
    // Cores base
    val backgroundColor = Color(0xFFFAFAFA)
    val primaryPurple = Color(0xFF673AB7)
    val textDark = Color(0xFF212121)
    val textGrey = Color(0xFF757575)
    val lightPurpleCard = Color(0xFFF3E5F5)

    Scaffold(
        containerColor = backgroundColor,
        bottomBar = {
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
                    selected = false,
                    onClick = { onNavegar(Rotas.MAQUINAS) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.ReceiptLong, contentDescription = "Pedidos") },
                    label = { Text("Pedidos") },
                    selected = true, // Selecionado conforme o design
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
            Text(
                text = "Tudo certo para\no seu próximo ciclo?",
                color = textDark,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                lineHeight = 30.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Revise os detalhes antes de confirmar.",
                color = textGrey,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Stepper de Progresso
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StepItemResumo("Máquina", estado = "concluido")
                StepItemResumo("Serviço", estado = "concluido")
                StepItemResumo("Resumo", estado = "ativo")
                StepItemResumo("Pagamento", estado = "pendente")
            }
            HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp, modifier = Modifier.padding(top = 8.dp))

            Spacer(modifier = Modifier.height(24.dp))

            // Cartão de Resumo do Pedido
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Máquina Info
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.LocalLaundryService, contentDescription = null, tint = primaryPurple)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(text = "Máquina 01", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = textDark)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = "Até 12 kg · Jardim Aurora", fontSize = 12.sp, color = textGrey)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier
                                    .background(lightPurpleCard, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Outlined.LocalLaundryService, contentDescription = null, tint = primaryPurple, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Lavagem e secagem", color = primaryPurple, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Linhas de Detalhe
                    DetalheRow(label = "Lavagem", value = "R$ 18,00")
                    Spacer(modifier = Modifier.height(12.dp))
                    DetalheRow(label = "Secagem", value = "R$ 14,00")
                    Spacer(modifier = Modifier.height(12.dp))
                    DetalheRow(label = "Sabão - padrão 30 ml", value = "Incluído")
                    Spacer(modifier = Modifier.height(12.dp))
                    DetalheRow(label = "Amaciante - padrão 15 mL", value = "Incluído")

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    DetalheRow(label = "Duração estimada", value = "70 min", isBold = true)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Total do pedido", color = textGrey, fontSize = 14.sp)
                        Text(text = "R$ 32,00", color = primaryPurple, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Pedido demonstrativo LLA-1045. Nenhuma cobrança foi realizada nesta etapa.",
                        color = textGrey,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Seção Depois do Pagamento
            Text(text = "Depois do pagamento", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textDark)
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    InfoItemRow(
                        icone = Icons.Outlined.AccessTime,
                        titulo = "Confirmação do pagamento",
                        descricao = "A liberação do início depende de pagamento confirmado."
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    InfoItemRow(
                        icone = Icons.Outlined.PlayCircleOutline,
                        titulo = "Confirmação da máquina",
                        descricao = "O ciclo só aparece ativo após a máquina confirmar o comando de início."
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Banner Informativo Roxo
            Card(
                colors = CardDefaults.cardColors(containerColor = lightPurpleCard),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Icon(Icons.Outlined.Info, contentDescription = null, tint = primaryPurple, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "Pagamento não inicia a máquina", color = primaryPurple, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Após pagar, confira a carga e acompanhe a confirmação de início. São duas confirmações diferentes.",
                            color = primaryPurple, fontSize = 12.sp, lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botão Confirmar e Pagar
            Button(
                onClick = onConfirmar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryPurple),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Confirmar e pagar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botão Voltar para Editar
            TextButton(
                onClick = onVoltar,
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Icon(Icons.Outlined.Edit, contentDescription = null, tint = primaryPurple, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Voltar para editar", color = primaryPurple, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "A disponibilidade da máquina será conferida novamente antes da confirmação do pedido.",
                color = textGrey,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// Componentes Auxiliares
@Composable
fun StepItemResumo(label: String, estado: String) {
    val primaryPurple = Color(0xFF673AB7)
    val greyText = Color(0xFF9E9E9E)

    val color = if (estado == "concluido" || estado == "ativo") primaryPurple else greyText
    val weight = if (estado == "ativo" || estado == "concluido") FontWeight.Bold else FontWeight.Normal

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = color, fontWeight = weight, fontSize = 13.sp)
        if (estado == "ativo") {
            Spacer(modifier = Modifier.height(4.dp))
            Box(modifier = Modifier.height(2.dp).width(30.dp).background(primaryPurple, RoundedCornerShape(2.dp)))
        }
    }
}

@Composable
fun DetalheRow(label: String, value: String, isBold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = Color(0xFF757575), fontSize = 14.sp)
        Text(
            text = value,
            color = Color(0xFF212121),
            fontSize = 14.sp,
            fontWeight = if (isBold) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
fun InfoItemRow(icone: androidx.compose.ui.graphics.vector.ImageVector, titulo: String, descricao: String) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(Color(0xFFF3E5F5), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icone, contentDescription = null, tint = Color(0xFF673AB7), modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = titulo, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF212121))
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = descricao, color = Color(0xFF757575), fontSize = 12.sp, lineHeight = 16.sp)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ResumoScreenPreview() {
    ResumoScreen()
}