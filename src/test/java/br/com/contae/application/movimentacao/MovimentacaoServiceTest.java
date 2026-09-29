package br.com.contae.application.movimentacao;

import br.com.contae.api.movimentacao.dto.MovimentacaoRequestDTO;
import br.com.contae.domain.categoria.Categoria;
import br.com.contae.domain.conta.Conta;
import br.com.contae.domain.conta.TipoConta;
import br.com.contae.domain.movimentacao.TipoMovimentacao;
import br.com.contae.domain.usuario.Usuario;
import br.com.contae.infrastructure.categoria.CategoriaRepository;
import br.com.contae.infrastructure.conta.ContaRepository;
import br.com.contae.infrastructure.movimentacao.MovimentacaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovimentacaoServiceTest {

    @Mock
    private MovimentacaoRepository movimentacaoRepository;

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Test
    void deveCriarMovimentacaoUsandoRecursosDoUsuarioAutenticado() {
        String email = "usuario@contae.com";
        Usuario usuario = Usuario.builder().id(1L).email(email).build();
        Conta conta = new Conta(usuario, "Conta", TipoConta.CORRENTE, BigDecimal.ZERO);
        Categoria categoria = new Categoria(usuario, "Alimentacao");
        MovimentacaoService service = new MovimentacaoService(
                movimentacaoRepository, contaRepository, categoriaRepository);

        when(contaRepository.findByIdAndUsuario_Email(10L, email)).thenReturn(Optional.of(conta));
        when(categoriaRepository.findByIdAndUsuario_Email(20L, email)).thenReturn(Optional.of(categoria));
        when(movimentacaoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        service.criar(request(10L, 20L), email);

        verify(contaRepository).findByIdAndUsuario_Email(10L, email);
        verify(categoriaRepository).findByIdAndUsuario_Email(20L, email);
    }

    @Test
    void deveRejeitarContaQueNaoPertenceAoUsuarioAutenticado() {
        String email = "usuario@contae.com";
        MovimentacaoService service = new MovimentacaoService(
                movimentacaoRepository, contaRepository, categoriaRepository);

        when(contaRepository.findByIdAndUsuario_Email(10L, email)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.criar(request(10L, 20L), email));

        assertEquals(404, exception.getStatusCode().value());
    }

    private MovimentacaoRequestDTO request(Long contaId, Long categoriaId) {
        return new MovimentacaoRequestDTO(
                contaId,
                categoriaId,
                "Compra",
                BigDecimal.TEN,
                TipoMovimentacao.DESPESA,
                null,
                LocalDate.of(2026, 1, 1),
                false
        );
    }
}