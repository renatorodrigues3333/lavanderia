package com.equipe.lavemais

// Dados usados apenas como valor padrão das telas (Preview do Android Studio).
// No app, os valores reais vêm da API.

val maquinasExemplo = listOf(
    MaquinaApi(1, "Máquina 01", capacidade_kg = 12.0, estado = "livre"),
    MaquinaApi(2, "Máquina 02", capacidade_kg = 10.0, estado = "livre"),
    MaquinaApi(3, "Máquina 03", capacidade_kg = 12.0, estado = "livre"),
    MaquinaApi(4, "Máquina 04", capacidade_kg = 17.0, estado = "em_uso"),
    MaquinaApi(5, "Máquina 05", capacidade_kg = 10.0, estado = "em_uso"),
    MaquinaApi(6, "Máquina 06", capacidade_kg = 10.0, estado = "manutencao")
)

val servicosExemplo = listOf(
    ServicoApi(1, "Lavagem", "lavagem", preco = 18.0, duracao_estimada = 35),
    ServicoApi(2, "Secagem", "secagem", preco = 14.0, duracao_estimada = 35),
    ServicoApi(3, "Lavagem e secagem", "lavagem_secagem", preco = 32.0, duracao_estimada = 70)
)

val pedidosExemplo = listOf(
    UtilizacaoApi(1048, "em_andamento", 32.0, "2026-10-18 09:12:00", "Máquina 01", "Lavagem e secagem", 70),
    UtilizacaoApi(1026, "concluida", 32.0, "2026-10-14 15:20:00", "Máquina 04", "Lavagem e secagem", 70),
    UtilizacaoApi(992, "concluida", 18.0, "2026-10-07 08:10:00", "Máquina 02", "Lavagem", 35),
    UtilizacaoApi(831, "cancelada", 14.0, "2026-10-02 10:10:00", "Máquina 03", "Secagem", 35)
)
