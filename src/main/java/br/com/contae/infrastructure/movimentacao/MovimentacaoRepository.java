package br.com.contae.infrastructure.movimentacao;

import br.com.contae.domain.movimentacao.Movimentacao;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {

    List<Movimentacao> findAllByConta_Usuario_Email(String email);

    List<Movimentacao> findByContaIdAndConta_Usuario_Email(Long contaId, String email);

    List<Movimentacao> findByCategoriaIdAndConta_Usuario_Email(Long categoriaId, String email);

    List<Movimentacao> findByDataAndConta_Usuario_Email(LocalDate data, String email);

    Optional<Movimentacao> findByIdAndConta_Usuario_Email(Long id, String email);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Movimentacao> findLockedByIdAndConta_Usuario_Email(Long id, String email);

}
