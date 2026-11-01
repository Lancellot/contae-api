package br.com.contae.infrastructure.movimentacao;

import br.com.contae.domain.movimentacao.Movimentacao;
import br.com.contae.domain.movimentacao.TipoDespesa;
import br.com.contae.domain.movimentacao.TipoMovimentacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {

    List<Movimentacao> findByContaId(Long contaId);

    List<Movimentacao> findByCategoriaId(Long categoriaId);

    List<Movimentacao> findByTipoMovimentacao(TipoMovimentacao tipoMovimentacao);

    List<Movimentacao> findByTipoDespesa(TipoDespesa tipoDespesa);

    List<Movimentacao> findByRecorrente (boolean recorrente);

    List<Movimentacao> findByData(LocalDate data);

    List<Movimentacao> findByDataBetween(LocalDate inicio, LocalDate fim);
}