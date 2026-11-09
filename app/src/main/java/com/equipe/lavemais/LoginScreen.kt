package com.equipe.lavemais

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen(
    carregando: Boolean = false,
    erro: String? = null,
    onEntrar: (String, String) -> Unit = { _, _ -> },
    onEsqueciSenha: () -> Unit = {},
    onCriarConta: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    // Cores extraídas do Figma
    val backgroundColor = Color(0xFFFAFAFA)
    val primaryPurple = Color(0xFF673AB7)
    val lightPurpleCard = Color(0xFFF3E5F5)
    val textDark = Color(0xFF212121)
    val textGrey = Color(0xFF757575)
    val errorRed = Color(0xFFD32F2F)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.Start
    ) {
        // Cabeçalho (Logo e Slogan)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_lavemais),
                contentDescription = "Logo Lave+",
                modifier = Modifier.height(48.dp)
            )
            Text(
                text = "Cuidado em cada ciclo",
                color = textGrey,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Cartão de Destaque
        Card(
            colors = CardDefaults.cardColors(containerColor = lightPurpleCard),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(24.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Roupa limpa.\nTempo livre.",
                        color = primaryPurple,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        lineHeight = 28.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Sua lavanderia,\nno seu ritmo.",
                        color = primaryPurple,
                        fontSize = 14.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .size(100.dp, 120.dp)
                        .background(Color.White, RoundedCornerShape(16.dp))
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Textos de Boas-vindas
        Text(
            text = "Bom te ver por aqui.",
            color = textDark,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Entre na Lila Lavanderia e cuide de tudo pelo celular, sem filas e sem complicação.",
            color = textGrey,
            fontSize = 16.sp,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Campo de Email/Telefone
        Text(text = "E-mail ou telefone", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("Digite seu e-mail ou telefone") },
            leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            enabled = !carregando,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de Senha
        Text(text = "Senha", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("Digite sua senha") },
            leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            enabled = !carregando,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        // Esqueci a senha
        TextButton(
            onClick = onEsqueciSenha,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(text = "Esqueci minha senha", color = primaryPurple)
        }

        // Mensagem de erro vinda da API
        if (erro != null) {
            Text(
                text = erro,
                color = errorRed,
                fontSize = 13.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botão Entrar
        Button(
            onClick = { onEntrar(email, password) },
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
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Entrar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Rodapé de Registro
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = buildAnnotatedString {
                    append("Primeira vez na Lila? ")
                    withStyle(style = SpanStyle(color = primaryPurple, fontWeight = FontWeight.Bold)) {
                        append("Criar conta")
                    }
                },
                modifier = Modifier.clickable(enabled = !carregando) { onCriarConta() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Seus dados protegidos, do início ao fim.",
                color = textGrey,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Ambiente demonstrativo · Todos os dados são fictícios.",
                color = textGrey,
                fontSize = 10.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// O Preview deve ficar totalmente isolado aqui no final!
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}
