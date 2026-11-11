package br.com.lavamais.lavamais_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SuporteRequest(
        @NotNull Integer usuarioId,
        @Size(max = 150) String tema,
        @NotBlank String descricao
) {}
