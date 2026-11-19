package com.equipe.lavemais

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/** Nome e endereço da lavanderia: todos leem, só o administrador altera. */
class LavanderiaViewModel(app: Application) : AndroidViewModel(app) {

    private val sessao = SessaoUsuario(app)
    private val api = ApiClient.service(app)

    var dados by mutableStateOf(LavanderiaResponse())
        private set

    var salvando by mutableStateOf(false)
        private set

    var mensagem by mutableStateOf<String?>(null)
        private set

    fun limparMensagem() {
        mensagem = null
    }

    /** Falhas aqui não incomodam o cliente: ele só continua vendo o último endereço conhecido. */
    fun carregar(aoExpirar: () -> Unit) {
        viewModelScope.launch {
            try {
                val resposta = api.lavanderia()
                if (resposta.code() == 401) {
                    sessao.limpar()
                    aoExpirar()
                } else if (resposta.isSuccessful) {
                    dados = resposta.body() ?: LavanderiaResponse()
                }
            } catch (_: Exception) {
                // mantém o que já estava na tela
            }
        }
    }

    fun salvar(nome: String, endereco: String, aoExpirar: () -> Unit) {
        if (salvando) return
        salvando = true
        viewModelScope.launch {
            try {
                val resposta = api.atualizarLavanderia(LavanderiaRequest(nome.trim(), endereco.trim()))
                when {
                    resposta.code() == 401 -> {
                        sessao.limpar()
                        aoExpirar()
                    }
                    resposta.code() == 403 ->
                        mensagem = "Sem permissão. Entre com a conta de administrador."
                    resposta.isSuccessful -> {
                        dados = resposta.body() ?: dados
                        mensagem = "Dados da lavanderia atualizados."
                    }
                    else -> mensagem = ApiClient.mensagemErro(resposta)
                }
            } catch (_: Exception) {
                mensagem = ApiClient.mensagemFalha()
            } finally {
                salvando = false
            }
        }
    }
}
