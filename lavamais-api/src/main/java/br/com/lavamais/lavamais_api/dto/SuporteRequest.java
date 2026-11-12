package br.com.lavamais.lavamais_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SuporteRequest(
        @Size(max = 150) String tema,
        @NotBlank String descricao
) {}
