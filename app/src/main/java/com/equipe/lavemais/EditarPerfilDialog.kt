package com.equipe.lavemais

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val emailValido = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")

/** Dados que o diálogo devolve ao tocar em "Salvar". */
data class EdicaoPerfil(
    val nome: String,
    val email: String,
    val telefone: String,
    val cpf: String,
    val senhaAtual: String,
    val novaSenha: String
)

/**
 * Diálogo de edição do perfil. Usa os mesmos campos e regras do cadastro.
 * Para salvar é preciso informar a senha atual; a nova senha é opcional.
 */
@Composable
fun EditarPerfilDialog(
    nome: String,
    email: String,
    telefone: String?,
    cpf: String?,
    salvando: Boolean = false,
    erroServidor: String? = null,
    onSalvar: (EdicaoPerfil) -> Unit = {},
    onCancelar: () -> Unit = {}
) {
    val primaryPurple = Color(0xFF673AB7)
    val textGrey = Color(0xFF757575)
    val errorRed = Color(0xFFD32F2F)

    var campoNome by remember { mutableStateOf(nome) }
    var campoEmail by remember { mutableStateOf(email) }
    var campoTelefone by remember { mutableStateOf(telefone.orEmpty().filter { it.isDigit() }) }
    var campoCpf by remember { mutableStateOf(cpf.orEmpty().filter { it.isDigit() }) }
    var senhaAtual by remember { mutableStateOf("") }
    var novaSenha by remember { mutableStateOf("") }
    var confirmacao by remember { mutableStateOf("") }
    var tentouSalvar by remember { mutableStateOf(false) }

    // Regras de validação (iguais às da API)
    val erroNome = if (campoNome.isBlank()) "Informe seu nome completo." else null
    val erroEmail = if (!emailValido.matches(campoEmail.trim())) "Digite um e-mail válido." else null
    val erroTelefone =
        if (campoTelefone.length !in 10..11) "Digite o telefone com DDD (10 ou 11 números)." else null
    val erroCpf = if (campoCpf.length != 11) "Informe os 11 números do CPF." else null
    val erroSenhaAtual = if (senhaAtual.isEmpty()) "Informe sua senha atual para salvar." else null
    val erroNovaSenha =
        if (novaSenha.isNotEmpty() && (novaSenha.length < 8 || novaSenha.none { it.isLetter() } || novaSenha.none { it.isDigit() }))
            "Use no mínimo 8 caracteres, com letras e números." else null
    val erroConfirmacao =
        if (novaSenha.isNotEmpty() && confirmacao != novaSenha) "As senhas não coincidem." else null

    val valido = listOf(
        erroNome, erroEmail, erroTelefone, erroCpf, erroSenhaAtual, erroNovaSenha, erroConfirmacao
    ).all { it == null }

    fun mostrar(erro: String?): String? = if (tentouSalvar) erro else null

    AlertDialog(
        onDismissRequest = { if (!salvando) onCancelar() },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp),
        title = {
            Text("Editar informações", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF212121))
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                CampoCadastro(
                    rotulo = "Nome completo *", valor = campoNome, aoMudar = { campoNome = it },
                    erro = mostrar(erroNome), valido = erroNome == null, habilitado = !salvando
                )
                Spacer(modifier = Modifier.height(12.dp))
                CampoCadastro(
                    rotulo = "E-mail *", valor = campoEmail, aoMudar = { campoEmail = it },
                    erro = mostrar(erroEmail), valido = erroEmail == null,
                    teclado = KeyboardType.Email, habilitado = !salvando
                )
                Spacer(modifier = Modifier.height(12.dp))
                CampoCadastro(
                    rotulo = "Telefone *", valor = campoTelefone,
                    aoMudar = { campoTelefone = it.filter { c -> c.isDigit() }.take(11) },
                    erro = mostrar(erroTelefone), dica = "Somente números, com DDD.",
                    valido = erroTelefone == null, teclado = KeyboardType.Phone, habilitado = !salvando
                )
                Spacer(modifier = Modifier.height(12.dp))
                CampoCadastro(
                    rotulo = "CPF *", valor = campoCpf,
                    aoMudar = { campoCpf = it.filter { c -> c.isDigit() }.take(11) },
                    erro = mostrar(erroCpf), dica = "Somente números.",
                    valido = erroCpf == null, teclado = KeyboardType.Number, habilitado = !salvando
                )

                Spacer(modifier = Modifier.height(20.dp))
                Text("Alterar senha (opcional)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = primaryPurple)
                Spacer(modifier = Modifier.height(12.dp))
                CampoCadastro(
                    rotulo = "Nova senha", valor = novaSenha, aoMudar = { novaSenha = it },
                    erro = mostrar(erroNovaSenha), dica = "Deixe em branco para manter a senha atual.",
                    valido = erroNovaSenha == null, teclado = KeyboardType.Password, ehSenha = true,
                    iconeInicio = { Icon(Icons.Outlined.Lock, contentDescription = null) }, habilitado = !salvando
                )
                if (novaSenha.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    CampoCadastro(
                        rotulo = "Confirmar nova senha", valor = confirmacao, aoMudar = { confirmacao = it },
                        erro = mostrar(erroConfirmacao), valido = erroConfirmacao == null,
                        teclado = KeyboardType.Password, ehSenha = true,
                        iconeInicio = { Icon(Icons.Outlined.Lock, contentDescription = null) }, habilitado = !salvando
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                CampoCadastro(
                    rotulo = "Senha atual *", valor = senhaAtual, aoMudar = { senhaAtual = it },
                    erro = mostrar(erroSenhaAtual), dica = "Necessária para confirmar as alterações.",
                    valido = erroSenhaAtual == null, teclado = KeyboardType.Password, ehSenha = true,
                    iconeInicio = { Icon(Icons.Outlined.Lock, contentDescription = null) }, habilitado = !salvando
                )

                // Erro vindo da API (e-mail já usado, senha atual incorreta, sem conexão etc.)
                if (erroServidor != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = erroServidor, color = errorRed, fontSize = 13.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    tentouSalvar = true
                    if (valido) {
                        onSalvar(
                            EdicaoPerfil(
                                nome = campoNome.trim(),
                                email = campoEmail.trim(),
                                telefone = campoTelefone,
                                cpf = campoCpf,
                                senhaAtual = senhaAtual,
                                novaSenha = novaSenha
                            )
                        )
                    }
                },
                enabled = !salvando,
                colors = ButtonDefaults.buttonColors(containerColor = primaryPurple),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (salvando) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Salvar", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar, enabled = !salvando) {
                Text("Cancelar", color = textGrey, fontWeight = FontWeight.SemiBold)
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun EditarPerfilDialogPreview() {
    EditarPerfilDialog(
        nome = "Ana Clara Lima",
        email = "ana.lima@exemplo.com",
        telefone = "11998764820",
        cpf = "12345678901"
    )
}
