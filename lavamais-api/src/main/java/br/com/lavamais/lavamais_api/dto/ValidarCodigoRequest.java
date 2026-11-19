package br.com.lavamais.lavamais_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ValidarCodigoRequest(
    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    @Size(max = 60, message = "E-mail deve ter no máximo 60 caracteres")
    String email,

    @NotBlank(message = "Código é obrigatório")
    @Size(min = 6, max = 6, message = "Código deve ter exatamente 6 dígitos")
    String codigo
) {}
