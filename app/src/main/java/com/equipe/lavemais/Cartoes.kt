package com.equipe.lavemais

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import java.util.Calendar

const val TIPO_CREDITO = "credito"
const val TIPO_DEBITO = "debito"

/**
 * Cartão "cadastrado" na simulação de pagamento. Guarda só dados mascarados:
 * o número completo e o CVV nunca são salvos, nem neste aparelho.
 */
data class CartaoSalvo(
    val bandeira: String,
    val finalCartao: String,
    val titular: String,
    val validade: String
) {
    /** "Visa •••• 4242" */
    fun resumo(): String = "$bandeira •••• $finalCartao"
}

/** Cartões simulados e a forma de pagamento de cada pedido, separados por usuário. */
class ArmazenamentoCartoes(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences("cartoes_simulados", Context.MODE_PRIVATE)
    private val sessao = SessaoUsuario(context)

    private fun chave(sufixo: String) = "${sessao.email.lowercase()}|$sufixo"

    fun cartao(tipo: String): CartaoSalvo? {
        val partes = prefs.getString(chave("cartao_$tipo"), null)?.split("|") ?: return null
        if (partes.size != 4) return null
        return CartaoSalvo(partes[0], partes[1], partes[2], partes[3])
    }

    fun salvar(tipo: String, cartao: CartaoSalvo) {
        val texto = listOf(cartao.bandeira, cartao.finalCartao, cartao.titular, cartao.validade)
            .joinToString("|") { it.replace("|", " ") }
        prefs.edit().putString(chave("cartao_$tipo"), texto).apply()
    }

    fun remover(tipo: String) {
        prefs.edit().remove(chave("cartao_$tipo")).apply()
    }

    /** Ex.: "Cartão de crédito Visa •••• 4242", mostrado no comprovante do pedido. */
    fun guardarForma(pedidoId: Int, descricao: String) {
        prefs.edit().putString(chave("forma_$pedidoId"), descricao).apply()
    }

    fun forma(pedidoId: Int): String? = prefs.getString(chave("forma_$pedidoId"), null)
}

class CartoesViewModel(app: Application) : AndroidViewModel(app) {

    private val armazenamento = ArmazenamentoCartoes(app)

    var credito by mutableStateOf<CartaoSalvo?>(null)
        private set

    var debito by mutableStateOf<CartaoSalvo?>(null)
        private set

    /** Lê os cartões de quem está logado agora. */
    fun carregar() {
        credito = armazenamento.cartao(TIPO_CREDITO)
        debito = armazenamento.cartao(TIPO_DEBITO)
    }

    fun salvar(tipo: String, cartao: CartaoSalvo) {
        armazenamento.salvar(tipo, cartao)
        carregar()
    }

    fun remover(tipo: String) {
        armazenamento.remover(tipo)
        carregar()
    }

    fun limpar() {
        credito = null
        debito = null
    }
}

// ----- Validação e formatação do formulário de cartão -----

fun somenteDigitos(texto: String): String = texto.filter { it.isDigit() }

/** 4242424242424242 -> "4242 4242 4242 4242" */
fun formatarNumeroCartao(digitos: String): String = digitos.take(19).chunked(4).joinToString(" ")

/** 1230 -> "12/30" */
fun formatarValidade(digitos: String): String =
    if (digitos.length > 2) "${digitos.take(2)}/${digitos.drop(2).take(2)}" else digitos

/** Confere o tamanho e o dígito verificador (algoritmo de Luhn). */
fun numeroCartaoValido(digitos: String): Boolean {
    if (digitos.length !in 13..19) return false
    var soma = 0
    var dobrar = false
    for (i in digitos.length - 1 downTo 0) {
        var n = digitos[i] - '0'
        if (dobrar) {
            n *= 2
            if (n > 9) n -= 9
        }
        soma += n
        dobrar = !dobrar
    }
    return soma % 10 == 0
}

fun bandeiraDoCartao(digitos: String): String = when {
    digitos.startsWith("4") -> "Visa"
    digitos.length >= 2 && digitos.take(2).toInt() in 51..55 -> "Mastercard"
    digitos.length >= 4 && digitos.take(4).toInt() in 2221..2720 -> "Mastercard"
    digitos.startsWith("34") || digitos.startsWith("37") -> "Amex"
    else -> "Cartão"
}

/** "MM/AA" com mês de 01 a 12 e que ainda não venceu. */
fun validadeValida(texto: String): Boolean {
    val d = somenteDigitos(texto)
    if (d.length != 4) return false
    val mes = d.substring(0, 2).toInt()
    val ano = 2000 + d.substring(2).toInt()
    if (mes !in 1..12) return false

    val agora = Calendar.getInstance()
    val anoAtual = agora.get(Calendar.YEAR)
    val mesAtual = agora.get(Calendar.MONTH) + 1
    return ano > anoAtual || (ano == anoAtual && mes >= mesAtual)
}
