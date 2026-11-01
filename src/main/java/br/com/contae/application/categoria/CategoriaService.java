package br.com.contae.application.categoria;

import br.com.contae.api.categoria.dto.CategoriaResponseDTO;
import br.com.contae.api.categoria.dto.CategoriaRequestDTO;
import br.com.contae.api.categoria.mapper.CategoriaMapper;
import br.com.contae.domain.categoria.Categoria;
import br.com.contae.domain.usuario.Usuario;
import br.com.contae.infrastructure.categoria.CategoriaRepository;
import br.com.contae.infrastructure.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.http.HttpStatus;
import java.util.List;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;

    // Lista todas as categorias, ja convertidas para DTO de saida.
    public List<CategoriaResponseDTO> listar(String email) {
        return categoriaRepository.findAllByUsuario_Email(email)
                .stream()
                .map(CategoriaMapper::toResponseDTO)
                .toList();
    }

    // Cria uma nova categoria a partir do DTO de entrada.
        public CategoriaResponseDTO salvar(CategoriaRequestDTO dto, String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        // Converte DTO + Usuario em entidade Categoria.
        Categoria categoria = CategoriaMapper.toEntity(dto, usuario);

        // Salva a entidade no banco.
        Categoria categoriaSalva = categoriaRepository.save(categoria);

        // Converte a entidade salva de volta para DTO de saida.
        return CategoriaMapper.toResponseDTO(categoriaSalva);
    }

    // Busca uma categoria pelo ID e devolve ja como DTO de saida.
        public CategoriaResponseDTO buscarPorId(Long id, String email) {
        Categoria categoria = categoriaRepository.findByIdAndUsuario_Email(id, email)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Categoria não encontrada"));
        return CategoriaMapper.toResponseDTO(categoria);
    }

    // Exclui uma categoria pelo ID.
    public void excluir(Long id, String email) {
        Categoria categoria = categoriaRepository.findByIdAndUsuario_Email(id, email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Categoria não encontrada"));
        categoriaRepository.delete(categoria);
    }
}