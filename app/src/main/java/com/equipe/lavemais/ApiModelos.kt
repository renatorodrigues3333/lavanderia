package com.equipe.lavemais

// ---------------------------------------------------------------------------
// Modelos que espelham o JSON da API Spring Boot (lavamais-api). Os nomes dos
// campos são em camelCase, exatamente como a API envia e recebe.
// ---------------------------------------------------------------------------

// ----- Autenticação e usuário -----

data class LoginRequest(val email: String, val senha: String)

data class CadastroUsuarioRequest(
    val nome: String,
    val email: String,
    val senha: String,
    val telefone: String? = null
)

/**
 * senhaAtual só é exigida pela API ao trocar e-mail ou senha.
 * novaSenha = null mantém a senha atual (o Gson não envia campos null).
 */
data class AtualizarPerfilRequest(
    val nome: String,
    val email: String,
    val telefone: String? = null,
    val senhaAtual: String? = null,
    val novaSenha: String? = null
)

/** Usuário devolvido por POST /auth/login e GET /auth/me. */
data class UsuarioLogadoApi(
    val id: Int = 0,
    val nome: String = "",
    val email: String = "",
    val administrador: Boolean = false,
    val telefone: String? = null
)

data class LoginResponse(
    val token: String? = null,
    val tipo: String? = null,
    val expiraEmSegundos: Long = 0,
    val usuario: UsuarioLogadoApi? = null
)

/** Usuário devolvido por POST /usuarios e PUT /usuarios/me. */
data class UsuarioResponse(
    val id: Int = 0,
    val nome: String = "",
    val email: String = "",
    val telefone: String? = null
)

// ----- Máquinas e serviços -----

data class MaquinaResponse(
    val id: Int = 0,
    val nome: String = "",
    val capacidade: Double = 0.0,
    val descricao: String? = null,
    val estado: String = "",
    val ativa: Boolean = true
)

data class ServicoResponse(
    val id: Int = 0,
    val nome: String = "",
    val descricao: String? = null,
    val preco: Double = 0.0,
    val duracaoEstimada: Int = 0,
    val ativo: Boolean = true
)

// ----- Painel do administrador -----

data class MaquinaRequest(val nome: String, val capacidade: Double, val descricao: String? = null)

/** estado: "livre", "em_uso", "manutencao" ou "indisponivel". */
data class EstadoMaquinaRequest(val estado: String)

data class ServicoRequest(
    val nome: String,
    val descricao: String? = null,
    val preco: Double,
    val duracaoEstimada: Int
)

// ----- Suporte e notificações -----

/** tema (até 150 caracteres) é o assunto; descricao é obrigatória. */
data class SuporteRequest(val tema: String? = null, val descricao: String)

typealias SuporteApiRequest = SuporteRequest

/** estado: "aberto", "em_atendimento", "resolvido" ou "cancelado". */
data class SuporteResponse(
    val id: Int = 0,
    val usuarioId: Int = 0,
    /** Quem abriu o chamado (o administrador vê; nulo se a conta já foi removida). */
    val usuarioNome: String? = null,
    val usuarioEmail: String? = null,
    val tema: String? = null,
    val descricao: String = "",
    val estado: String = "",
    val criadoEm: String? = null,
    val resolvidoEm: String? = null
)

/** Novo estado do chamado (só o administrador altera). */
data class EstadoSuporteRequest(val estado: String)

typealias SuporteApiResponse = SuporteResponse

/** usuarioId nulo = notificação geral, enviada a todos. */
data class NotificacaoResponse(
    val id: Int = 0,
    val usuarioId: Int? = null,
    val utilizacaoId: Int? = null,
    val tipo: String = "",
    val titulo: String = "",
    val mensagem: String = "",
    val lidoEm: String? = null,
    val criadoEm: String? = null
)

typealias NotificacaoApiResponse = NotificacaoResponse

// ----- Lavanderia (nome e endereço) -----

data class LavanderiaRequest(val nome: String, val endereco: String)

data class LavanderiaResponse(val nome: String = "", val endereco: String = "")

// ----- Utilizações e pagamentos -----

