package br.com.lavamais.lavamais_api.dto;

import java.time.LocalDateTime;

import br.com.lavamais.lavamais_api.entity.EstadoSuporte;
import br.com.lavamais.lavamais_api.entity.Suporte;

public record SuporteResponse(
        Integer id,
        Integer usuarioId,
        String tema,
        String descricao,
        EstadoSuporte estado,
        LocalDateTime criadoEm,
        LocalDateTime resolvidoEm
) {
    public static SuporteResponse de(Suporte s) {
        return new SuporteResponse(s.getId(), s.getUsuarioId(), s.getTema(),
                s.getDescricao(), s.getEstado(), s.getCriadoEm(), s.getResolvidoEm());
    }
}
