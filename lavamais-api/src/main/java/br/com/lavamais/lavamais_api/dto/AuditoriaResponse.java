package br.com.lavamais.lavamais_api.dto;

import java.time.LocalDateTime;

import br.com.lavamais.lavamais_api.entity.Auditoria;

public record AuditoriaResponse(
        Integer id,
        Integer usuarioId,
        String acao,
        String entidade,
        Integer entidadeId,
        String detalhes,
        LocalDateTime criadoEm
) {
    public static AuditoriaResponse de(Auditoria a) {
        return new AuditoriaResponse(a.getId(), a.getUsuarioId(), a.getAcao(),
                a.getEntidade(), a.getEntidadeId(), a.getDetalhes(), a.getCriadoEm());
    }
}
