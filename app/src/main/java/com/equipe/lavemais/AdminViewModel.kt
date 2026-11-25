package com.equipe.lavemais

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Response

/** Dados e ações do painel do administrador (máquinas e serviços). */
class AdminViewModel(app: Application) : AndroidViewModel(app) {

    private val sessao = SessaoUsuario(app)
    private val api = ApiClient.service(app)

    var maquinas by mutableStateOf<List<MaquinaResponse>>(emptyList())
        private set

    var servicos by mutableStateOf<List<ServicoResponse>>(emptyList())
        private set

    /** Chamados de todos os clientes (suporte, reembolso e exclusão de conta). */
    var chamados by mutableStateOf<List<SuporteResponse>>(emptyList())
        private set

    var carregando by mutableStateOf(false)
        private set

    /** Aviso para mostrar na tela (sucesso ou erro). A tela chama [limparMensagem] ao exibir. */
    var mensagem by mutableStateOf<String?>(null)
        private set

    fun limparMensagem() {
        mensagem = null
    }

    fun limpar() {
        maquinas = emptyList()
        servicos = emptyList()
        chamados = emptyList()
        mensagem = null
    }

    fun carregar(aoExpirar: () -> Unit) {
        if (carregando) return
        carregando = true
        viewModelScope.launch {
            try {
                buscarListas(aoExpirar)
            } catch (_: Exception) {
                mensagem = ApiClient.mensagemFalha()
            } finally {
                carregando = false
            }
        }
    }

    // ----- Máquinas -----

    /** [id] nulo cadastra uma máquina nova; com id, atualiza a existente. */
    fun salvarMaquina(id: Int?, nome: String, capacidade: Double, descricao: String, aoExpirar: () -> Unit) {
        val corpo = MaquinaRequest(nome.trim(), capacidade, descricao.trim().ifEmpty { null })
        executar(aoExpirar, if (id == null) "Máquina cadastrada." else "Máquina atualizada.") {
            if (id == null) api.criarMaquina(corpo) else api.atualizarMaquina(id, corpo)
        }
    }

    fun alterarEstadoMaquina(id: Int, estado: String, aoExpirar: () -> Unit) {
        executar(aoExpirar, "Estado da máquina atualizado.") {
            api.alterarEstadoMaquina(id, EstadoMaquinaRequest(estado))
        }
    }

    fun excluirMaquina(id: Int, aoExpirar: () -> Unit) {
        executar(aoExpirar, "Máquina excluída.") { api.excluirMaquina(id) }
    }

    // ----- Serviços -----

    fun salvarServico(
        id: Int?,
        nome: String,
        descricao: String,
        preco: Double,
        duracao: Int,
        aoExpirar: () -> Unit
    ) {
        val corpo = ServicoRequest(nome.trim(), descricao.trim().ifEmpty { null }, preco, duracao)
        executar(aoExpirar, if (id == null) "Serviço cadastrado." else "Serviço atualizado.") {
            if (id == null) api.criarServico(corpo) else api.atualizarServico(id, corpo)
        }
    }

    fun excluirServico(id: Int, aoExpirar: () -> Unit) {
        executar(aoExpirar, "Serviço excluído.") { api.excluirServico(id) }
    }

    // ----- Chamados -----

    fun alterarEstadoChamado(id: Int, estado: String, aoExpirar: () -> Unit) {
        executar(aoExpirar, "Chamado atualizado.") {
            api.alterarEstadoSuporte(id, EstadoSuporteRequest(estado))
        }
    }

    /** Pedido de exclusão de dados: apaga a conta do cliente e marca o chamado como resolvido. */
    fun removerUsuarioDoChamado(chamado: SuporteResponse, aoExpirar: () -> Unit) {
        executar(aoExpirar, "Usuário removido e solicitação concluída.") {
            val remocao = api.removerUsuario(chamado.usuarioId)
            if (remocao.isSuccessful) {
                api.alterarEstadoSuporte(chamado.id, EstadoSuporteRequest("resolvido"))
            } else {
                remocao
            }
        }
    }

    // ----- Internos -----

    /** Faz a chamada e, se der certo, avisa e recarrega as listas. */
    private fun executar(
        aoExpirar: () -> Unit,
        sucesso: String,
        chamada: suspend () -> Response<*>
    ) {
        if (carregando) return
        carregando = true
        viewModelScope.launch {
            try {
                if (chamada().deuCerto(aoExpirar)) {
                    if (buscarListas(aoExpirar)) mensagem = sucesso
                }
            } catch (_: Exception) {
                mensagem = ApiClient.mensagemFalha()
            } finally {
                carregando = false
            }
        }
    }

    /** Busca máquinas, serviços e chamados; devolve false se alguma chamada falhou. */
    private suspend fun buscarListas(aoExpirar: () -> Unit): Boolean {
        val listaMaquinas = api.maquinas()
        if (!listaMaquinas.deuCerto(aoExpirar)) return false
        val listaServicos = api.servicos()
        if (!listaServicos.deuCerto(aoExpirar)) return false

        val listaChamados = api.suportes()
        if (!listaChamados.deuCerto(aoExpirar)) return false

        maquinas = listaMaquinas.body().orEmpty()
        servicos = listaServicos.body().orEmpty()
        chamados = listaChamados.body().orEmpty()
        return true
    }

    /** true se a resposta foi 2xx; trata sessão expirada (401) e falta de permissão (403). */
    private fun Response<*>.deuCerto(aoExpirar: () -> Unit): Boolean {
        when {
            code() == 401 -> {
                sessao.limpar()
                limpar()
                aoExpirar()
            }
            code() == 403 -> mensagem = "Sem permissão. Entre com a conta de administrador."
            isSuccessful -> return true
            else -> mensagem = ApiClient.mensagemErro(this)
        }
        return false
    }
}
