package com.equipe.lavemais

import com.google.gson.annotations.SerializedName

// Modelos mapeados para os JSONs da API Spring Boot

data class LoginRequest(
    @SerializedName("email") val email: String,
    val senha: String
) {
    // Construtor auxiliar para aceitar chamadas antigas que passam "login"
    constructor(login: String, senha: String, unused: Boolean = true) : this(email = login, senha = senha)
}

data class RegisterRequest(
    val nome: String,
    val email: String,
    val telefone: String? = null,
    val cpf: String? = null,
    val senha: String
)

/** nova_senha = null mantém a senha atual (o Gson não envia campos null). */
data class AtualizarPerfilRequest(
    val nome: String,
    val email: String,
    val telefone: String? = null,
    val cpf: String? = null,
    @SerializedName("senhaAtual") val senha_atual: String? = null,
    @SerializedName("novaSenha") val nova_senha: String? = null
)

data class UsuarioApi(
    val id: Int = 0,
    val nome: String = "",
    val email: String = "",
    val telefone: String? = null,
    val cpf: String? = null,
    @SerializedName("administrador") val administrador: Boolean? = false,
    val tipo_usuario: String? = if (administrador == true) "admin" else "cliente"
)

data class AuthResponse(
    val mensagem: String? = null,
    val usuario: UsuarioApi? = null,
    val token: String? = null,
    @SerializedName("tipoToken") val tipoToken: String? = null,
    @SerializedName("expiracaoSegundos") val expiracaoSegundos: Long? = null
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

/** metodo: "pix", "cartao" ou "dinheiro" */
data class PagamentoRequest(
    @SerializedName("utilizacaoId") val utilizacao_id: Int = 0,
    val metodo: String = "pix",
    val provedor: String? = "App",
    @SerializedName("referenciaExterna") val referencia_externa: String? = null
) {
    // Construtor legada que aceitava apenas o método
    constructor(metodo: String) : this(utilizacao_id = 0, metodo = metodo)
}

data class PagamentoResponse(
    val id: Int = 0,
    @SerializedName("utilizacaoId") val utilizacaoId: Int = 0,
    val valor: Double = 0.0,
    val metodo: String = "",
    val estado: String = "",
    @SerializedName("criadoEm") val criadoEm: String? = null,
    @SerializedName("pagoEm") val pagoEm: String? = null
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

data class MensagemResponse(val mensagem: String? = null, val id: Int? = null)
