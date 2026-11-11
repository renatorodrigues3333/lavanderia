package br.com.lavamais.lavamais_api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.com.lavamais.lavamais_api.entity.EstadoUtilizacao;
import br.com.lavamais.lavamais_api.entity.Utilizacao;

public record UtilizacaoResponse(
        Integer id,
        Integer usuarioId,
        Integer maquinaId,
        Integer servicoId,
        EstadoUtilizacao estado,
        LocalDateTime inicioPrevisto,
        LocalDateTime inicioReal,
        LocalDateTime fimReal,
        BigDecimal valorTotal,
        LocalDateTime criadoEm
) {
    public static UtilizacaoResponse de(Utilizacao u) {
        return new UtilizacaoResponse(u.getId(), u.getUsuarioId(), u.getMaquinaId(),
                u.getServicoId(), u.getEstado(), u.getInicioPrevisto(), u.getInicioReal(),
                u.getFimReal(), u.getValorTotal(), u.getCriadoEm());
    }
}
