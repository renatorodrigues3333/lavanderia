package br.com.lavamais.lavamais_api.dto;

import java.time.LocalDateTime;

import br.com.lavamais.lavamais_api.entity.Notificacao;
import br.com.lavamais.lavamais_api.entity.TipoNotificacao;

public record NotificacaoResponse(
        Integer id,
        Integer usuarioId,
        Integer utilizacaoId,
        TipoNotificacao tipo,
        String titulo,
        String mensagem,
        LocalDateTime lidoEm,
        LocalDateTime criadoEm
) {
    public static NotificacaoResponse de(Notificacao n) {
        return new NotificacaoResponse(n.getId(), n.getUsuarioId(), n.getUtilizacaoId(),
                n.getTipo(), n.getTitulo(), n.getMensagem(), n.getLidoEm(), n.getCriadoEm());
    }
}
