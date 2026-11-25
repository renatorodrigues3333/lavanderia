package com.equipe.lavemais

import androidx.compose.runtime.compositionLocalOf

/** Dados da lavanderia atual, disponíveis para todas as telas (preenchidos pelo AppNavigation). */
val LocalLavanderia = compositionLocalOf { LavanderiaResponse() }

private const val NOME_PADRAO = "Lave +"

/** Nome da unidade, ou o nome do app enquanto o administrador não cadastrar. */
fun LavanderiaResponse.nomeExibicao(): String = nome.ifBlank { NOME_PADRAO }

/** Texto do topo das telas: "Unidade Centro". */
fun LavanderiaResponse.tituloUnidade(): String =
    if (nome.isBlank()) NOME_PADRAO else "Unidade $nome"

fun LavanderiaResponse.enderecoExibicao(): String =
    endereco.ifBlank { "Endereço ainda não informado" }
