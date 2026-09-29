package br.com.contae.domain.categoria;
import br.com.contae.domain.usuario.Usuario;
import jakarta.persistence.*;
import lombok.Setter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import br.com.contae.domain.usuario.Usuario;

@Entity
@Table(name = "categorias")
public class Categoria {

    // Identificador unico da categoria.
    // O banco de dados gera o ID automaticamente.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // cria a coluna usuario_id na tabela categorias.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Altera o nome da categoria.
    // Nome da categoria.
    @Setter
    @Column(nullable = false, length = 100)
    private String nome;

    // Construtor vazio exigido pelo JPA/Hibernate.
    protected Categoria() {
    }

    // Construtor utilizado para criar uma categoria.
    public Categoria(Usuario usuario, String nome) {
        this.usuario = usuario;
        this.nome = nome;
    }

    // Retorna o ID da categoria.
    public Long getId() {
        return id;
    }

    // Retorna o usuario dono da categoria.
    public Usuario getUsuario() {
        return usuario;
    }

    // Retorna o nome da categoria.
    public String getNome() {
        return nome;
    }

}
