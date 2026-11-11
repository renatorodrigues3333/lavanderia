package br.com.lavamais.lavamais_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MensagemSuporteRequest(
        @NotNull Integer remetenteId,
        @NotBlank String conteudo
) {}
