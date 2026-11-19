package br.com.lavamais.lavamais_api.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MaquinaRequest(
        @NotBlank @Size(max = 30) String nome,
        @NotNull @DecimalMin("0.01") @Digits(integer = 3, fraction = 2) BigDecimal capacidade,
        String descricao
) {}
