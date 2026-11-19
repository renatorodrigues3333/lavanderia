package br.com.lavamais.lavamais_api.dto;

public record LoginResponse(
        String token,
        String tipo,
        long expiraEmSegundos,
        UsuarioLogadoResponse usuario
) {}
