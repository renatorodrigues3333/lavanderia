package com.equipe.lavemais

import android.content.Context

/** Guarda o token e os dados básicos de quem está logado. */
class SessaoUsuario(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences("sessao_lavanderia", Context.MODE_PRIVATE)

    val token: String? get() = prefs.getString("token", null)
    val nome: String get() = prefs.getString("nome", "") ?: ""
    val email: String get() = prefs.getString("email", "") ?: ""
    val logado: Boolean get() = token != null

    fun salvar(token: String, nome: String, email: String) {
        prefs.edit()
            .putString("token", token)
            .putString("nome", nome)
            .putString("email", email)
            .apply()
    }

    fun limpar() {
        prefs.edit().clear().apply()
    }
}
