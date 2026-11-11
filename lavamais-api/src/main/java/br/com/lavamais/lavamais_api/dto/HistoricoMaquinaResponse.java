package br.com.lavamais.lavamais_api.dto;

import java.time.LocalDateTime;

import br.com.lavamais.lavamais_api.entity.HistoricoMaquina;

public record HistoricoMaquinaResponse(
        Integer id,
        Integer maquinaId,
        Integer usuarioId,
        Integer utilizacaoId,
        String tipoEvento,
        String estadoAnterior,
        String estadoNovo,
        String descricao,
        LocalDateTime criadoEm
) {
    public static HistoricoMaquinaResponse de(HistoricoMaquina h) {
        return new HistoricoMaquinaResponse(h.getId(), h.getMaquinaId(), h.getUsuarioId(),
                h.getUtilizacaoId(), h.getTipoEvento(), h.getEstadoAnterior(),
                h.getEstadoNovo(), h.getDescricao(), h.getCriadoEm());
    }
}
