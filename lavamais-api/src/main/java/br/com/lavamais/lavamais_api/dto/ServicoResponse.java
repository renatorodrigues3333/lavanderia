package br.com.lavamais.lavamais_api.dto;

import java.math.BigDecimal;

import br.com.lavamais.lavamais_api.entity.Servico;

public record ServicoResponse(
        Integer id,
        String nome,
        String descricao,
        BigDecimal preco,
        Integer duracaoEstimada,
        Boolean ativo
) {
    public static ServicoResponse de(Servico s) {
        return new ServicoResponse(s.getId(), s.getNome(), s.getDescricao(),
                s.getPreco(), s.getDuracaoEstimada(), s.getAtivo());
    }
}
