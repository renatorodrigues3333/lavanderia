package br.com.lavamais.lavamais_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CadastroUsuarioRequest(
        @NotBlank @Size(max = 100) String nome,
        @NotBlank @Email @Size(max = 60) String email,
        @NotBlank @Size(min = 8, max = 64) String senha,
        @Size(max = 15) String telefone
) {}
