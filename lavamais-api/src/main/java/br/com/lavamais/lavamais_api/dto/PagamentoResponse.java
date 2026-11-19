package br.com.lavamais.lavamais_api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.com.lavamais.lavamais_api.entity.EstadoPagamento;
import br.com.lavamais.lavamais_api.entity.MetodoPagamento;
import br.com.lavamais.lavamais_api.entity.Pagamento;

public record PagamentoResponse(
        Integer id,
        Integer utilizacaoId,
        BigDecimal valor,
        MetodoPagamento metodo,
        String provedor,
        String referenciaExterna,
        EstadoPagamento estado,
        LocalDateTime criadoEm,
        LocalDateTime pagoEm
) {
    public static PagamentoResponse de(Pagamento p) {
        return new PagamentoResponse(p.getId(), p.getUtilizacaoId(), p.getValor(),
                p.getMetodo(), p.getProvedor(), p.getReferenciaExterna(),
                p.getEstado(), p.getCriadoEm(), p.getPagoEm());
    }
}
