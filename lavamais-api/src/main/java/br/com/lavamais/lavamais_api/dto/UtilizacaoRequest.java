package br.com.lavamais.lavamais_api.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

/** usuarioId só vale para o administrador; para o cliente, a API usa quem está logado. */
public record UtilizacaoRequest(
        Integer usuarioId,
        @NotNull Integer maquinaId,
        @NotNull Integer servicoId,
        LocalDateTime inicioPrevisto
) {}
