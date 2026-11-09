package com.equipe.lavemais

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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val emailRegex = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")

@Composable
fun CadastroScreen(
    carregando: Boolean = false,
    erroServidor: String? = null,
    onCadastrar: (String, String, String, String, String) -> Unit = { _, _, _, _, _ -> },
    onVoltar: () -> Unit = {},
    onEntrar: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    // Estados dos campos (começam vazios)
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }
    var cpf by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var confirmacaoSenha by remember { mutableStateOf("") }
    var termosAceites by remember { mutableStateOf(false) }
    var tentouEnviar by remember { mutableStateOf(false) }

    // Cores extraídas
    val backgroundColor = Color(0xFFFAFAFA)
    val primaryPurple = Color(0xFF673AB7)
    val lightPurpleCard = Color(0xFFF3E5F5)
    val textDark = Color(0xFF212121)
    val textGrey = Color(0xFF757575)
    val errorRed = Color(0xFFD32F2F)

    // Regras de validação (iguais às da API)
    val erroNome = if (nome.isBlank()) "Informe seu nome completo." else null
    val erroEmail = if (!emailRegex.matches(email.trim())) "Digite um e-mail válido." else null
    val erroTelefone =
        if (telefone.length !in 10..11) "Digite o telefone com DDD (10 ou 11 números)." else null
    val erroCpf = if (cpf.length != 11) "Obrigatório: informe os 11 números do CPF." else null
    val erroSenha =
        if (senha.length < 8 || senha.none { it.isLetter() } || senha.none { it.isDigit() })
            "Use no mínimo 8 caracteres, com letras e números." else null
    val erroConfirmacao =
        if (confirmacaoSenha.isEmpty() || confirmacaoSenha != senha) "As senhas não coincidem." else null

    val formularioValido = listOf(
        erroNome, erroEmail, erroTelefone, erroCpf, erroSenha, erroConfirmacao
    ).all { it == null } && termosAceites

    // Só mostra os erros depois da primeira tentativa de envio
    fun mostrar(erro: String?): String? = if (tentouEnviar) erro else null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.Start
    ) {
        // Cabeçalho (Botão Voltar e Slogan)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onVoltar,
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(12.dp))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = textDark
                )
            }
            Text(
                text = "Cuidado em cada ciclo",
                color = textGrey,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Título e Subtítulo
        Text(
            text = "Seu primeiro ciclo\ncomeça aqui.",
            color = textDark,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            lineHeight = 34.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Crie sua conta na Lila. É simples, seguro e sem papelada.",
            color = textGrey,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Banner de Aviso (Preenchimento obrigatório)
        Card(
            colors = CardDefaults.cardColors(containerColor = lightPurpleCard),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(16.dp)) {
                Icon(
                    imageVector = Icons.Outlined.Assignment,
                    contentDescription = null,
                    tint = primaryPurple,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Preenchimento obrigatório",
                        color = primaryPurple,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Todos os campos com * são obrigatórios.",
                        color = primaryPurple,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        CampoCadastro(
            rotulo = "Nome completo *",
            valor = nome,
            aoMudar = { nome = it },
            erro = mostrar(erroNome),
            valido = erroNome == null,
            habilitado = !carregando
        )

        Spacer(modifier = Modifier.height(16.dp))

        CampoCadastro(
            rotulo = "E-mail *",
            valor = email,
            aoMudar = { email = it },
            erro = mostrar(erroEmail),
            valido = erroEmail == null,
            teclado = KeyboardType.Email,
            habilitado = !carregando
        )

        Spacer(modifier = Modifier.height(16.dp))

        CampoCadastro(
            rotulo = "Telefone *",
            valor = telefone,
            aoMudar = { telefone = it.filter { c -> c.isDigit() }.take(11) },
            erro = mostrar(erroTelefone),
            dica = "Somente números, com DDD.",
            valido = erroTelefone == null,
            teclado = KeyboardType.Phone,
            habilitado = !carregando
        )

        Spacer(modifier = Modifier.height(16.dp))

        CampoCadastro(
            rotulo = "CPF *",
            valor = cpf,
            aoMudar = { cpf = it.filter { c -> c.isDigit() }.take(11) },
            erro = mostrar(erroCpf),
            dica = "Somente números. Usado para emitir comprovantes.",
            valido = erroCpf == null,
            teclado = KeyboardType.Number,
            habilitado = !carregando
        )

        Spacer(modifier = Modifier.height(16.dp))

        CampoCadastro(
            rotulo = "Senha *",
            valor = senha,
            aoMudar = { senha = it },
            erro = mostrar(erroSenha),
            dica = "Use no mínimo 8 caracteres, com letras e números.",
            valido = erroSenha == null,
            teclado = KeyboardType.Password,
            ehSenha = true,
            iconeInicio = { Icon(Icons.Outlined.Lock, contentDescription = null) },
            habilitado = !carregando
        )

        Spacer(modifier = Modifier.height(16.dp))

        CampoCadastro(
            rotulo = "Confirmação de senha *",
            valor = confirmacaoSenha,
            aoMudar = { confirmacaoSenha = it },
            erro = mostrar(erroConfirmacao),
            dica = if (erroConfirmacao == null) "As senhas coincidem." else null,
            valido = erroConfirmacao == null,
            teclado = KeyboardType.Password,
            ehSenha = true,
            iconeInicio = { Icon(Icons.Outlined.Lock, contentDescription = null) },
            habilitado = !carregando
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Checkbox de Termos
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Checkbox(
                checked = termosAceites,
                onCheckedChange = { termosAceites = it },
                colors = CheckboxDefaults.colors(checkedColor = primaryPurple)
            )
            Column(modifier = Modifier.padding(top = 12.dp)) {
                Text(
                    text = "Concordo com o tratamento dos meus dados pessoais para criar minha conta e prestar os serviços da Lila. *",
                    fontSize = 12.sp,
                    color = textDark
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Ler a Política de Privacidade",
                    fontSize = 12.sp,
                    color = primaryPurple,
                    fontWeight = FontWeight.SemiBold
                )
                if (tentouEnviar && !termosAceites) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "É necessário aceitar para continuar.",
                        fontSize = 12.sp,
                        color = errorRed
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Erro vindo da API (e-mail já cadastrado, sem conexão etc.)
        if (erroServidor != null) {
            Text(
                text = erroServidor,
                color = errorRed,
                fontSize = 13.sp,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Botão Criar Conta
        Button(
            onClick = {
                tentouEnviar = true
                if (formularioValido) {
                    onCadastrar(nome.trim(), email.trim(), telefone, cpf, senha)
                }
            },
            enabled = !carregando,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primaryPurple),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (carregando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(text = "Criar conta", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (tentouEnviar && !formularioValido) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Corrija os campos destacados e aceite o consentimento para continuar.",
                color = errorRed,
                fontSize = 10.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Rodapé de Login
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = buildAnnotatedString {
                    append("Já tenho conta · ")
                    withStyle(style = SpanStyle(color = primaryPurple, fontWeight = FontWeight.Bold)) {
                        append("Entrar")
                    }
                },
                modifier = Modifier.clickable(enabled = !carregando) { onEntrar() }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Ambiente demonstrativo · Dados fictícios e protegidos.",
                color = textGrey,
                fontSize = 10.sp
            )
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}

/** Rótulo + campo + mensagem de erro/dica, no mesmo estilo do layout original. */
@Composable
internal fun CampoCadastro(
    rotulo: String,
    valor: String,
    aoMudar: (String) -> Unit,
    erro: String?,
    valido: Boolean,
    dica: String? = null,
    teclado: KeyboardType = KeyboardType.Text,
    ehSenha: Boolean = false,
    iconeInicio: (@Composable () -> Unit)? = null,
    habilitado: Boolean = true
) {
    val successGreen = Color(0xFF4CAF50)
    val errorRed = Color(0xFFD32F2F)

    Text(text = rotulo, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color(0xFF212121))
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = valor,
        onValueChange = aoMudar,
        singleLine = true,
        enabled = habilitado,
        isError = erro != null,
        leadingIcon = iconeInicio,
        trailingIcon = {
            if (erro != null) {
                Icon(Icons.Outlined.ErrorOutline, contentDescription = "Erro", tint = errorRed)
            } else if (valido && valor.isNotEmpty()) {
                Icon(Icons.Outlined.CheckCircle, contentDescription = "Válido", tint = successGreen)
            }
        },
        visualTransformation = if (ehSenha) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = teclado),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    )

    val texto = erro ?: dica
    if (texto != null) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = texto,
            color = if (erro != null) errorRed else Color(0xFF757575),
            fontSize = 12.sp
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CadastroScreenPreview() {
    CadastroScreen()
}
