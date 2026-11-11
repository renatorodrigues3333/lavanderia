package br.com.lavamais.lavamais_api.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

public record UtilizacaoRequest(
        @NotNull Integer usuarioId,
        @NotNull Integer maquinaId,
        @NotNull Integer servicoId,
        LocalDateTime inicioPrevisto
) {}
