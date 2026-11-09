package com.equipe.lavemais

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.json.JSONObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    // Emulador do Android Studio: 10.0.2.2 aponta para o seu computador.
    // Celular físico: troque pelo IP do Mac na rede Wi-Fi,
    // por exemplo "http://192.168.0.10:8000/api/"
    const val BASE_URL = "http://10.0.2.2:8000/api/"

    @Volatile
    private var instancia: ApiService? = null

    fun service(context: Context): ApiService =
        instancia ?: synchronized(this) {
            instancia ?: criar(context.applicationContext).also { instancia = it }
        }

    private fun criar(context: Context): ApiService {
        val sessao = SessaoUsuario(context)

        val client = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(Interceptor { chain ->
                val pedido = chain.request().newBuilder()
                    .header("Accept", "application/json")
                val token = sessao.token
                if (token != null) {
                    pedido.header("Authorization", "Bearer $token")
                }
                chain.proceed(pedido.build())
            })
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    /** Extrai a mensagem de erro do JSON devolvido pelo Laravel. */
    fun mensagemErro(response: Response<*>): String {
        try {
            val corpo = response.errorBody()?.string()
            if (!corpo.isNullOrBlank()) {
                val json = JSONObject(corpo)

                val erros = json.optJSONObject("errors")
                if (erros != null && erros.keys().hasNext()) {
                    val lista = erros.getJSONArray(erros.keys().next())
                    if (lista.length() > 0) return lista.getString(0)
                }

                val mensagem = json.optString("mensagem")
                if (mensagem.isNotBlank()) return mensagem

                val message = json.optString("message")
                if (message.isNotBlank()) return message
            }
        } catch (_: Exception) {
            // cai na mensagem padrão abaixo
        }
        return "Erro ${response.code()} ao falar com o servidor."
    }

    fun mensagemFalha(): String =
        "Não foi possível conectar ao servidor. Confira se a API está rodando."
}
