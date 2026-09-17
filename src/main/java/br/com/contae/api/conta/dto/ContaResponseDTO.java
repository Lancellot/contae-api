package br.com.contae.api.conta.dto;

import br.com.contae.domain.conta.TipoConta;

import java.math.BigDecimal;

public class ContaResponseDTO {

    private Long id;
    private String nome;
    private TipoConta tipo;
    private BigDecimal saldo;
    private Long usuarioId;

    public ContaResponseDTO() {
    }

    public ContaResponseDTO(Long id, String nome, TipoConta tipo, BigDecimal saldo, Long usuarioId) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.saldo = saldo;
        this.usuarioId = usuarioId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
}