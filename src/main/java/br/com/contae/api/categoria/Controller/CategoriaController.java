package br.com.contae.api.categoria.Controller;

import br.com.contae.api.categoria.dto.CategoriaRequestDTO;
import br.com.contae.api.categoria.dto.CategoriaResponseDTO;
import br.com.contae.application.categoria.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias")
@RequiredArgsConstructor
@Tag(name = "Categorias", description = "Operações relacionadas às categorias financeiras do usuário")
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping
    @Operation(summary = "Listar categorias", description = "Retorna todas as categorias cadastradas.")
    @ApiResponse(responseCode = "200", description = "Lista de categorias retornada com sucesso")
    public List<CategoriaResponseDTO> listar(Authentication authentication) {
        return categoriaService.listar(authentication.getName());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar categoria por ID", description = "Retorna os dados de uma categoria específica.")
    @ApiResponse(responseCode = "200", description = "Categoria encontrada")
    @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    public CategoriaResponseDTO buscarPorId(@PathVariable Long id, Authentication authentication) {
        return categoriaService.buscarPorId(id, authentication.getName());
    }

    @PostMapping
    @Operation(summary = "Criar categoria", description = "Cria uma nova categoria a partir dos dados informados.")
    @ApiResponse(responseCode = "201", description = "Categoria criada com sucesso")
    public CategoriaResponseDTO salvar(@Valid @RequestBody CategoriaRequestDTO dto,
                                       Authentication authentication) {
        return categoriaService.salvar(dto, authentication.getName());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir categoria", description = "Remove a categoria pelo ID informado.")
    @ApiResponse(responseCode = "204", description = "Categoria excluída com sucesso")
    @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    public void excluir(@PathVariable Long id, Authentication authentication) {
        categoriaService.excluir(id, authentication.getName());
    }
}
