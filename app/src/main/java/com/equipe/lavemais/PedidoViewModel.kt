package com.equipe.lavemais

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Response

/** Guarda o pedido em montagem (máquina -> serviço -> resumo -> pagamento) e o histórico. */
class PedidoViewModel(app: Application) : AndroidViewModel(app) {

    private val sessao = SessaoUsuario(app)
    private val api = ApiClient.service(app)

    var servicos by mutableStateOf<List<ServicoApi>>(emptyList())
        private set

    var pedidos by mutableStateOf<List<UtilizacaoApi>>(emptyList())
        private set

    var maquinaSelecionada by mutableStateOf<MaquinaApi?>(null)
        private set

    var servicoSelecionadoId by mutableStateOf<Int?>(null)
        private set

    /** Id do pedido criado no último "Confirmar e pagar". */
    var pedidoId by mutableStateOf<Int?>(null)
        private set

    /** Momento (relógio do aparelho) em que [pedidos] foi recebido; usado com decorrido_seg. */
    var pedidosRecebidosEm by mutableStateOf(0L)
        private set

    var carregando by mutableStateOf(false)
        private set

    var erro by mutableStateOf<String?>(null)
        private set

    val servicoSelecionado: ServicoApi?
        get() = servicos.firstOrNull { it.id == servicoSelecionadoId } ?: servicoPadrao(servicos)

    val pedidoAtual: UtilizacaoApi?
        get() = pedidos.firstOrNull { it.id == pedidoId }

    fun selecionarMaquina(maquina: MaquinaApi) {
        maquinaSelecionada = maquina
    }

    fun selecionarServico(servico: ServicoApi) {
        servicoSelecionadoId = servico.id
    }

    fun carregarServicos(aoExpirar: () -> Unit) {
        chamar(aoExpirar, { api.servicos() }) { servicos = it ?: emptyList() }
    }

    fun carregarPedidos(aoExpirar: () -> Unit) {
        chamar(aoExpirar, { api.utilizacoes() }) { lista ->
            pedidos = (lista ?: emptyList()).sortedByDescending { it.id }
            pedidosRecebidosEm = System.currentTimeMillis()
        }
    }

    /** Paga o pedido atual; o servidor inicia o ciclo e ocupa a máquina. */
    fun pagar(metodo: String, aoExpirar: () -> Unit, aoConcluir: () -> Unit) {
        val id = pedidoId
        if (id == null) {
            erro = "Nenhum pedido para pagar."
            return
        }
        if (carregando) return

        chamar(aoExpirar, { api.pagar(id, PagamentoRequest(metodo)) }) {
            carregarPedidos(aoExpirar)
            aoConcluir()
        }
    }

    /** Cria a utilização no servidor e, se der certo, chama [aoConcluir]. */
    fun confirmarPedido(aoExpirar: () -> Unit, aoConcluir: () -> Unit) {
        val maquina = maquinaSelecionada
        val servico = servicoSelecionado
        if (maquina == null || servico == null) {
            erro = "Escolha uma máquina e um serviço."
            return
        }
        if (carregando) return

        chamar(aoExpirar, { api.criarUtilizacao(NovaUtilizacaoRequest(maquina.id, servico.id)) }) { resposta ->
            pedidoId = resposta?.id
            carregarPedidos(aoExpirar)
            aoConcluir()
        }
    }

    /** Limpa tudo ao sair da conta, para o próximo usuário não ver dados antigos. */
    fun limpar() {
        servicos = emptyList()
        pedidos = emptyList()
        maquinaSelecionada = null
        servicoSelecionadoId = null
        pedidoId = null
        pedidosRecebidosEm = 0L
        erro = null
    }

    private fun <T> chamar(
        aoExpirar: () -> Unit,
        chamada: suspend () -> Response<T>,
        aoSucesso: (T?) -> Unit
    ) {
        carregando = true
        erro = null

        viewModelScope.launch {
            try {
                val resposta = chamada()
                when {
                    resposta.code() == 401 -> {
                        sessao.limpar()
                        limpar()
                        aoExpirar()
                    }
                    resposta.isSuccessful -> aoSucesso(resposta.body())
                    else -> erro = ApiClient.mensagemErro(resposta)
                }
            } catch (_: Exception) {
                erro = ApiClient.mensagemFalha()
            }
            carregando = false
        }
    }
}
