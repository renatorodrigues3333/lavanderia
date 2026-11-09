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

    /** Dados completos de quem está logado (vem de GET /me). */
    var usuario by mutableStateOf<UsuarioApi?>(null)
        private set

    val logado: Boolean get() = sessao.logado
    val nome: String get() = usuario?.nome ?: sessao.nome
    val email: String get() = usuario?.email ?: sessao.email

    var salvandoPerfil by mutableStateOf(false)
        private set

    var erroPerfil by mutableStateOf<String?>(null)
        private set

    fun limparErroPerfil() {
        erroPerfil = null
    }

    /** Envia as alterações do perfil (PUT /me). [novaSenha] vazia mantém a senha atual. */
    fun atualizarPerfil(
        nome: String,
        email: String,
        telefone: String,
        cpf: String,
        senhaAtual: String,
        novaSenha: String,
        aoExpirar: () -> Unit,
        aoConcluir: () -> Unit
    ) {
        if (salvandoPerfil) return
        salvandoPerfil = true
        erroPerfil = null

        viewModelScope.launch {
            try {
                val resposta = api.atualizarPerfil(
                    AtualizarPerfilRequest(
                        nome = nome.trim(),
                        email = email.trim().lowercase(),
                        telefone = telefone,
                        cpf = cpf,
                        senha_atual = senhaAtual,
                        nova_senha = novaSenha.ifEmpty { null }
                    )
                )
                val atualizado = resposta.body()?.usuario
                val token = sessao.token
                when {
                    resposta.code() == 401 -> {
                        sessao.limpar()
                        usuario = null
                        aoExpirar()
                    }
                    resposta.isSuccessful && atualizado != null && token != null -> {
                        usuario = atualizado
                        sessao.salvar(token, atualizado.nome, atualizado.email)
                        aoConcluir()
                    }
                    else -> erroPerfil = ApiClient.mensagemErro(resposta)
                }
            } catch (_: Exception) {
                erroPerfil = ApiClient.mensagemFalha()
            }
            salvandoPerfil = false
        }
    }

    /** Atualiza os dados do perfil. Se o token não valer mais, limpa a sessão e avisa. */
    fun carregarPerfil(aoExpirar: () -> Unit) {
        viewModelScope.launch {
            try {
                val resposta = api.me()
                if (resposta.code() == 401) {
                    sessao.limpar()
                    usuario = null
                    aoExpirar()
                } else if (resposta.isSuccessful) {
                    usuario = resposta.body()
                }
            } catch (_: Exception) {
                // mantém os dados salvos na sessão
            }
        }
    }

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
            usuario = null
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
                    this@AuthViewModel.usuario = usuario
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
