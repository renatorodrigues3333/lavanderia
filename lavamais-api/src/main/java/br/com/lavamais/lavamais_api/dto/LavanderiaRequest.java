package br.com.lavamais.lavamais_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LavanderiaRequest(
        @Size(max = 60) String nome,
        @NotBlank @Size(max = 200) String endereco
) {}
