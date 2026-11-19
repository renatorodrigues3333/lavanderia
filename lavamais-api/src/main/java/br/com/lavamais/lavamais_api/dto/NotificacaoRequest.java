package br.com.lavamais.lavamais_api.dto;

import br.com.lavamais.lavamais_api.entity.TipoNotificacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NotificacaoRequest(
        Integer usuarioId,
        Integer utilizacaoId,
        @NotNull TipoNotificacao tipo,
        @NotBlank @Size(max = 150) String titulo,
        @NotBlank String mensagem
) {}
