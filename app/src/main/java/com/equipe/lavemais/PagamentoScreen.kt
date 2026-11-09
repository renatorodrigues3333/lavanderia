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
import androidx.compose.material.icons.filled.RadioButtonChecked
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

@Composable
fun PagamentoScreen(
    onNavegar: (String) -> Unit = {},
    onVoltar: () -> Unit = {},
    onContinuar: () -> Unit = {}
) {
    var metodoSelecionado by remember { mutableStateOf("Pix") }

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
            Text(text = "Falta só o pagamento", color = textDark, fontWeight = FontWeight.Bold, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Escolha como pagar. O início da máquina é uma etapa separada.", color = textGrey, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(24.dp))

            // Stepper de Progresso
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StepItemPagamento("Máquina", estado = "concluido")
                StepItemPagamento("Serviço", estado = "concluido")
                StepItemPagamento("Resumo", estado = "concluido")
                StepItemPagamento("Pagamento", estado = "ativo")
            }
            HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp, modifier = Modifier.padding(top = 8.dp))

            Spacer(modifier = Modifier.height(24.dp))

            // Cartão de Resumo Simplificado
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Pedido LLA-1048", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = textDark)
                        Text(text = "R$ 32,00", color = primaryPurple, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Máquina 01 · Lavagem e secagem · 70 min", fontSize = 12.sp, color = textGrey)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Sabão e amaciante incluídos.", fontSize = 12.sp, color = textGrey)

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .background(warningBg, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.AccessTime, contentDescription = null, tint = warningText, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Aguardando pagamento", color = warningText, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Forma de pagamento", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textDark)
            Spacer(modifier = Modifier.height(16.dp))

            // Opções de Pagamento
            MetodoPagamentoCard(
                titulo = "Pix", subtitulo = "Selecionado - confirmação após pagamento", icone = Icons.Outlined.QrCodeScanner,
                selecionado = metodoSelecionado == "Pix", onClick = { metodoSelecionado = "Pix" }
            )
            MetodoPagamentoCard(
                titulo = "Cartão de crédito", subtitulo = "Seleção demonstrativa - nenhum cartão salvo", icone = Icons.Outlined.CreditCard,
                selecionado = metodoSelecionado == "Credito", onClick = { metodoSelecionado = "Credito" }
            )
            MetodoPagamentoCard(
                titulo = "Cartão de débito", subtitulo = "Seleção demonstrativa - nenhum cartão salvo", icone = Icons.Outlined.CreditCard,
                selecionado = metodoSelecionado == "Debito", onClick = { metodoSelecionado = "Debito" }
            )

            // Secção Pix Expandida
            if (metodoSelecionado == "Pix") {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Pix - Apenas demonstração", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = textDark)
                        Spacer(modifier = Modifier.height(16.dp))

                        // Fake QR Code Box
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .background(lightPurpleCard, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.QrCode2, contentDescription = "QR Code", tint = primaryPurple, modifier = Modifier.size(80.dp))
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "QR Code ilustrativo, sem valor de pagamento. Não escaneie nem utilize em um aplicativo bancário.",
                            color = textGrey, fontSize = 11.sp, textAlign = TextAlign.Center, lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Text(text = "CÓDIGO FICTÍCIO - NÃO UTILIZÁVEL", fontSize = 10.sp, color = textGrey, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "LILA-DEMO-SEM-VALOR-1048", fontSize = 13.sp, color = textDark, fontWeight = FontWeight.SemiBold)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { /* Copiar código */ },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = lightPurpleCard, contentColor = primaryPurple),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Outlined.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Copiar código demonstrativo", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Aviso: Início não liberado
            Card(
                colors = CardDefaults.cardColors(containerColor = warningBg),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Icon(Icons.Outlined.Lock, contentDescription = null, tint = warningText, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "Início ainda não liberado", color = warningText, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Aguarde a confirmação do pagamento. Pagar não significa que a máquina começou a funcionar.",
                            color = warningText, fontSize = 12.sp, lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botão Verificar Pagamento
            Button(
                onClick = onContinuar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryPurple),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Sync, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Verificar pagamento", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Se a conexão falhar, verifique o status antes de tentar pagar novamente. Não faça um segundo pagamento sem conferir.",
                color = textGrey, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(), lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)
            Spacer(modifier = Modifier.height(24.dp))

            // Secção: OUTROS ESTADOS DEMONSTRAÇÃO
            Text(text = "OUTROS ESTADOS - DEMONSTRAÇÃO", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = textGrey)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Cenários independentes. Não representam o pedido atual.", fontSize = 11.sp, color = textGrey)

            Spacer(modifier = Modifier.height(16.dp))

            // Exemplo: Sucesso
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .background(successBg, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = successText, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Pagamento confirmado", color = successText, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "R$ 32,00 pagos - Exemplo independente", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = textDark)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Pagamento recebido. O início está liberado, mas o ciclo ainda não foi iniciado. Vá ao acompanhamento e aguarde a confirmação da máquina após solicitar o início.",
                        color = textGrey, fontSize = 12.sp, lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Exemplo: Erro
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .background(errorBg, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Cancel, contentDescription = null, tint = errorText, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Pagamento recusado", color = errorText, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Não foi possível autorizar o pagamento.", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = errorText)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Nenhum valor foi pago neste exemplo. Escolha outra forma ou revise os dados antes de tentar novamente. O início permanece bloqueado.",
                        color = textGrey, fontSize = 12.sp, lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = lightPurpleCard, contentColor = primaryPurple),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Escolher outra forma", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Ambiente fictício. Nenhum dado bancário ou informação sensível de cartão é retido.",
                color = textGrey, fontSize = 10.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// Componentes Auxiliares
@Composable
fun StepItemPagamento(label: String, estado: String) {
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
fun MetodoPagamentoCard(titulo: String, subtitulo: String, icone: ImageVector, selecionado: Boolean, onClick: () -> Unit) {
    val primaryPurple = Color(0xFF673AB7)
    val borderColor = if (selecionado) primaryPurple else Color(0xFFEEEEEE)
    val bgColor = if (selecionado) Color(0xFFF3E5F5) else Color.White
    val borderWidth = if (selecionado) 2.dp else 1.dp

    Card(
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .border(borderWidth, borderColor, RoundedCornerShape(12.dp))
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
                    Text(text = titulo, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF212121))
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = subtitulo, fontSize = 12.sp, color = Color(0xFF757575))
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PagamentoScreenPreview() {
    PagamentoScreen()
}
