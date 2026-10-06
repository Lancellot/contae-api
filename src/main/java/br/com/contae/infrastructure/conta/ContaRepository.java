package br.com.contae.infrastructure.conta;

import br.com.contae.domain.conta.Conta;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;

public interface ContaRepository extends JpaRepository<Conta, Long> {

    List<Conta> findAllByUsuario_Email(String email);

    Optional<Conta> findByIdAndUsuario_Email(Long id, String email);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Conta> findLockedByIdAndUsuario_Email(Long id, String email);
}
