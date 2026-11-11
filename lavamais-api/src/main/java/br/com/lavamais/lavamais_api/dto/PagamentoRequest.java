package br.com.lavamais.lavamais_api.dto;

import br.com.lavamais.lavamais_api.entity.MetodoPagamento;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PagamentoRequest(
        @NotNull Integer utilizacaoId,
        @NotNull MetodoPagamento metodo,
        @Size(max = 35) String provedor,
        @Size(max = 255) String referenciaExterna
) {}
