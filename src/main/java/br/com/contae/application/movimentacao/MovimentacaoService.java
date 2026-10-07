package br.com.contae.application.movimentacao;

import br.com.contae.api.movimentacao.dto.MovimentacaoRequestDTO;
import br.com.contae.api.movimentacao.dto.MovimentacaoResponseDTO;
import br.com.contae.api.movimentacao.mapper.MovimentacaoMapper;
import br.com.contae.domain.categoria.Categoria;
import br.com.contae.domain.conta.Conta;
import br.com.contae.domain.exception.RecursoNaoEncontradoException;
import br.com.contae.domain.movimentacao.Movimentacao;
import br.com.contae.domain.movimentacao.TipoMovimentacao;
import br.com.contae.infrastructure.categoria.CategoriaRepository;
import br.com.contae.infrastructure.conta.ContaRepository;
import br.com.contae.infrastructure.movimentacao.MovimentacaoRepository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class MovimentacaoService {

    private final MovimentacaoRepository movimentacaoRepository;
    private final ContaRepository contaRepository;
    private final CategoriaRepository categoriaRepository;

    // Criar
    @Transactional
    public MovimentacaoResponseDTO criar(MovimentacaoRequestDTO dto, String email) {

        Conta conta = contaRepository.findLockedByIdAndUsuario_Email(dto.getContaId(), email)
                .orElseThrow(() -> recursoNaoEncontrado("Conta não encontrada"));

        Categoria categoria = categoriaRepository.findByIdAndUsuario_Email(dto.getCategoriaId(), email)
                .orElseThrow(() -> recursoNaoEncontrado("Categoria não encontrada"));

        Movimentacao movimentacao = MovimentacaoMapper.toEntity(
                dto,
                conta,
                categoria
        );

                if (dto.getTipoMovimentacao() == TipoMovimentacao.RECEITA) {
                        conta.depositar(dto.getValor());
                } else {
                        conta.sacar(dto.getValor());
                }

        Movimentacao salva = movimentacaoRepository.save(movimentacao);

        return MovimentacaoMapper.toResponseDTO(salva);
    }

    // Atualizar
    @Transactional
    public MovimentacaoResponseDTO atualizar(
            Long id,
            MovimentacaoRequestDTO dto,
            String email) {

        Movimentacao movimentacao = movimentacaoRepository.findLockedByIdAndConta_Usuario_Email(id, email)
                .orElseThrow(() -> recursoNaoEncontrado("Movimentação não encontrada"));

        Conta contaAnterior = movimentacao.getConta();
        TipoMovimentacao tipoAnterior = movimentacao.getTipoMovimentacao();
        BigDecimal valorAnterior = movimentacao.getValor();

        Map<Long, Conta> contasBloqueadas = bloquearContas(contaAnterior.getId(), dto.getContaId(), email);
        Conta conta = contasBloqueadas.get(dto.getContaId());

        Categoria categoria = categoriaRepository.findByIdAndUsuario_Email(dto.getCategoriaId(), email)
                .orElseThrow(() -> recursoNaoEncontrado("Categoria não encontrada"));

        MovimentacaoMapper.atualizar(
                movimentacao,
                dto,
                conta,
                categoria
        );

        aplicarEfeito(contaAnterior, tipoAnterior, valorAnterior, true);
        aplicarEfeito(conta, dto.getTipoMovimentacao(), dto.getValor(), false);

        Movimentacao atualizada = movimentacaoRepository.save(movimentacao);

        return MovimentacaoMapper.toResponseDTO(atualizada);
    }

    // Deletar
        @Transactional
        public void deletar(Long id, String email) {

                Movimentacao movimentacao = movimentacaoRepository.findLockedByIdAndConta_Usuario_Email(id, email)
                                .orElseThrow(() -> recursoNaoEncontrado("Movimentação não encontrada"));

        contaRepository.findLockedByIdAndUsuario_Email(movimentacao.getConta().getId(), email)
                .orElseThrow(() -> recursoNaoEncontrado("Conta não encontrada"));
        aplicarEfeito(movimentacao.getConta(), movimentacao.getTipoMovimentacao(), movimentacao.getValor(), true);
        movimentacaoRepository.delete(movimentacao);
    }

    // Buscar todas
        public List<MovimentacaoResponseDTO> buscarTodas(String email) {

                return movimentacaoRepository.findAllByConta_Usuario_Email(email)
                .stream()
                .map(MovimentacaoMapper::toResponseDTO)
                .toList();
    }

    // Buscar movimentações por data
        public List<MovimentacaoResponseDTO> buscarPorData(LocalDate data, String email) {

                return movimentacaoRepository.findByDataAndConta_Usuario_Email(data, email)
                .stream()
                .map(MovimentacaoMapper::toResponseDTO)
                .toList();
    }

    // Buscar movimentações por categoria
        public List<MovimentacaoResponseDTO> buscarPorCategoria(Long categoriaId, String email) {

                return movimentacaoRepository.findByCategoriaIdAndConta_Usuario_Email(categoriaId, email)
                .stream()
                .map(MovimentacaoMapper::toResponseDTO)
                .toList();
    }

    // Buscar movimentações por conta
        public List<MovimentacaoResponseDTO> buscarPorConta(Long contaId, String email) {

                return movimentacaoRepository.findByContaIdAndConta_Usuario_Email(contaId, email)
                .stream()
                .map(MovimentacaoMapper::toResponseDTO)
                .toList();
    }

        private void aplicarEfeito(Conta conta, TipoMovimentacao tipo, BigDecimal valor, boolean estornar) {
                boolean receita = tipo == TipoMovimentacao.RECEITA;
                if (receita != estornar) {
                        conta.depositar(valor);
                } else {
                        conta.sacar(valor);
                }
        }

        private Map<Long, Conta> bloquearContas(Long contaAnteriorId, Long contaNovaId, String email) {
                Map<Long, Conta> contas = new HashMap<>();
                java.util.Arrays.asList(contaAnteriorId, contaNovaId).stream()
                        .filter(java.util.Objects::nonNull)
                        .distinct()
                        .sorted()
                        .forEach(id ->
                        contas.put(id, contaRepository.findLockedByIdAndUsuario_Email(id, email)
                                .orElseThrow(() -> recursoNaoEncontrado("Conta não encontrada"))));
                return contas;
        }

        private RecursoNaoEncontradoException recursoNaoEncontrado(String mensagem) {
                return new RecursoNaoEncontradoException(mensagem);
        }
}

