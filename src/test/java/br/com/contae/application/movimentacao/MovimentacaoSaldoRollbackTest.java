package br.com.contae.application.movimentacao;

import br.com.contae.api.movimentacao.dto.MovimentacaoRequestDTO;
import br.com.contae.domain.categoria.Categoria;
import br.com.contae.domain.conta.Conta;
import br.com.contae.domain.conta.TipoConta;
import br.com.contae.domain.movimentacao.TipoMovimentacao;
import br.com.contae.domain.usuario.Usuario;
import br.com.contae.infrastructure.categoria.CategoriaRepository;
import br.com.contae.infrastructure.conta.ContaRepository;
import br.com.contae.infrastructure.usuario.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class MovimentacaoSaldoRollbackTest {

    @Autowired
    private MovimentacaoService movimentacaoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Test
    void falhaAoSalvarMovimentacaoDeveReverterAlteracaoDoSaldo() {
        String email = UUID.randomUUID() + "@contae.com";
        Usuario usuario = usuarioRepository.save(Usuario.builder()
                .nome("Teste")
                .email(email)
                .senha("senha")
                .ativo(true)
                .build());
        Conta conta = contaRepository.save(new Conta(usuario, "Conta", TipoConta.CORRENTE, BigDecimal.valueOf(100)));
        Categoria categoria = categoriaRepository.save(new Categoria(usuario, "Categoria"));
        String descricaoLonga = "x".repeat(256);
        MovimentacaoRequestDTO dto = new MovimentacaoRequestDTO(
                conta.getId(), categoria.getId(), descricaoLonga, BigDecimal.TEN,
                TipoMovimentacao.DESPESA, null, LocalDate.now(), false);

        assertThrows(DataIntegrityViolationException.class, () -> movimentacaoService.criar(dto, email));

        Conta contaPersistida = contaRepository.findByIdAndUsuario_Email(conta.getId(), email).orElseThrow();
        assertEquals(0, BigDecimal.valueOf(100).compareTo(contaPersistida.getSaldo()));
    }
}
