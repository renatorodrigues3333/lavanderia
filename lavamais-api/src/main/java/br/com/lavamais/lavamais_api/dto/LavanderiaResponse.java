package br.com.lavamais.lavamais_api.dto;

import br.com.lavamais.lavamais_api.entity.Lavanderia;

public record LavanderiaResponse(String nome, String endereco) {

    public static LavanderiaResponse de(Lavanderia l) {
        return new LavanderiaResponse(l.getNome(), l.getEndereco());
    }
}
