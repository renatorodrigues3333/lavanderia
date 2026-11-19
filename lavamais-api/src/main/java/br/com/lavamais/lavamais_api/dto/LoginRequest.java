package br.com.lavamais.lavamais_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank @Size(max = 60) String email,
        @NotBlank @Size(max = 64) String senha
) {}
