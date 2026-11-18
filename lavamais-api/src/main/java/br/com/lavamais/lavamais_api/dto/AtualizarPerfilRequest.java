package br.com.lavamais.lavamais_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AtualizarPerfilRequest(
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    String nome,

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    @Size(max = 60, message = "E-mail deve ter no máximo 60 caracteres")
    String email,

    @Size(max = 15, message = "Telefone deve ter no máximo 15 caracteres")
    String telefone,

    String senhaAtual,

    @Size(max = 64, message = "A nova senha deve ter no máximo 64 caracteres")
    String novaSenha
) {}
