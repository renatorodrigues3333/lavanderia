package com.equipe.lavemais

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
fun NotificacoesScreen(
    notificacoes: List<NotificacaoApiResponse> = emptyList(),
    carregando: Boolean = false,
    erro: String? = null,
    onMarcarLida: (id: Int) -> Unit = {},
    onVoltar: () -> Unit = {}
) {
    val backgroundColor = Color(0xFFFAFAFA)
    val primaryPurple = Color(0xFF673AB7)
    val textDark = Color(0xFF212121)
    val textGrey = Color(0xFF757575)
    val strokeColor = Color(0xFFEEEEEE)

    Scaffold(
        containerColor = backgroundColor
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
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
                        .border(1.dp, strokeColor, RoundedCornerShape(12.dp))
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = textDark)
                }
                Text(
                    text = "Notificações",
                    color = textDark,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.size(40.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (carregando) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = primaryPurple)
                }
            } else if (notificacoes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.NotificationsNone,
                            contentDescription = null,
                            tint = textGrey,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Nenhuma notificação",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = textDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Você receberá novidades e avisos sobre seus ciclos aqui.",
                            fontSize = 13.sp,
                            color = textGrey,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    items(notificacoes) { notif ->
                        val lida = notif.lidoEm != null
                        val icone = notifIcone(notif.tipo)
                        val corIcone = notifCor(notif.tipo)

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (lida) Color.White else Color(0xFFF3E5F5)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, strokeColor, RoundedCornerShape(16.dp))
                                .clickable {
                                    if (!lida) onMarcarLida(notif.id)
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(corIcone.copy(alpha = 0.15f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(icone, contentDescription = null, tint = corIcone, modifier = Modifier.size(20.dp))
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = notif.titulo,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = textDark
                                        )
                                        if (!lida) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .background(primaryPurple, CircleShape)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = notif.mensagem,
                                        fontSize = 13.sp,
                                        color = textGrey,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun notifIcone(tipo: String): ImageVector = when (tipo.lowercase()) {
    "oferta" -> Icons.Outlined.LocalOffer
    "pagamento" -> Icons.Outlined.AttachMoney
    "conclusao" -> Icons.Outlined.CheckCircle
    "suporte" -> Icons.Outlined.SupportAgent
    else -> Icons.Outlined.Notifications
}

private fun notifCor(tipo: String): Color = when (tipo.lowercase()) {
    "oferta" -> Color(0xFFE65100)
    "pagamento" -> Color(0xFF2E7D32)
    "conclusao" -> Color(0xFF1565C0)
    "suporte" -> Color(0xFF673AB7)
    else -> Color(0xFF757575)
}

@Preview(showBackground = true)
@Composable
fun NotificacoesScreenPreview() {
    NotificacoesScreen()
}
