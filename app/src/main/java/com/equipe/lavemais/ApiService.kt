package com.equipe.lavemais

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/** Rotas da lavamais-api (Spring Boot). A base é http://HOST:8080/api/ */
interface ApiService {

    // ----- Autenticação e usuário -----

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>

    @POST("usuarios")
    suspend fun register(@Body body: CadastroUsuarioRequest): Response<UsuarioResponse>

    @GET("auth/me")
    suspend fun me(): Response<UsuarioLogadoApi>

    @PUT("usuarios/me")
    suspend fun atualizarPerfil(@Body body: AtualizarPerfilRequest): Response<UsuarioResponse>

    // ----- Máquinas e serviços -----

    @GET("maquinas")
    suspend fun maquinas(): Response<List<MaquinaResponse>>

    @GET("servicos")
    suspend fun servicos(): Response<List<ServicoResponse>>

    // ----- Utilizações -----

    @POST("utilizacoes")
    suspend fun criarUtilizacao(@Body body: UtilizacaoRequest): Response<UtilizacaoResponse>

    @GET("utilizacoes")
    suspend fun utilizacoes(): Response<List<UtilizacaoResponse>>

    /** Só funciona depois que o pagamento foi aprovado; ocupa a máquina. */
    @PATCH("utilizacoes/{id}/iniciar")
    suspend fun iniciarUtilizacao(@Path("id") id: Int): Response<UtilizacaoResponse>

    // ----- Pagamentos -----

    @POST("pagamentos")
    suspend fun criarPagamento(@Body body: PagamentoRequest): Response<PagamentoResponse>

    /** Aprovação de demonstração (exige lavamais.pagamento.simulacao-habilitada=true na API). */
    @POST("pagamentos/{id}/simular-aprovacao")
    suspend fun simularAprovacao(@Path("id") id: Int): Response<PagamentoResponse>
}
