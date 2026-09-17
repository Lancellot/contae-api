package br.com.contae.domain.categoria;
import br.com.contae.domain.usuario.Usuario;
import jakarta.persistence.*;

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

    // Nome da categoria.
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

    // Altera o nome da categoria.
    public void setNome(String nome) {
        this.nome = nome;
    }
}
