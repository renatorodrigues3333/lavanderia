package com.equipe.lavemais

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Response

class AuthViewModel(app: Application) : AndroidViewModel(app) {

    private val sessao = SessaoUsuario(app)
    private val api = ApiClient.service(app)

    var carregando by mutableStateOf(false)
        private set

    var erro by mutableStateOf<String?>(null)
        private set

    val logado: Boolean get() = sessao.logado
    val nome: String get() = sessao.nome

    fun limparErro() {
        erro = null
    }

    fun entrar(login: String, senha: String, aoConcluir: () -> Unit) {
        if (login.isBlank() || senha.isBlank()) {
            erro = "Preencha e-mail ou telefone e a senha."
            return
        }
        executarAuth(aoConcluir) { api.login(LoginRequest(login.trim(), senha)) }
    }

    fun cadastrar(
        nome: String,
        email: String,
        telefone: String,
        cpf: String,
        senha: String,
        aoConcluir: () -> Unit
    ) {
        executarAuth(aoConcluir) {
            api.register(
                RegisterRequest(
                    nome = nome.trim(),
                    email = email.trim().lowercase(),
                    telefone = telefone,
                    cpf = cpf,
                    senha = senha
                )
            )
        }
    }

    fun sair(aoConcluir: () -> Unit) {
        viewModelScope.launch {
            try {
                api.logout() // avisa o servidor; se falhar, sai mesmo assim
            } catch (_: Exception) {
            }
            sessao.limpar()
            erro = null
            aoConcluir()
        }
    }

    private fun executarAuth(
        aoConcluir: () -> Unit,
        chamada: suspend () -> Response<AuthResponse>
    ) {
        carregando = true
        erro = null

        viewModelScope.launch {
            try {
                val resposta = chamada()
                val token = resposta.body()?.token
                val usuario = resposta.body()?.usuario

                if (resposta.isSuccessful && token != null && usuario != null) {
                    sessao.salvar(token, usuario.nome, usuario.email)
                    aoConcluir()
                } else {
                    erro = ApiClient.mensagemErro(resposta)
                }
            } catch (_: Exception) {
                erro = ApiClient.mensagemFalha()
            }
            carregando = false
        }
    }
}
