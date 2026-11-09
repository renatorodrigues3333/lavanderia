package com.equipe.lavemais

// Modelos que espelham o JSON da API Laravel

data class LoginRequest(val login: String, val senha: String)

data class RegisterRequest(
    val nome: String,
    val email: String,
    val telefone: String,
    val cpf: String,
    val senha: String
)

data class UsuarioApi(
    val id: Int = 0,
    val nome: String = "",
    val email: String = "",
    val telefone: String? = null,
    val cpf: String? = null,
    val tipo_usuario: String? = null
)

data class AuthResponse(
    val mensagem: String? = null,
    val usuario: UsuarioApi? = null,
    val token: String? = null
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

data class NovaUtilizacaoRequest(val maquina_id: Int, val servico_id: Int)

data class UtilizacaoApi(
    val id: Int = 0,
    val estado: String = "",
    val valor_total: Double = 0.0,
    val criado_em: String? = null,
    val maquina: String = "",
    val servico: String = "",
    val duracao_estimada: Int = 0
)

data class MensagemResponse(val mensagem: String? = null, val id: Int? = null)
