package br.com.contae.application.movimentacao;

import br.com.contae.api.movimentacao.dto.MovimentacaoRequestDTO;
import br.com.contae.api.movimentacao.dto.MovimentacaoResponseDTO;
import br.com.contae.api.movimentacao.mapper.MovimentacaoMapper;
import br.com.contae.application.conta.ContaService;
import br.com.contae.domain.categoria.Categoria;
import br.com.contae.domain.conta.Conta;
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
import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class MovimentacaoService {

    private final MovimentacaoRepository movimentacaoRepository;
    private final ContaRepository contaRepository;
    private final CategoriaRepository categoriaRepository;
    private final ContaService contaService;

    @Transactional
    public MovimentacaoResponseDTO criar(MovimentacaoRequestDTO dto) {

        Conta conta = contaRepository.findById(dto.getContaId())
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));

        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        // 1. Guarda o saldo original da conta antes de mexer
        BigDecimal saldoOriginal = conta.getSaldo();

        Movimentacao movimentacao = MovimentacaoMapper.toEntity(
                dto,
                conta,
                categoria
        );

        movimentacao.setSaldoOriginal(saldoOriginal);

        // 2. Aplica a operação na conta
        if ("DESPESA".equalsIgnoreCase(String.valueOf(dto.getTipoMovimentacao()))) {
            conta.sacar(dto.getValor());
        } else if ("RECEITA".equalsIgnoreCase(String.valueOf(dto.getTipoMovimentacao()))) {
            conta.depositar(dto.getValor());
        }

        contaRepository.saveAndFlush(conta);

        // 3. Guarda o saldo de quanto a conta ficou após a movimentação
        movimentacao.setSaldoApos(conta.getSaldo());

        if (movimentacao.getData() == null) {
            movimentacao.setData(LocalDate.now());
        }

        Movimentacao salva = movimentacaoRepository.save(movimentacao);

        return MovimentacaoMapper.toResponseDTO(salva, saldoOriginal);
    }

    // Atualizar
    @Transactional
    public MovimentacaoResponseDTO atualizar(Long id, MovimentacaoRequestDTO dto) {

        Movimentacao movimentacao = movimentacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimentação não encontrada"));

        // Guarda o estado ANTIGO para estorno
        Conta contaAntiga = movimentacao.getConta();
        BigDecimal valorAntigo = movimentacao.getValor();
        TipoMovimentacao tipoAntigo = movimentacao.getTipoMovimentacao();

        // Busca a NOVA conta e Categoria
        Conta novaConta = contaRepository.findById(dto.getContaId())
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));

        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        // 1. Estorna o efeito da movimentação antiga na conta antiga
        if (tipoAntigo != null) {
            String tipoAntigoStr = String.valueOf(tipoAntigo);
            if ("DESPESA".equalsIgnoreCase(tipoAntigoStr)) {
                contaAntiga.depositar(valorAntigo);
            } else if ("RECEITA".equalsIgnoreCase(tipoAntigoStr)) {
                contaAntiga.sacar(valorAntigo);
            }
            contaRepository.saveAndFlush(contaAntiga);
        }

        // 2. Captura o novo saldo original da conta de destino antes de aplicar a nova movimentação
        BigDecimal saldoOriginalNovo = novaConta.getSaldo();
        movimentacao.setSaldoOriginal(saldoOriginalNovo);

        // 3. Atualiza os dados na entidade usando o Mapper
        MovimentacaoMapper.atualizar(
                movimentacao,
                dto,
                novaConta,
                categoria
        );

        // 4. Aplica o efeito da nova movimentação na nova conta
        TipoMovimentacao tipoNovo = dto.getTipoMovimentacao();
        if (tipoNovo != null) {
            String tipoNovoStr = String.valueOf(tipoNovo);
            if ("DESPESA".equalsIgnoreCase(tipoNovoStr)) {
                novaConta.sacar(dto.getValor());
            } else if ("RECEITA".equalsIgnoreCase(tipoNovoStr)) {
                novaConta.depositar(dto.getValor());
            }
            contaRepository.saveAndFlush(novaConta);
        }

        // 5. Salva o novo saldo após a movimentação
        movimentacao.setSaldoApos(novaConta.getSaldo());

        if (movimentacao.getData() == null) {
            movimentacao.setData(LocalDate.now());
        }

        Movimentacao atualizada = movimentacaoRepository.save(movimentacao);

        return MovimentacaoMapper.toResponseDTO(atualizada);
    }

    // Deletar
    @Transactional
    public void deletar(Long id) {

        Movimentacao movimentacao = movimentacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimentação não encontrada"));

        // Opcional: Se quiser estornar o valor na conta ao deletar, pode fazer aqui também
        Conta conta = movimentacao.getConta();
        BigDecimal valor = movimentacao.getValor();
        TipoMovimentacao tipo = movimentacao.getTipoMovimentacao();

        if (tipo != null) {
            if ("DESPESA".equalsIgnoreCase(String.valueOf(tipo))) {
                conta.depositar(valor);
            } else if ("RECEITA".equalsIgnoreCase(String.valueOf(tipo))) {
                conta.sacar(valor);
            }
            contaRepository.saveAndFlush(conta);
        }

        movimentacaoRepository.delete(movimentacao);
    }

    // Buscar todas
    public List<MovimentacaoResponseDTO> buscarTodas() {
        return movimentacaoRepository.findAll()
                .stream()
                .map(MovimentacaoMapper::toResponseDTO)
                .toList();
    }

    // Buscar movimentações por data
    public List<MovimentacaoResponseDTO> buscarPorData(LocalDate data) {
        return movimentacaoRepository.findByData(data)
                .stream()
                .map(MovimentacaoMapper::toResponseDTO)
                .toList();
    }

    // Buscar movimentações por categoria
    public List<MovimentacaoResponseDTO> buscarPorCategoria(Long categoriaId) {
        return movimentacaoRepository.findByCategoriaId(categoriaId)
                .stream()
                .map(MovimentacaoMapper::toResponseDTO)
                .toList();
    }

    // Buscar movimentações por conta
    public List<MovimentacaoResponseDTO> buscarPorConta(Long contaId) {
        return movimentacaoRepository.findByContaId(contaId)
                .stream()
                .map(MovimentacaoMapper::toResponseDTO)
                .toList();
    }
}