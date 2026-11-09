package com.equipe.lavemais

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("login")
    suspend fun login(@Body body: LoginRequest): Response<AuthResponse>

    @POST("register")
    suspend fun register(@Body body: RegisterRequest): Response<AuthResponse>

    @POST("logout")
    suspend fun logout(): Response<MensagemResponse>

    @GET("me")
    suspend fun me(): Response<UsuarioApi>

    @GET("maquinas")
    suspend fun maquinas(): Response<List<MaquinaApi>>

    @GET("servicos")
    suspend fun servicos(): Response<List<ServicoApi>>

    @POST("utilizacoes")
    suspend fun criarUtilizacao(@Body body: NovaUtilizacaoRequest): Response<MensagemResponse>

    @GET("utilizacoes")
    suspend fun utilizacoes(): Response<List<UtilizacaoApi>>
}
