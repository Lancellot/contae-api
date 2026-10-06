package br.com.contae.application.movimentacao;

import br.com.contae.api.movimentacao.dto.MovimentacaoRequestDTO;
import br.com.contae.domain.categoria.Categoria;
import br.com.contae.domain.conta.Conta;
import br.com.contae.domain.conta.TipoConta;
import br.com.contae.domain.exception.RecursoNaoEncontradoException;
import br.com.contae.domain.exception.SaldoInsuficienteException;
import br.com.contae.domain.movimentacao.TipoMovimentacao;
import br.com.contae.domain.movimentacao.Movimentacao;
import br.com.contae.domain.usuario.Usuario;
import br.com.contae.infrastructure.categoria.CategoriaRepository;
import br.com.contae.infrastructure.conta.ContaRepository;
import br.com.contae.infrastructure.movimentacao.MovimentacaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.lenient;

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
        Conta conta = new Conta(usuario, "Conta", TipoConta.CORRENTE, BigDecimal.valueOf(20));
        Categoria categoria = new Categoria(usuario, "Alimentacao");
        MovimentacaoService service = new MovimentacaoService(
                movimentacaoRepository, contaRepository, categoriaRepository);

        when(contaRepository.findLockedByIdAndUsuario_Email(10L, email)).thenReturn(Optional.of(conta));
        when(categoriaRepository.findByIdAndUsuario_Email(20L, email)).thenReturn(Optional.of(categoria));
        when(movimentacaoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        service.criar(request(10L, 20L), email);

        verify(contaRepository).findLockedByIdAndUsuario_Email(10L, email);
        verify(categoriaRepository).findByIdAndUsuario_Email(20L, email);
        assertEquals(BigDecimal.TEN, conta.getSaldo());
    }

    @Test
    void deveRejeitarContaQueNaoPertenceAoUsuarioAutenticado() {
        String email = "usuario@contae.com";
        MovimentacaoService service = new MovimentacaoService(
                movimentacaoRepository, contaRepository, categoriaRepository);

        when(contaRepository.findLockedByIdAndUsuario_Email(10L, email)).thenReturn(Optional.empty());

        RecursoNaoEncontradoException exception = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> service.criar(request(10L, 20L), email));

        assertEquals("Conta não encontrada", exception.getMessage());
    }

    @Test
    void deveReconciliarSaldoAoAlterarValor() {
        Conta conta = conta(BigDecimal.valueOf(90));
        Movimentacao movimentacao = movimentacao(conta, TipoMovimentacao.DESPESA, BigDecimal.TEN);
        MovimentacaoService service = prepararAtualizacao(movimentacao, conta);

        service.atualizar(1L, request(10L, 20L, BigDecimal.valueOf(20), TipoMovimentacao.DESPESA), "usuario@contae.com");

        assertEquals(BigDecimal.valueOf(80), conta.getSaldo());
    }

    @Test
    void deveReconciliarSaldoAoAlterarTipo() {
        Conta conta = conta(BigDecimal.valueOf(90));
        Movimentacao movimentacao = movimentacao(conta, TipoMovimentacao.DESPESA, BigDecimal.TEN);
        MovimentacaoService service = prepararAtualizacao(movimentacao, conta);

        service.atualizar(1L, request(10L, 20L, BigDecimal.TEN, TipoMovimentacao.RECEITA), "usuario@contae.com");

        assertEquals(BigDecimal.valueOf(110), conta.getSaldo());
    }

    @Test
    void deveTransferirEfeitoAoAlterarConta() {
        Conta contaAnterior = conta(BigDecimal.valueOf(90));
        Conta contaNova = conta(BigDecimal.valueOf(50));
        Movimentacao movimentacao = movimentacao(contaAnterior, TipoMovimentacao.DESPESA, BigDecimal.TEN);
        MovimentacaoService service = prepararAtualizacao(movimentacao, contaNova);

        service.atualizar(1L, request(11L, 20L, BigDecimal.valueOf(20), TipoMovimentacao.DESPESA), "usuario@contae.com");

        assertEquals(BigDecimal.valueOf(100), contaAnterior.getSaldo());
        assertEquals(BigDecimal.valueOf(30), contaNova.getSaldo());
    }

    @Test
    void deveReverterSaldoAoExcluirMovimentacao() {
        Conta conta = conta(BigDecimal.valueOf(90));
        Movimentacao movimentacao = movimentacao(conta, TipoMovimentacao.DESPESA, BigDecimal.TEN);
        when(movimentacaoRepository.findLockedByIdAndConta_Usuario_Email(1L, "usuario@contae.com"))
                .thenReturn(Optional.of(movimentacao));
        when(contaRepository.findLockedByIdAndUsuario_Email(any(), org.mockito.ArgumentMatchers.eq("usuario@contae.com")))
                .thenReturn(Optional.of(conta));
        MovimentacaoService service = new MovimentacaoService(
                movimentacaoRepository, contaRepository, categoriaRepository);

        service.deletar(1L, "usuario@contae.com");

        assertEquals(BigDecimal.valueOf(100), conta.getSaldo());
        verify(movimentacaoRepository).delete(movimentacao);
    }

    @Test
    void deveRejeitarEstornoDeReceitaQueDeixariaSaldoNegativo() {
        Conta conta = conta(BigDecimal.valueOf(5));
        Movimentacao movimentacao = movimentacao(conta, TipoMovimentacao.RECEITA, BigDecimal.TEN);
        MovimentacaoService service = prepararAtualizacao(movimentacao, conta);

        assertThrows(SaldoInsuficienteException.class, () -> service.atualizar(
                1L, request(10L, 20L, BigDecimal.valueOf(5), TipoMovimentacao.DESPESA), "usuario@contae.com"));

        assertEquals(BigDecimal.valueOf(5), conta.getSaldo());
        verify(movimentacaoRepository, never()).save(any());
    }

    private MovimentacaoService prepararAtualizacao(Movimentacao movimentacao, Conta contaDestino) {
        String email = "usuario@contae.com";
        Categoria categoria = new Categoria(Usuario.builder().id(1L).email(email).build(), "Categoria");
        when(movimentacaoRepository.findLockedByIdAndConta_Usuario_Email(1L, email)).thenReturn(Optional.of(movimentacao));
        when(contaRepository.findLockedByIdAndUsuario_Email(any(), org.mockito.ArgumentMatchers.eq(email)))
                .thenReturn(Optional.of(contaDestino));
        when(categoriaRepository.findByIdAndUsuario_Email(20L, email)).thenReturn(Optional.of(categoria));
        lenient().when(movimentacaoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        return new MovimentacaoService(movimentacaoRepository, contaRepository, categoriaRepository);
    }

    private Conta conta(BigDecimal saldo) {
        return new Conta(Usuario.builder().id(1L).email("usuario@contae.com").build(),
                "Conta", TipoConta.CORRENTE, saldo);
    }

    private Movimentacao movimentacao(Conta conta, TipoMovimentacao tipo, BigDecimal valor) {
        Categoria categoria = new Categoria(conta.getUsuario(), "Categoria");
        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setConta(conta);
        movimentacao.setCategoria(categoria);
        movimentacao.setTipoMovimentacao(tipo);
        movimentacao.setValor(valor);
        return movimentacao;
    }

    private MovimentacaoRequestDTO request(Long contaId, Long categoriaId) {
        return request(contaId, categoriaId, BigDecimal.TEN, TipoMovimentacao.DESPESA);
    }

    private MovimentacaoRequestDTO request(Long contaId, Long categoriaId, BigDecimal valor, TipoMovimentacao tipo) {
        return new MovimentacaoRequestDTO(
                contaId,
                categoriaId,
                "Compra",
                valor,
                tipo,
                null,
                LocalDate.of(2026, 1, 1),
                false
        );
    }
}
