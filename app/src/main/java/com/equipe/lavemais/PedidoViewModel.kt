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
            val resolvidas = (lista ?: emptyList()).map { item ->
                val nomeMaq = if (item.maquina.isNotBlank()) item.maquina
                              else maquinaSelecionada?.identificacao ?: "Máquina ${item.maquinaId ?: item.id}"
                val nomeServ = if (item.servico.isNotBlank()) item.servico
                               else servicoSelecionado?.nome ?: "Serviço ${item.servicoId ?: ""}"
                item.copy(maquina = nomeMaq, servico = nomeServ)
            }
            pedidos = resolvidas.sortedByDescending { it.id }
            pedidosRecebidosEm = System.currentTimeMillis()
        }
    }

    /** Paga o pedido atual ou verifica pagamento existente (evitando duplicidades) e só inicia se efetivamente aprovado. */
    fun pagar(metodo: String, aoExpirar: () -> Unit, aoConcluir: () -> Unit) {
        val id = pedidoId
        if (id == null) {
            erro = "Nenhum pedido para pagar."
            return
        }
        if (carregando) return
        carregando = true
        erro = null

        viewModelScope.launch {
            try {
                // 1. Consulta pagamentos existentes para a utilização (GET /api/pagamentos?utilizacaoId=id)
                val respConsulta = api.buscarPagamentosPorUtilizacao(id)

                if (respConsulta.code() == 401) {
                    sessao.limpar()
                    limpar()
                    aoExpirar()
                    carregando = false
                    return@launch
                }

                if (!respConsulta.isSuccessful) {
                    // Trata erro na consulta GET (500, 503, etc.) sem tentar criar um novo pagamento indevidamente
                    erro = ApiClient.mensagemErro(respConsulta)
                    carregando = false
                    return@launch
                }

                val pagamentosExistentes = respConsulta.body() ?: emptyList()

                // Seleção inteligente do pagamento ativo:
                // Prioriza "aprovado", depois "pendente", depois "reembolso". Se o último foi "recusado", permite nova tentativa (null)
                val aprovado = pagamentosExistentes.firstOrNull { it.estado.lowercase() == "aprovado" }
                val pendente = pagamentosExistentes.lastOrNull { it.estado.lowercase() == "pendente" }
                val reembolso = pagamentosExistentes.lastOrNull { it.estado.lowercase() == "reembolso" }

                val pagamentoExistente = aprovado ?: pendente ?: reembolso

                val pagamentoAtual: PagamentoResponse? = if (pagamentoExistente != null) {
                    // Já existe um pagamento ativo (aprovado, pendente ou reembolso)! Reutiliza sem criar duplicado.
                    pagamentoExistente
                } else {
                    // Não existe nenhum pagamento ativo (ou o último foi recusado). Cria um novo pagamento (POST /api/pagamentos)
                    val respPag = api.criarPagamento(PagamentoRequest(utilizacao_id = id, metodo = metodo))
                    if (respPag.code() == 401) {
                        sessao.limpar()
                        limpar()
                        aoExpirar()
                        carregando = false
                        return@launch
                    }
                    if (!respPag.isSuccessful) {
                        erro = ApiClient.mensagemErro(respPag)
                        carregando = false
                        return@launch
                    }
                    respPag.body()
                }

                val estadoPagamento = pagamentoAtual?.estado?.lowercase() ?: "pendente"

                when (estadoPagamento) {
                    "recusado" -> {
                        erro = "Pagamento recusado. Escolha outra forma de pagamento ou tente novamente."
                        carregarPedidos(aoExpirar)
                    }
                    "reembolso" -> {
                        erro = "Pagamento com reembolso solicitado. O início do ciclo foi cancelado."
                        carregarPedidos(aoExpirar)
                    }
                    "aprovado" -> {
                        // Pagamento aprovado! Tenta iniciar a utilização (PATCH /api/utilizacoes/{id}/iniciar)
                        val respIniciar = api.iniciarUtilizacao(id)
                        when {
                            respIniciar.code() == 401 -> {
                                sessao.limpar()
                                limpar()
                                aoExpirar()
                            }
                            respIniciar.isSuccessful -> {
                                carregarPedidos(aoExpirar)
                                aoConcluir()
                            }
                            else -> {
                                erro = ApiClient.mensagemErro(respIniciar)
                                carregarPedidos(aoExpirar)
                            }
                        }
                    }
                    else -> { // "pendente" ou outro estado não aprovado
                        // Tenta o endpoint de iniciar para verificar aprovação no servidor
                        val respIniciar = api.iniciarUtilizacao(id)
                        when {
                            respIniciar.code() == 401 -> {
                                sessao.limpar()
                                limpar()
                                aoExpirar()
                            }
                            respIniciar.isSuccessful -> {
                                // O servidor aprovou e iniciou a utilização com sucesso
                                carregarPedidos(aoExpirar)
                                aoConcluir()
                            }
                            respIniciar.code() == 409 -> {
                                // HTTP 409 Conflict: "Pagamento não aprovado"
                                erro = "Pagamento registrado com sucesso! Aguardando confirmação do pagamento para iniciar a máquina."
                                carregarPedidos(aoExpirar)
                            }
                            else -> {
                                erro = ApiClient.mensagemErro(respIniciar)
                                carregarPedidos(aoExpirar)
                            }
                        }
                    }
                }
            } catch (_: Exception) {
                erro = ApiClient.mensagemFalha()
            }
            carregando = false
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
