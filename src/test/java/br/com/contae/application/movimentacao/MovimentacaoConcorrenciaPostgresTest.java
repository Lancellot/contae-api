package br.com.contae.application.movimentacao;

import br.com.contae.api.movimentacao.dto.MovimentacaoRequestDTO;
import br.com.contae.domain.categoria.Categoria;
import br.com.contae.domain.conta.Conta;
import br.com.contae.domain.conta.TipoConta;
import br.com.contae.domain.movimentacao.Movimentacao;
import br.com.contae.domain.movimentacao.TipoMovimentacao;
import br.com.contae.domain.usuario.Usuario;
import br.com.contae.infrastructure.categoria.CategoriaRepository;
import br.com.contae.infrastructure.conta.ContaRepository;
import br.com.contae.infrastructure.movimentacao.MovimentacaoRepository;
import br.com.contae.infrastructure.usuario.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers(disabledWithoutDocker = true)
class MovimentacaoConcorrenciaPostgresTest {

    @Container
    private static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:16-alpine");

    @DynamicPropertySource
    static void configurarBanco(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired
    private MovimentacaoService movimentacaoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private MovimentacaoRepository movimentacaoRepository;

    @Test
    void movimentosConcorrentesMantemSaldoIgualAoTotalPersistido() throws Exception {
        String email = UUID.randomUUID() + "@contae.com";
        Usuario usuario = usuarioRepository.save(Usuario.builder()
                .nome("Teste")
                .email(email)
                .senha("senha")
                .ativo(true)
                .build());
        Conta conta = contaRepository.save(new Conta(usuario, "Conta", TipoConta.CORRENTE, BigDecimal.ZERO));
        Categoria categoria = categoriaRepository.save(new Categoria(usuario, "Categoria"));
        int quantidade = 12;
        CountDownLatch inicio = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(quantidade);
        try {
            List<Future<Object>> tarefas = java.util.stream.IntStream.range(0, quantidade)
                    .mapToObj(i -> executor.submit(() -> {
                        inicio.await();
                        movimentacaoService.criar(new MovimentacaoRequestDTO(
                                conta.getId(), categoria.getId(), "Receita", BigDecimal.TEN,
                                TipoMovimentacao.RECEITA, null, LocalDate.now(), false), email);
                        return null;
                    }))
                    .toList();
            inicio.countDown();
            for (Future<?> tarefa : tarefas) {
                tarefa.get();
            }
        } finally {
            executor.shutdownNow();
        }

        List<Movimentacao> movimentos = movimentacaoRepository.findAllByConta_Usuario_Email(email);
        BigDecimal totalMovimentos = movimentos.stream()
                .map(Movimentacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Conta contaPersistida = contaRepository.findByIdAndUsuario_Email(conta.getId(), email).orElseThrow();

        assertEquals(quantidade, movimentos.size());
        assertEquals(0, totalMovimentos.compareTo(contaPersistida.getSaldo()));
    }
}
