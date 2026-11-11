package br.com.lavamais.lavamais_api.dto;

import br.com.lavamais.lavamais_api.entity.EstadoPagamento;
import jakarta.validation.constraints.NotNull;

public record EstadoPagamentoRequest(@NotNull EstadoPagamento estado) {}
