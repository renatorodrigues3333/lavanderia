package br.com.lavamais.lavamais_api.dto;

import br.com.lavamais.lavamais_api.entity.Usuario;

public record UsuarioResponse(Integer id, String nome, String email, String telefone) {

    public static UsuarioResponse de(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNome(), u.getEmail(), u.getTelefone());
    }
}
