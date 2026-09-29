package br.com.contae.infrastructure.categoria;

import br.com.contae.domain.categoria.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface CategoriaRepository extends JpaRepository<Categoria, Long>{

	List<Categoria> findAllByUsuario_Email(String email);

	Optional<Categoria> findByIdAndUsuario_Email(Long id, String email);
}
