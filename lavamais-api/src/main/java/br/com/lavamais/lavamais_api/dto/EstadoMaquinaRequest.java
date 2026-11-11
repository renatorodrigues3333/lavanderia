package br.com.lavamais.lavamais_api.dto;

import br.com.lavamais.lavamais_api.entity.EstadoMaquina;
import jakarta.validation.constraints.NotNull;

public record EstadoMaquinaRequest(@NotNull EstadoMaquina estado) {}
