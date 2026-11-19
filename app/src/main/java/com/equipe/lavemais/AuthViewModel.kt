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

    /** Dados completos de quem está logado (vem de GET /auth/me). */
    var usuario by mutableStateOf<UsuarioApi?>(null)
        private set

    var notificacoes by mutableStateOf<List<NotificacaoApiResponse>>(emptyList())
        private set

    var suportes by mutableStateOf<List<SuporteApiResponse>>(emptyList())
        private set

    var enviandoSuporte by mutableStateOf(false)
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

    /** Envia as alterações do perfil (PUT /usuarios/me). [novaSenha] vazia mantém a senha atual. */
    fun atualizarPerfil(
        nome: String,
        email: String,
        telefone: String,
        senhaAtual: String,
        novaSenha: String,
        cpf: String? = null,
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
                        telefone = telefone.ifBlank { null },
                        cpf = cpf?.ifBlank { null },
                        senhaAtual = senhaAtual.ifEmpty { null },
                        novaSenha = novaSenha.ifEmpty { null }
                    )
                )
                val atualizado = resposta.body()?.paraUsuario()
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
                    usuario = resposta.body()?.paraUsuario()
                }
            } catch (_: Exception) {
                // mantém os dados salvos na sessão
            }
        }
    }

    fun carregarNotificacoes(aoExpirar: () -> Unit) {
        viewModelScope.launch {
            try {
                val resposta = api.notificacoes()
                if (resposta.code() == 401) {
                    sessao.limpar()
                    usuario = null
                    aoExpirar()
                } else if (resposta.isSuccessful) {
                    notificacoes = resposta.body().orEmpty()
                }
            } catch (_: Exception) {
            }
        }
    }

    fun marcarNotificacaoLida(id: Int, aoExpirar: () -> Unit) {
        viewModelScope.launch {
            try {
                val resposta = api.marcarNotificacaoLida(id)
                if (resposta.code() == 401) {
                    sessao.limpar()
                    usuario = null
                    aoExpirar()
                } else if (resposta.isSuccessful) {
                    carregarNotificacoes(aoExpirar)
                }
            } catch (_: Exception) {
            }
        }
    }

    fun carregarSuportes(aoExpirar: () -> Unit) {
        viewModelScope.launch {
            try {
                val resposta = api.suportes()
                if (resposta.code() == 401) {
                    sessao.limpar()
                    usuario = null
                    aoExpirar()
                } else if (resposta.isSuccessful) {
                    suportes = resposta.body().orEmpty().sortedByDescending { it.id }
                }
            } catch (_: Exception) {
            }
        }
    }

    fun criarSuporte(
        tema: String?,
        descricao: String,
        aoExpirar: () -> Unit,
        aoConcluir: () -> Unit
    ) {
        if (enviandoSuporte) return
        enviandoSuporte = true

        viewModelScope.launch {
            try {
                val resposta = api.criarSuporte(SuporteApiRequest(tema = tema, descricao = descricao.trim()))
                if (resposta.code() == 401) {
                    sessao.limpar()
                    usuario = null
                    aoExpirar()
                } else if (resposta.isSuccessful) {
                    carregarSuportes(aoExpirar)
                    aoConcluir()
                } else {
                    erro = ApiClient.mensagemErro(resposta)
                }
            } catch (_: Exception) {
                erro = ApiClient.mensagemFalha()
            }
            enviandoSuporte = false
        }
    }

    fun limparErro() {
        erro = null
    }

    fun entrar(login: String, senha: String, aoConcluir: () -> Unit) {
        if (login.isBlank() || senha.isBlank()) {
            erro = "Preencha o e-mail e a senha."
            return
        }
        carregando = true
        erro = null

        viewModelScope.launch {
            try {
                concluirLogin(api.login(LoginRequest(login.trim(), senha)), aoConcluir)
            } catch (_: Exception) {
                erro = ApiClient.mensagemFalha()
            }
            carregando = false
        }
    }

    /** A API só cria a conta (sem token), então entramos com o mesmo e-mail e senha em seguida. */
    fun cadastrar(
        nome: String,
        email: String,
        telefone: String,
        senha: String,
        cpf: String? = null,
        aoConcluir: () -> Unit
    ) {
        if (carregando) return
        carregando = true
        erro = null

        viewModelScope.launch {
            try {
                val emailLimpo = email.trim().lowercase()
                val cadastro = api.register(
                    CadastroUsuarioRequest(
                        nome = nome.trim(),
                        email = emailLimpo,
                        senha = senha,
                        telefone = telefone.ifBlank { null },
                        cpf = cpf?.ifBlank { null }
                    )
                )
                if (cadastro.isSuccessful) {
                    concluirLogin(api.login(LoginRequest(emailLimpo, senha)), aoConcluir)
                } else {
                    erro = ApiClient.mensagemErro(cadastro)
                }
            } catch (_: Exception) {
                erro = ApiClient.mensagemFalha()
            }
            carregando = false
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

    private fun concluirLogin(resposta: Response<LoginResponse>, aoConcluir: () -> Unit) {
        val token = resposta.body()?.token
        val logado = resposta.body()?.usuario?.paraUsuario()

        if (resposta.isSuccessful && token != null && logado != null) {
            sessao.salvar(token, logado.nome, logado.email)
            usuario = logado
            aoConcluir()
        } else {
            erro = ApiClient.mensagemErro(resposta)
        }
    }
}
