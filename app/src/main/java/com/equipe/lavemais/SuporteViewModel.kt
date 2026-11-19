package com.equipe.lavemais

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/** Solicitações de suporte/reembolso e notificações do cliente. */
class SuporteViewModel(app: Application) : AndroidViewModel(app) {

    private val sessao = SessaoUsuario(app)
    private val api = ApiClient.service(app)

    var solicitacoes by mutableStateOf<List<SuporteResponse>>(emptyList())
        private set

    var notificacoes by mutableStateOf<List<NotificacaoResponse>>(emptyList())
        private set

    var enviando by mutableStateOf(false)
        private set

    var erro by mutableStateOf<String?>(null)
        private set

    fun limparErro() {
        erro = null
    }

    fun limpar() {
        solicitacoes = emptyList()
        notificacoes = emptyList()
        erro = null
    }

    fun carregarSolicitacoes(aoExpirar: () -> Unit) {
        viewModelScope.launch {
            try {
                val resposta = api.suportes()
                when {
                    resposta.code() == 401 -> expirar(aoExpirar)
                    resposta.isSuccessful -> solicitacoes = resposta.body().orEmpty()
                }
            } catch (_: Exception) {
                // mantém a lista que já estava na tela
            }
        }
    }

    fun carregarNotificacoes(aoExpirar: () -> Unit) {
        viewModelScope.launch {
            try {
                val resposta = api.notificacoes()
                when {
                    resposta.code() == 401 -> expirar(aoExpirar)
                    resposta.isSuccessful -> notificacoes = resposta.body().orEmpty()
                }
            } catch (_: Exception) {
                // mantém a lista que já estava na tela
            }
        }
    }

    /** Marca como lida. Notificações gerais (sem dono) só o administrador pode alterar. */
    fun lerNotificacao(notificacao: NotificacaoResponse, aoExpirar: () -> Unit) {
        if (notificacao.lidoEm != null || notificacao.usuarioId == null) return
        viewModelScope.launch {
            try {
                val resposta = api.marcarNotificacaoLida(notificacao.id)
                if (resposta.code() == 401) {
                    expirar(aoExpirar)
                } else if (resposta.isSuccessful) {
                    carregarNotificacoes(aoExpirar)
                }
            } catch (_: Exception) {
                // sem rede: tenta de novo na próxima abertura
            }
        }
    }

    /** Abre um chamado; [aoConcluir] roda só se o servidor aceitou. */
    fun abrirSolicitacao(
        tema: String,
        descricao: String,
        aoExpirar: () -> Unit,
        aoConcluir: () -> Unit
    ) {
        if (enviando) return
        enviando = true
        erro = null

        viewModelScope.launch {
            try {
                val resposta = api.abrirSuporte(SuporteRequest(tema.take(150), descricao.trim()))
                when {
                    resposta.code() == 401 -> expirar(aoExpirar)
                    resposta.isSuccessful -> {
                        carregarSolicitacoes(aoExpirar)
                        aoConcluir()
                    }
                    else -> erro = ApiClient.mensagemErro(resposta)
                }
            } catch (_: Exception) {
                erro = ApiClient.mensagemFalha()
            } finally {
                enviando = false
            }
        }
    }

    /** A API não apaga contas sozinha, então o pedido de exclusão vira um chamado para a equipe. */
    fun solicitarExclusaoConta(aoExpirar: () -> Unit, aoConcluir: () -> Unit) {
        abrirSolicitacao(
            tema = "Exclusão de conta e dados",
            descricao = "Solicito a exclusão da minha conta e dos meus dados pessoais.",
            aoExpirar = aoExpirar,
            aoConcluir = aoConcluir
        )
    }

    private fun expirar(aoExpirar: () -> Unit) {
        sessao.limpar()
        limpar()
        aoExpirar()
    }
}
