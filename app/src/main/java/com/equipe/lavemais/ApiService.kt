package com.equipe.lavemais

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

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

    // ----- Suporte e notificações -----

    @GET("suportes")
    suspend fun suportes(): Response<List<SuporteResponse>>

    @POST("suportes")
    suspend fun abrirSuporte(@Body body: SuporteRequest): Response<SuporteResponse>

    @POST("suportes")
    suspend fun criarSuporte(@Body body: SuporteRequest): Response<SuporteResponse>

    @GET("notificacoes")
    suspend fun notificacoes(): Response<List<NotificacaoResponse>>

    @PATCH("notificacoes/{id}/lida")
    suspend fun marcarNotificacaoLida(@Path("id") id: Int): Response<NotificacaoResponse>

    // ----- Lavanderia -----

    @GET("lavanderia")
    suspend fun lavanderia(): Response<LavanderiaResponse>

    /** Só o administrador. */
    @PUT("lavanderia")
    suspend fun atualizarLavanderia(@Body body: LavanderiaRequest): Response<LavanderiaResponse>

    // ----- Máquinas e serviços -----

    @GET("maquinas")
    suspend fun maquinas(): Response<List<MaquinaResponse>>

    @GET("servicos")
    suspend fun servicos(): Response<List<ServicoResponse>>

    // ----- Administrador: máquinas e serviços (a API exige o papel de admin) -----

    @POST("maquinas")
    suspend fun criarMaquina(@Body body: MaquinaRequest): Response<MaquinaResponse>

    @PUT("maquinas/{id}")
    suspend fun atualizarMaquina(@Path("id") id: Int, @Body body: MaquinaRequest): Response<MaquinaResponse>

    @PATCH("maquinas/{id}/estado")
    suspend fun alterarEstadoMaquina(@Path("id") id: Int, @Body body: EstadoMaquinaRequest): Response<MaquinaResponse>

    /** A API só desativa a máquina: ela some da lista, mas o histórico fica. */
    @DELETE("maquinas/{id}")
    suspend fun excluirMaquina(@Path("id") id: Int): Response<Unit>

    @POST("servicos")
    suspend fun criarServico(@Body body: ServicoRequest): Response<ServicoResponse>

    @PUT("servicos/{id}")
    suspend fun atualizarServico(@Path("id") id: Int, @Body body: ServicoRequest): Response<ServicoResponse>

    @DELETE("servicos/{id}")
    suspend fun excluirServico(@Path("id") id: Int): Response<Unit>

    // ----- Utilizações -----

    @POST("utilizacoes")
    suspend fun criarUtilizacao(@Body body: UtilizacaoRequest): Response<UtilizacaoResponse>

    @GET("utilizacoes")
    suspend fun utilizacoes(): Response<List<UtilizacaoResponse>>

    /** Só funciona depois que o pagamento foi aprovado; ocupa a máquina. */
    @PATCH("utilizacoes/{id}/iniciar")
    suspend fun iniciarUtilizacao(@Path("id") id: Int): Response<UtilizacaoResponse>

    // ----- Pagamentos -----

    @GET("pagamentos")
    suspend fun buscarPagamentosPorUtilizacao(@Query("utilizacaoId") utilizacaoId: Int): Response<List<PagamentoResponse>>

    @POST("pagamentos")
    suspend fun criarPagamento(@Body body: PagamentoRequest): Response<PagamentoResponse>

    /** Aprovação de demonstração (exige lavamais.pagamento.simulacao-habilitada=true na API). */
    @POST("pagamentos/{id}/simular-aprovacao")
    suspend fun simularAprovacao(@Path("id") id: Int): Response<PagamentoResponse>
}
