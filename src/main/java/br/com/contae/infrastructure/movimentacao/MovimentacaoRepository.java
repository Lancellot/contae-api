package br.com.contae.infrastructure.movimentacao;

import br.com.contae.domain.movimentacao.Movimentacao;
import br.com.contae.domain.movimentacao.TipoDespesa;
import br.com.contae.domain.movimentacao.TipoMovimentacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {

    List<Movimentacao> findAllByConta_Usuario_Email(String email);

    List<Movimentacao> findByContaIdAndConta_Usuario_Email(Long contaId, String email);

    List<Movimentacao> findByCategoriaIdAndConta_Usuario_Email(Long categoriaId, String email);

    List<Movimentacao> findByDataAndConta_Usuario_Email(LocalDate data, String email);

    Optional<Movimentacao> findByIdAndConta_Usuario_Email(Long id, String email);

    List<Movimentacao> findByTipoMovimentacao(TipoMovimentacao tipoMovimentacao);

    List<Movimentacao> findByTipoDespesa(TipoDespesa tipoDespesa);

    List<Movimentacao> findByRecorrente (boolean recorrente);

    List<Movimentacao> findByDataBetween(LocalDate inicio, LocalDate fim);
}
