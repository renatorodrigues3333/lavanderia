package br.com.lavamais.lavamais_api.dto;

import java.time.LocalDateTime;

import br.com.lavamais.lavamais_api.entity.EstadoSuporte;
import br.com.lavamais.lavamais_api.entity.Suporte;
import br.com.lavamais.lavamais_api.entity.Usuario;

/** usuarioNome e usuarioEmail ficam nulos quando a conta de quem abriu o chamado já foi removida. */
public record SuporteResponse(
        Integer id,
        Integer usuarioId,
        String usuarioNome,
        String usuarioEmail,
        String tema,
        String descricao,
        EstadoSuporte estado,
        LocalDateTime criadoEm,
        LocalDateTime resolvidoEm
) {
    public static SuporteResponse de(Suporte s) {
        return de(s, null);
    }

    public static SuporteResponse de(Suporte s, Usuario u) {
        return new SuporteResponse(s.getId(), s.getUsuarioId(),
                u == null ? null : u.getNome(), u == null ? null : u.getEmail(),
                s.getTema(), s.getDescricao(), s.getEstado(), s.getCriadoEm(), s.getResolvidoEm());
    }
}
