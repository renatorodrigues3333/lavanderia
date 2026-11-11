package br.com.lavamais.lavamais_api.dto;

import java.math.BigDecimal;

import br.com.lavamais.lavamais_api.entity.EstadoMaquina;
import br.com.lavamais.lavamais_api.entity.Maquina;

public record MaquinaResponse(
        Integer id,
        String nome,
        BigDecimal capacidade,
        String descricao,
        EstadoMaquina estado,
        Boolean ativa
) {
    public static MaquinaResponse de(Maquina m) {
        return new MaquinaResponse(m.getId(), m.getNome(), m.getCapacidade(),
                m.getDescricao(), m.getEstado(), m.getAtiva());
    }
}
