package br.com.lavamais.lavamais_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RedefinirSenhaRequest(
    @NotBlank(message = "Token de redefinição é obrigatório")
    String tokenRedefinicao,

    @NotBlank(message = "Nova senha é obrigatória")
    @Size(min = 6, max = 64, message = "A nova senha deve ter entre 6 e 64 caracteres")
    String novaSenha
) {}
