package br.com.lavamais.lavamais_api.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ServicoRequest(
        @NotBlank @Size(max = 50) String nome,
        String descricao,
        @NotNull @DecimalMin("0.00") @Digits(integer = 4, fraction = 2) BigDecimal preco,
        @NotNull @Positive Integer duracaoEstimada
) {}
