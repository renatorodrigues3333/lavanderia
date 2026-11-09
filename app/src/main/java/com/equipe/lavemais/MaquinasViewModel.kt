package com.equipe.lavemais

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MaquinasViewModel(app: Application) : AndroidViewModel(app) {

    private val sessao = SessaoUsuario(app)
    private val api = ApiClient.service(app)

    var maquinas by mutableStateOf<List<MaquinaApi>>(emptyList())
        private set

    var carregando by mutableStateOf(false)
        private set

    var erro by mutableStateOf<String?>(null)
        private set

    val livres: Int get() = maquinas.count { it.estado == "livre" }
    val emUso: Int get() = maquinas.count { it.estado == "em_uso" }
    val foraDeServico: Int
        get() = maquinas.count { it.estado == "fora_de_servico" || it.estado == "manutencao" }

    /** Carrega as máquinas. Se o token não valer mais, limpa a sessão e avisa. */
    fun carregar(aoExpirar: () -> Unit) {
        carregando = true
        erro = null

        viewModelScope.launch {
            try {
                val resposta = api.maquinas()
                if (resposta.code() == 401) {
                    sessao.limpar()
                    aoExpirar()
                } else if (resposta.isSuccessful) {
                    maquinas = resposta.body() ?: emptyList()
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
