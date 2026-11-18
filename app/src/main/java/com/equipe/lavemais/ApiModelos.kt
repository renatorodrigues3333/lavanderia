package com.equipe.lavemais

import com.google.gson.annotations.SerializedName

// ---------------------------------------------------------------------------
// Modelos que espelham o JSON da API Spring Boot (lavamais-api). Os nomes dos
// campos são em camelCase, exatamente como a API envia e recebe.
// ---------------------------------------------------------------------------

// ----- Autenticação e usuário -----

data class LoginRequest(val email: String, val senha: String) {
    // Construtor auxiliar para aceitar chamadas antigas que passam "login"
    constructor(login: String, senha: String, unused: Boolean = true) : this(email = login, senha = senha)
}

data class CadastroUsuarioRequest(
    val nome: String,
    val email: String,
    val senha: String,
    val telefone: String? = null,
    val cpf: String? = null
)

/**
 * senhaAtual só é exigida pela API ao trocar e-mail ou senha.
 * novaSenha = null mantém a senha atual (o Gson não envia campos null).
 */
data class AtualizarPerfilRequest(
    val nome: String,
    val email: String,
    val telefone: String? = null,
    val cpf: String? = null,
    @SerializedName("senhaAtual") val senhaAtual: String? = null,
    @SerializedName("novaSenha") val novaSenha: String? = null
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
data class PagamentoRequest(
    val utilizacaoId: Int,
    val metodo: String,
    val provedor: String? = "App",
    val referenciaExterna: String? = null
)

data class PagamentoResponse(
    val id: Int = 0,
    val utilizacaoId: Int = 0,
    val valor: Double = 0.0,
    val metodo: String = "",
    val estado: String = "",
    val criadoEm: String? = null,
    val pagoEm: String? = null
)

data class MensagemResponse(val mensagem: String? = null, val id: Int? = null)

// ---------------------------------------------------------------------------
// Modelos usados pelas telas. As respostas da API são convertidas para eles,
// assim as telas não precisam conhecer o formato do servidor.
// ---------------------------------------------------------------------------

data class UsuarioApi(
    val id: Int = 0,
    val nome: String = "",
    val email: String = "",
    val telefone: String? = null,
    val cpf: String? = null,
    val administrador: Boolean? = false,
    val tipo_usuario: String? = if (administrador == true) "admin" else "cliente"
)

data class AuthResponse(
    val mensagem: String? = null,
    val usuario: UsuarioApi? = null,
    val token: String? = null
)

data class MaquinaApi(
    val id: Int = 0,
    @SerializedName("nome") val identificacao: String = "",
    val descricao: String? = null,
    @SerializedName("capacidade") val capacidade_kg: Double = 0.0,
    val estado: String = "",
    val motivo_bloqueio: String? = null,
    val ativa: Boolean? = true
)

data class ServicoApi(
    val id: Int = 0,
    val nome: String = "",
    val tipo: String? = null,
    val descricao: String? = null,
    val preco: Double = 0.0,
    @SerializedName("duracaoEstimada") val duracao_estimada: Int = 0,
    val ativo: Boolean? = true
)

data class NovaUtilizacaoRequest(
    @SerializedName("maquinaId") val maquina_id: Int,
    @SerializedName("servicoId") val servico_id: Int
)

data class UtilizacaoApi(
    val id: Int = 0,
    val estado: String = "",
    @SerializedName("valorTotal") val valor_total: Double = 0.0,
    @SerializedName("criadoEm") val criado_em: String? = null,
    val maquina: String = "",
    val servico: String = "",
    @SerializedName("duracaoEstimada") val duracao_estimada: Int = 0,
    @SerializedName("inicioReal") val inicio_real: String? = null,
    @SerializedName("fimReal") val fim_real: String? = null,
    /** Segundos desde o início do ciclo, calculados pelo servidor (null se ainda não começou). */
    val decorrido_seg: Long? = null,
    @SerializedName("maquinaId") val maquinaId: Int? = null,
    @SerializedName("servicoId") val servicoId: Int? = null
)

// ----- Conversões API -> telas -----

fun UsuarioLogadoApi.paraUsuario() = UsuarioApi(id, nome, email, telefone, administrador = administrador)

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
        maquinaId = maquinaId,
        servicoId = servicoId
    )
}
