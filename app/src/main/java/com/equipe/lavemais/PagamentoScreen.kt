package com.equipe.lavemais

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PagamentoScreen(
    codigo: String = "LLA-1048",
    valor: Double = 32.0,
    descricao: String = "Máquina 01 · Lavagem e secagem · 70 min",
    enviando: Boolean = false,
    onNavegar: (String) -> Unit = {},
    onVoltar: () -> Unit = {},
    /** Cartões "cadastrados" na simulação (só dados mascarados). */
    cartaoCredito: CartaoSalvo? = null,
    cartaoDebito: CartaoSalvo? = null,
    onSalvarCartao: (tipo: String, cartao: CartaoSalvo) -> Unit = { _, _ -> },
    onRemoverCartao: (tipo: String) -> Unit = {},
    /** Recebe o método para a API ("pix" ou "cartao") e um texto da forma de pagamento para o comprovante. */
    onContinuar: (metodo: String, forma: String) -> Unit = { _, _ -> },
    /** Mensagem real de erro do último pagamento (null = sem erro). */
    erro: String? = null,
    onLimparErro: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val escopo = rememberCoroutineScope()
    var metodoSelecionado by remember { mutableStateOf("Pix") }
    var cadastrandoCartao by remember { mutableStateOf(false) }
    val contexto = LocalContext.current

    val ehCartao = metodoSelecionado != "Pix"
    val tipoCartao = if (metodoSelecionado == "Credito") TIPO_CREDITO else TIPO_DEBITO
    val cartaoAtual = when (metodoSelecionado) {
        "Credito" -> cartaoCredito
        "Debito" -> cartaoDebito
        else -> null
    }
    val codigoPix = "LAVE-DEMO-SEM-VALOR-" + codigo.filter { it.isDigit() }.ifEmpty { "0000" }

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
                .verticalScroll(scrollState)
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
                    text = LocalLavanderia.current.tituloUnidade(),
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
                        Text(text = "Pedido $codigo", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = textDark)
                        Text(text = reais(valor), color = primaryPurple, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = descricao, fontSize = 12.sp, color = textGrey)
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
                titulo = "Cartão de crédito", subtitulo = cartaoCredito?.resumo() ?: "Cadastre um cartão para pagar", icone = Icons.Outlined.CreditCard,
                selecionado = metodoSelecionado == "Credito", onClick = {
                    metodoSelecionado = "Credito"
                    cadastrandoCartao = false
                }
            )
            MetodoPagamentoCard(
                titulo = "Cartão de débito", subtitulo = cartaoDebito?.resumo() ?: "Cadastre um cartão para pagar", icone = Icons.Outlined.CreditCard,
                selecionado = metodoSelecionado == "Debito", onClick = {
                    metodoSelecionado = "Debito"
                    cadastrandoCartao = false
                }
            )

            // Cartão: cartão salvo ou formulário de cadastro (simulação)
            if (ehCartao) {
                if (cartaoAtual != null && !cadastrandoCartao) {
                    CartaoSalvoCard(
                        cartao = cartaoAtual,
                        onTrocar = { cadastrandoCartao = true },
                        onRemover = { onRemoverCartao(tipoCartao) }
                    )
                } else {
                    FormularioCartao(
                        tipoRotulo = if (metodoSelecionado == "Credito") "crédito" else "débito",
                        podeCancelar = cartaoAtual != null,
                        onCancelar = { cadastrandoCartao = false },
                        onSalvar = { cartao ->
                            onSalvarCartao(tipoCartao, cartao)
                            cadastrandoCartao = false
                        }
                    )
                }
            }

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
                            Text(text = codigoPix, fontSize = 13.sp, color = textDark, fontWeight = FontWeight.SemiBold)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                val area = contexto.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                area.setPrimaryClip(ClipData.newPlainText("Código Pix demonstrativo", codigoPix))
                                Toast.makeText(contexto, "Código copiado.", Toast.LENGTH_SHORT).show()
                            },
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
                onClick = {
                    val forma = when (metodoSelecionado) {
                        "Pix" -> "Pix"
                        "Credito" -> "Cartão de crédito ${cartaoAtual?.resumo().orEmpty()}".trim()
                        else -> "Cartão de débito ${cartaoAtual?.resumo().orEmpty()}".trim()
                    }
                    onContinuar(if (metodoSelecionado == "Pix") "pix" else "cartao", forma)
                },
                enabled = !enviando && !cadastrandoCartao && (!ehCartao || cartaoAtual != null),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryPurple),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Sync, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (ehCartao) "Pagar com cartão" else "Verificar pagamento", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (ehCartao && cartaoAtual == null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Cadastre um cartão para continuar.",
                    color = errorText, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Se a conexão falhar, verifique o status antes de tentar pagar novamente. Não faça um segundo pagamento sem conferir.",
                color = textGrey, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(), lineHeight = 16.sp
            )

            // Situação real do pagamento
            if (enviando) {
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(16.dp))
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp, color = primaryPurple)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = "Processando pagamento", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = textDark)
                            Text(text = "Aguarde a confirmação. Não feche esta tela.", fontSize = 12.sp, color = textGrey)
                        }
                    }
                }
            } else if (erro != null) {
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, errorBg, RoundedCornerShape(16.dp))
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
                            Text(text = "Pagamento não concluído", color = errorText, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = erro, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = errorText)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Confira os dados ou escolha outra forma de pagamento e tente de novo. O ciclo só começa depois que o pagamento é confirmado.",
                            color = textGrey, fontSize = 12.sp, lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                onLimparErro()
                                escopo.launch { scrollState.animateScrollTo(0) }
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = lightPurpleCard, contentColor = primaryPurple),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Escolher outra forma", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Ambiente fictício. O número completo e o CVV do cartão nunca são guardados.",
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

