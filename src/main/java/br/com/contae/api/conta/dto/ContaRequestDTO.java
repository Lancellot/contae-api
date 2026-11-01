package br.com.contae.api.conta.dto;

import br.com.contae.domain.conta.TipoConta;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class ContaRequestDTO {

    @NotBlank(message = "O nome da conta é obrigatório")
    @Size(max = 100, message = "O nome da conta deve ter no máximo 100 caracteres")
    private String nome;

    @NotNull(message = "O tipo da conta é obrigatório")
    private TipoConta tipo;

    @NotNull(message = "O saldo é obrigatório")
    @DecimalMin(value = "0.0", inclusive = true, message = "O saldo não pode ser negativo")
    private BigDecimal saldo;

    public ContaRequestDTO() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public TipoConta getTipo() {
        return tipo;
    }

    public void setTipo(TipoConta tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }
}