data class UtilizacaoRequest(val maquinaId: Int, val servicoId: Int)

data class UtilizacaoResponse(
    val id: Int = 0,
    val usuarioId: Int = 0,
    val maquinaId: Int = 0,
    val servicoId: Int = 0,
    val estado: String = "",
    val inicioPrevisto: String? = null,
    val inicioReal: String? = null,
    val fimReal: String? = null,
    val valorTotal: Double = 0.0,
    val criadoEm: String? = null
)

/** metodo: "pix", "cartao" ou "dinheiro" (valores do enum da API). */
data class PagamentoRequest(val utilizacaoId: Int, val metodo: String)

data class PagamentoResponse(
    val id: Int = 0,
    val utilizacaoId: Int = 0,
    val estado: String = "",
    val qrCode: String? = null,
    val copiaECola: String? = null,
    val pagoEm: String? = null
)

// ---------------------------------------------------------------------------
// Modelos usados pelas telas. As respostas da API são convertidas para eles,
// assim as telas não precisam conhecer o formato do servidor.
// ---------------------------------------------------------------------------

data class UsuarioApi(
    val id: Int = 0,
    val nome: String = "",
    val email: String = "",
    val telefone: String? = null
)

data class MaquinaApi(
    val id: Int = 0,
    val identificacao: String = "",
    val descricao: String? = null,
    val capacidade_kg: Double = 0.0,
    val estado: String = "",
    val motivo_bloqueio: String? = null
)

data class ServicoApi(
    val id: Int = 0,
    val nome: String = "",
    val tipo: String = "",
    val descricao: String? = null,
    val preco: Double = 0.0,
    val duracao_estimada: Int = 0
)

data class UtilizacaoApi(
    val id: Int = 0,
    val estado: String = "",
    val valor_total: Double = 0.0,
    val criado_em: String? = null,
    val maquina: String = "",
    val servico: String = "",
    val duracao_estimada: Int = 0,
    val inicio_real: String? = null,
    val fim_real: String? = null,
    /** Segundos desde o início do ciclo (null se ainda não começou ou já terminou). */
    val decorrido_seg: Long? = null,
    /** Ids usados para repetir o pedido ("usar como referência"). */
    val maquina_id: Int = 0,
    val servico_id: Int = 0
)

// ----- Conversões API -> telas -----

fun UsuarioLogadoApi.paraUsuario() = UsuarioApi(id, nome, email, telefone)

fun UsuarioResponse.paraUsuario() = UsuarioApi(id, nome, email, telefone)

/** Máquina desativada na API aparece como indisponível. */
fun MaquinaResponse.paraMaquina() = MaquinaApi(
    id = id,
    identificacao = nome,
    descricao = descricao,
    capacidade_kg = capacidade,
    estado = if (ativa) estado else "indisponivel"
)

fun ServicoResponse.paraServico() = ServicoApi(
    id = id,
    nome = nome,
    descricao = descricao,
    preco = preco,
    duracao_estimada = duracaoEstimada
)

/**
 * A API devolve só os ids de máquina e serviço, então os nomes e a duração vêm
 * das listas já carregadas. [agora] é o relógio do aparelho em milissegundos.
 */
fun UtilizacaoResponse.paraUtilizacao(
    maquinas: Map<Int, MaquinaApi>,
    servicos: Map<Int, ServicoApi>,
    agora: Long
): UtilizacaoApi {
    val inicio = dataApi(inicioReal)
    val decorrido = if (estado == "em_andamento" && inicio != null) {
        ((agora - inicio.time) / 1000).coerceAtLeast(0)
    } else null

    return UtilizacaoApi(
        id = id,
        estado = estado,
        valor_total = valorTotal,
        criado_em = criadoEm,
        maquina = maquinas[maquinaId]?.identificacao ?: "Máquina $maquinaId",
        servico = servicos[servicoId]?.nome ?: "Serviço $servicoId",
        duracao_estimada = servicos[servicoId]?.duracao_estimada ?: 0,
        inicio_real = inicioReal,
        fim_real = fimReal,
        decorrido_seg = decorrido,
        maquina_id = maquinaId,
        servico_id = servicoId
    )
}
