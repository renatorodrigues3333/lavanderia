package br.com.lavamais.lavamais_api.dto;

import br.com.lavamais.lavamais_api.entity.EstadoSuporte;
import jakarta.validation.constraints.NotNull;

public record EstadoSuporteRequest(@NotNull EstadoSuporte estado) {}
