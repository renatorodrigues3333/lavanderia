package br.com.lavamais.lavamais_api.dto;

import jakarta.validation.constraints.NotBlank;

public record MensagemSuporteRequest(@NotBlank String conteudo) {}
