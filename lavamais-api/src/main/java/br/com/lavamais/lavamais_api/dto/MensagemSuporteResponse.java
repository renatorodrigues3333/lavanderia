package br.com.lavamais.lavamais_api.dto;

import java.time.LocalDateTime;

import br.com.lavamais.lavamais_api.entity.MensagemSuporte;

public record MensagemSuporteResponse(
        Integer id,
        Integer suporteId,
        Integer remetenteId,
        String conteudo,
        LocalDateTime enviadaEm,
        LocalDateTime lidaEm
) {
    public static MensagemSuporteResponse de(MensagemSuporte m) {
        return new MensagemSuporteResponse(m.getId(), m.getSuporteId(), m.getRemetenteId(),
                m.getConteudo(), m.getEnviadaEm(), m.getLidaEm());
    }
}
