package com.equipe.lavemais

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.LocalLaundryService
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.ui.graphics.vector.ImageVector
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// Funções de apoio para transformar os dados da API em textos das telas.

private val localeBr: Locale = Locale.forLanguageTag("pt-BR")

/** 32.0 -> "R$ 32,00" */
fun reais(valor: Double): String = String.format(localeBr, "R$ %.2f", valor)

/** 12.0 -> "12 kg", 7.5 -> "7,5 kg" */
fun kg(valor: Double): String =
    if (valor % 1.0 == 0.0) "${valor.toInt()} kg" else String.format(localeBr, "%.1f kg", valor)

/** 1048 -> "LLA-1048" */
fun codigoPedido(id: Int): String = String.format(Locale.US, "LLA-%04d", id)

/** "fora_de_servico" -> "Fora de servico" */
fun estadoTexto(estado: String): String =
    estado.replace('_', ' ').trim().replaceFirstChar { it.uppercase() }

fun MaquinaApi.status(): MachineStatus = when (estado) {
    "livre" -> MachineStatus.LIVRE
    "em_uso" -> MachineStatus.EM_USO
    else -> MachineStatus.FORA_SERVICO
}

/** Serviço sugerido quando o usuário ainda não escolheu: "Lavagem e secagem", se existir. */
fun servicoPadrao(servicos: List<ServicoApi>): ServicoApi? =
    servicos.firstOrNull { it.nome.contains("lavagem", true) && it.nome.contains("secagem", true) }
        ?: servicos.firstOrNull()

fun iconeServico(servico: ServicoApi?): ImageVector {
    val texto = "${servico?.nome} ${servico?.tipo}".lowercase()
    val lava = "lavagem" in texto
    val seca = "secagem" in texto
    return when {
        lava && seca -> Icons.Outlined.LocalLaundryService
        seca -> Icons.Outlined.Air
        lava -> Icons.Outlined.WaterDrop
        else -> Icons.Outlined.LocalLaundryService
    }
}

/**
 * Lê as datas enviadas pelo Laravel, por exemplo
 * "2026-10-18T12:12:00.000000Z", "2026-10-18T09:12:00-03:00" ou "2026-10-18 09:12:00".
 */
fun dataApi(texto: String?): Date? {
    if (texto.isNullOrBlank()) return null
    var t = texto.trim().replace(Regex("\\.\\d+"), "")
    val utc = t.endsWith("Z")
    t = t.removeSuffix("Z")

    val padroes = listOf("yyyy-MM-dd'T'HH:mm:ssXXX", "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss")
    for (padrao in padroes) {
        try {
            val formato = SimpleDateFormat(padrao, Locale.US)
            formato.isLenient = false
            if (utc) formato.timeZone = TimeZone.getTimeZone("UTC")
            return formato.parse(t)
        } catch (_: Exception) {
            // tenta o próximo formato
        }
    }
    return null
}

/** -> "18 out 2026 - 9h12" */
fun dataHistorico(data: Date): String =
    SimpleDateFormat("dd MMM yyyy - H'h'mm", localeBr).format(data).replace(".", "")

/** -> "Outubro de 2026" */
fun mesAno(data: Date): String =
    SimpleDateFormat("MMMM 'de' yyyy", localeBr).format(data).replaceFirstChar { it.uppercase() }

/** -> "Hoje, às 10:23" ou "19/10, às 10:23" */
fun horarioPrevisto(data: Date): String {
    val hoje = Calendar.getInstance()
    val alvo = Calendar.getInstance().apply { time = data }
    val hora = SimpleDateFormat("HH:mm", localeBr).format(data)
    val mesmoDia = hoje.get(Calendar.YEAR) == alvo.get(Calendar.YEAR) &&
        hoje.get(Calendar.DAY_OF_YEAR) == alvo.get(Calendar.DAY_OF_YEAR)
    return if (mesmoDia) "Hoje, às $hora" else "${SimpleDateFormat("dd/MM", localeBr).format(data)}, às $hora"
}

/** "ana.lima@exemplo.com" -> "ana.l***@exemplo.com" */
fun mascararEmail(email: String): String {
    val arroba = email.indexOf('@')
    if (arroba <= 0) return email
    val visivel = email.substring(0, minOf(5, arroba))
    return "$visivel***${email.substring(arroba)}"
}

/** "11998764820" -> "(11) 9****-4820" */
fun mascararTelefone(telefone: String?): String {
    val d = telefone.orEmpty().filter { it.isDigit() }
    if (d.length < 10) return if (d.isEmpty()) "—" else telefone.orEmpty()
    return "(${d.take(2)}) ${d[2]}****-${d.takeLast(4)}"
}

/** "12345678901" -> "***.***.***-01" */
fun mascararCpf(cpf: String?): String {
    val d = cpf.orEmpty().filter { it.isDigit() }
    return if (d.length >= 2) "***.***.***-${d.takeLast(2)}" else "***.***.***-**"
}
