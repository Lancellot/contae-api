package br.com.contae.api.movimentacao.mapper;

import br.com.contae.api.movimentacao.dto.MovimentacaoRequestDTO;
import br.com.contae.api.movimentacao.dto.MovimentacaoResponseDTO;
import br.com.contae.domain.categoria.Categoria;
import br.com.contae.domain.conta.Conta;
import br.com.contae.domain.movimentacao.Movimentacao;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MovimentacaoMapper {

    public static Movimentacao toEntity (
            MovimentacaoRequestDTO dadosRecebidos,
            Conta conta,
            Categoria categoria
    ){

        Movimentacao movimentacao = new Movimentacao();

        movimentacao.setConta(conta);
        movimentacao.setCategoria(categoria);
        movimentacao.setDescricao(dadosRecebidos.getDescricao());
        movimentacao.setValor(dadosRecebidos.getValor());
        movimentacao.setTipoMovimentacao(dadosRecebidos.getTipoMovimentacao());
        movimentacao.setTipoDespesa(dadosRecebidos.getTipoDespesa());
        movimentacao.setData(dadosRecebidos.getData());
        movimentacao.setRecorrente(dadosRecebidos.getRecorrente());

        return movimentacao;

    }

    public static void atualizar(
            Movimentacao movimentacao,
            MovimentacaoRequestDTO dto,
            Conta conta,
            Categoria categoria) {

        movimentacao.setConta(conta);
        movimentacao.setCategoria(categoria);
        movimentacao.setDescricao(dto.getDescricao());
        movimentacao.setValor(dto.getValor());
        movimentacao.setTipoMovimentacao(dto.getTipoMovimentacao());
        movimentacao.setTipoDespesa(dto.getTipoDespesa());

        // Proteção para não sobrescrever a data com null se o DTO não mandar
        if (dto.getData() != null) {
            movimentacao.setData(dto.getData());
        }

        movimentacao.setRecorrente(dto.getRecorrente());
    }

    // Versão padrão usada nas listagens
    public static MovimentacaoResponseDTO toResponseDTO(Movimentacao movimentacao) {
        return toResponseDTO(movimentacao, movimentacao.getSaldoOriginal());
    }

    // Versão completa que recebe o saldo original (caso queira passar manualmente)
    public static MovimentacaoResponseDTO toResponseDTO(Movimentacao movimentacao, BigDecimal saldoOriginal) {

        MovimentacaoResponseDTO dto = new MovimentacaoResponseDTO();

        dto.setId(movimentacao.getId());
        dto.setContaId(movimentacao.getConta().getId());
        dto.setCategoriaId(movimentacao.getCategoria().getId());
        dto.setDescricao(movimentacao.getDescricao());
        dto.setValor(movimentacao.getValor());
        dto.setTipoMovimentacao(movimentacao.getTipoMovimentacao());
        dto.setTipoDespesa(movimentacao.getTipoDespesa());
        dto.setData(movimentacao.getData());
        dto.setRecorrente(movimentacao.getRecorrente());

        // Preenchimento dos nomes e dos saldos fixos salvos na movimentação
        dto.setNomeConta(movimentacao.getConta().getNome());
        dto.setNomeCategoria(movimentacao.getCategoria().getNome());

        // Usa o saldo original salvo (se veio parâmetro, prioriza ele; senão pega da entidade)
        dto.setSaldoOriginalConta(saldoOriginal != null ? saldoOriginal : movimentacao.getSaldoOriginal());

        // Exibe o saldo de quanto a conta ficou após essa movimentação específica
        dto.setSaldoAtualConta(movimentacao.getSaldoApos());

        return dto;
    }
}