/** Cartão já cadastrado na simulação, com opção de trocar ou remover. */
@Composable
private fun CartaoSalvoCard(cartao: CartaoSalvo, onTrocar: () -> Unit, onRemover: () -> Unit) {
    val roxo = Color(0xFF673AB7)

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.CreditCard, contentDescription = null, tint = roxo, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = cartao.resumo(), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF212121))
                    Text(
                        text = "${cartao.titular} · válido até ${cartao.validade}",
                        fontSize = 12.sp, color = Color(0xFF757575)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onTrocar) { Text("Trocar cartão", color = roxo, fontWeight = FontWeight.SemiBold) }
                TextButton(onClick = onRemover) { Text("Remover", color = Color(0xFFC62828), fontWeight = FontWeight.SemiBold) }
            }
        }
    }
}

/** Formulário de cadastro de cartão (simulação): valida os dados e guarda só a versão mascarada. */
@Composable
private fun FormularioCartao(
    tipoRotulo: String,
    podeCancelar: Boolean,
    onCancelar: () -> Unit,
    onSalvar: (CartaoSalvo) -> Unit
) {
    var numero by remember { mutableStateOf("") }
    var titular by remember { mutableStateOf("") }
    var validade by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var tentou by remember { mutableStateOf(false) }

    val digitos = somenteDigitos(numero)
    val erroNumero = if (!numeroCartaoValido(digitos)) "Número de cartão inválido." else null
    val erroTitular = if (titular.trim().length < 3) "Informe o nome como está no cartão." else null
    val erroValidade = if (!validadeValida(validade)) "Validade inválida ou vencida." else null
    val erroCvv = if (cvv.length !in 3..4) "O CVV tem 3 ou 4 números." else null

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = "Cadastrar cartão de $tipoRotulo", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF212121))
            Text(
                text = "Simulação: para testar, use 4242 4242 4242 4242, qualquer validade futura e qualquer CVV.",
                fontSize = 11.sp, color = Color(0xFF757575), lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            CampoCartao(
                rotulo = "Número do cartão", valor = numero,
                aoMudar = { numero = formatarNumeroCartao(somenteDigitos(it)) },
                erro = if (tentou) erroNumero else null, teclado = KeyboardType.Number
            )
            CampoCartao(
                rotulo = "Nome no cartão", valor = titular,
                aoMudar = { titular = it.uppercase().filter { c -> c.isLetter() || c == ' ' }.take(26) },
                erro = if (tentou) erroTitular else null, teclado = KeyboardType.Text
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    CampoCartao(
                        rotulo = "Validade (MM/AA)", valor = validade,
                        aoMudar = { validade = formatarValidade(somenteDigitos(it)) },
                        erro = if (tentou) erroValidade else null, teclado = KeyboardType.Number
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    CampoCartao(
                        rotulo = "CVV", valor = cvv,
                        aoMudar = { cvv = somenteDigitos(it).take(4) },
                        erro = if (tentou) erroCvv else null, teclado = KeyboardType.NumberPassword,
                        senha = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    tentou = true
                    if (erroNumero == null && erroTitular == null && erroValidade == null && erroCvv == null) {
                        // O número completo e o CVV ficam só nesta tela e somem aqui
                        onSalvar(CartaoSalvo(bandeiraDoCartao(digitos), digitos.takeLast(4), titular.trim(), validade))
                    }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Salvar cartão", fontWeight = FontWeight.Bold)
            }
            if (podeCancelar) {
                TextButton(onClick = onCancelar, modifier = Modifier.fillMaxWidth()) {
                    Text("Cancelar", color = Color(0xFF757575))
                }
            }
        }
    }
}

@Composable
private fun CampoCartao(
    rotulo: String,
    valor: String,
    aoMudar: (String) -> Unit,
    erro: String?,
    teclado: KeyboardType,
    senha: Boolean = false
) {
    OutlinedTextField(
        value = valor,
        onValueChange = aoMudar,
        label = { Text(rotulo) },
        singleLine = true,
        isError = erro != null,
        supportingText = { if (erro != null) Text(erro) },
        visualTransformation = if (senha) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = teclado),
        modifier = Modifier.fillMaxWidth()
    )
}
