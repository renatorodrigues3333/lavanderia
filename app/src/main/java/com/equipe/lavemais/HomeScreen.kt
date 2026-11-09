package com.equipe.lavemais

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(
    nome: String = "Ana Clara",
    livres: Int = 3,
    emUso: Int = 2,
    foraServico: Int = 1,
    onNavegar: (String) -> Unit = {}
) {
    val primeiroNome = nome.trim().substringBefore(" ")
    val scrollState = rememberScrollState()

    // Cores extraídas do Figma
    val backgroundColor = Color(0xFFFAFAFA)
    val primaryPurple = Color(0xFF673AB7)
    val lightPurpleCard = Color(0xFFF3E5F5)
    val textDark = Color(0xFF212121)
    val textGrey = Color(0xFF757575)

    // Cores dos status das máquinas
    val greenBg = Color(0xFFE8F5E9)
    val greenText = Color(0xFF2E7D32)
    val yellowBg = Color(0xFFFFFDE7)
    val yellowText = Color(0xFFF57F17)
    val redBg = Color(0xFFFFEBEE)
    val redText = Color(0xFFC62828)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.Start
    ) {
        // Cabeçalho superior (Logo, Unidade e Notificação)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_lavemais),
                contentDescription = "Logo Lave+",
                modifier = Modifier.height(36.dp)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Unidade Jardim Aurora",
                    fontSize = 12.sp,
                    color = textGrey,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = { /* Notificações */ }, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notificações",
                        tint = textDark,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Saudação
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Olá, $primeiroNome",
                color = textDark,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "☀️", fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Seu dia mais leve começa com um ciclo.",
            color = textGrey,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Card de Destaque Principal (Hero Card)
        Card(
            colors = CardDefaults.cardColors(containerColor = lightPurpleCard),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.LocalLaundryService,
                                contentDescription = null,
                                tint = primaryPurple,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "100% autosserviço",
                                color = primaryPurple,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Mais cuidado.\nMenos espera.",
                            color = primaryPurple,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            lineHeight = 28.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Escolha, pague e acompanhe tudo por aqui.",
                            color = primaryPurple,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(Color.White, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.LocalLaundryService,
                            contentDescription = null,
                            tint = primaryPurple,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onNavegar(Rotas.MAQUINAS) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryPurple),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Escolher máquina", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Seção: Sua unidade agora
        Text(
            text = "Sua unidade agora",
            color = textDark,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "${livres + emUso + foraServico} máquinas · Disponibilidade em tempo real",
            color = textGrey,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Cards de Status das Máquinas (Linha com 3 itens)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Livres
            Card(
                colors = CardDefaults.cardColors(containerColor = greenBg),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "$livres", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = greenText)
                        Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = greenText, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Livres", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = greenText)
                }
            }

            // Em uso
            Card(
                colors = CardDefaults.cardColors(containerColor = yellowBg),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "$emUso", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = yellowText)
                        Icon(Icons.Outlined.Schedule, contentDescription = null, tint = yellowText, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Em uso", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = yellowText)
                }
            }

            // Fora de serviço
            Card(
                colors = CardDefaults.cardColors(containerColor = redBg),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "$foraServico", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = redText)
                        Icon(Icons.Outlined.Build, contentDescription = null, tint = redText, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Fora de serviço", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = redText)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Seção: O que você precisa?
        Text(
            text = "O que você precisa?",
            color = textDark,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Grelha de Ações Rápidas (2x2)
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Card 1: Escolher máquina
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f).clickable { onNavegar(Rotas.MAQUINAS) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Icon(Icons.Outlined.LocalLaundryService, contentDescription = null, tint = primaryPurple, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "Escolher máquina", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = textDark)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "Veja serviços e valores", fontSize = 11.sp, color = textGrey)
                    }
                }
                // Card 2: Meus pedidos
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f).clickable { onNavegar(Rotas.HISTORICO) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Icon(Icons.Outlined.ReceiptLong, contentDescription = null, tint = primaryPurple, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "Meus pedidos", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = textDark)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "Acompanhe seus serviços", fontSize = 11.sp, color = textGrey)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Card 3: Histórico
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f).clickable { onNavegar(Rotas.HISTORICO) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Icon(Icons.Outlined.History, contentDescription = null, tint = primaryPurple, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "Histórico", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = textDark)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "Pedidos e comprovantes", fontSize = 11.sp, color = textGrey)
                    }
                }
                // Card 4: Suporte
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f).clickable { onNavegar(Rotas.PERFIL) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Icon(Icons.Outlined.SupportAgent, contentDescription = null, tint = primaryPurple, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "Suporte", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = textDark)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "Ajuda quando precisar", fontSize = 11.sp, color = textGrey)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Card de Informações da Unidade
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = primaryPurple, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Jardim Aurora", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = textDark)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Rua das Lavandas, 120 - endereço fictício", fontSize = 12.sp, color = textGrey)

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Todos os dias, das 6h às 23h. Unidade automatizada, sem funcionários no local.",
                    fontSize = 12.sp,
                    color = textDark,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Precisa de ajuda? O suporte está no seu perfil.",
                    fontSize = 12.sp,
                    color = primaryPurple,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Rodapé descritivo
        Text(
            text = "Lila Lavanderia · Ambiente demonstrativo. Todos os dados deste aplicativo são fictícios.",
            color = textGrey,
            fontSize = 10.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}