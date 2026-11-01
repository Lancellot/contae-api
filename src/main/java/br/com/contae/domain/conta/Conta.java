package br.com.contae.domain.conta;

import br.com.contae.domain.usuario.Usuario;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "contas")
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 100)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoConta tipo;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal saldo;

    protected Conta() {
    }

    public Conta(Usuario usuario, String nome, TipoConta tipo, BigDecimal saldo) {
        this.usuario = usuario;
        this.nome = nome;
        this.tipo = tipo;
        this.saldo = saldo;
    }

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
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

    public void depositar(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor do depósito deve ser positivo");
        }
        this.saldo = this.saldo.add(valor);
    }

    public void sacar(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor do saque deve ser positivo");
        }
        if (valor.compareTo(this.saldo) > 0) {
            throw new IllegalArgumentException("Saldo insuficiente");
        }
        this.saldo = this.saldo.subtract(valor);
    }
}
