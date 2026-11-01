package br.com.contae.api.categoria.mapper;

import br.com.contae.api.categoria.dto.CategoriaResponseDTO;
import br.com.contae.api.categoria.dto.CategoriaRequestDTO;
import br.com.contae.domain.categoria.Categoria;
import br.com.contae.domain.usuario.Usuario;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CategoriaMapper {

    // Transforma o DTO de entrada + o Usuario ja buscado no banco
    // em uma entidade Categoria pronta para ser salva.
    public Categoria toEntity(CategoriaRequestDTO dto, Usuario usuario) {
        return new Categoria(usuario, dto.getNome());
    }

    // Transforma a entidade Categoria (vinda do banco) no DTO de saida
    // que sera devolvido ao cliente da API.
    public CategoriaResponseDTO toResponseDTO(Categoria categoria) {
        return new CategoriaResponseDTO(
                categoria.getId(),
                categoria.getNome(),
                categoria.getUsuario().getId()
        );
    }
}