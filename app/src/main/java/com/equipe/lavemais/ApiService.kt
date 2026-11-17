package com.equipe.lavemais

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): Response<AuthResponse>

    @POST("usuarios")
    suspend fun register(@Body body: RegisterRequest): Response<UsuarioApi>

    @POST("logout")
    suspend fun logout(): Response<MensagemResponse>

    @GET("auth/me")
    suspend fun me(): Response<UsuarioApi>

    @PUT("usuarios/me")
    suspend fun atualizarPerfil(@Body body: AtualizarPerfilRequest): Response<UsuarioApi>

    @GET("maquinas")
    suspend fun maquinas(): Response<List<MaquinaApi>>

    @GET("servicos")
    suspend fun servicos(): Response<List<ServicoApi>>

    @POST("utilizacoes")
    suspend fun criarUtilizacao(@Body body: NovaUtilizacaoRequest): Response<UtilizacaoApi>

    @GET("utilizacoes")
    suspend fun utilizacoes(): Response<List<UtilizacaoApi>>

    @POST("pagamentos")
    suspend fun criarPagamento(@Body body: PagamentoRequest): Response<PagamentoResponse>

    @PATCH("utilizacoes/{id}/iniciar")
    suspend fun iniciarUtilizacao(@Path("id") id: Int): Response<UtilizacaoApi>
}
