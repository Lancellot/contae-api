package br.com.contae.api.movimentacao.controller;

import br.com.contae.api.movimentacao.dto.MovimentacaoRequestDTO;
import br.com.contae.api.movimentacao.dto.MovimentacaoResponseDTO;
import br.com.contae.application.movimentacao.MovimentacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/movimentacoes")
@RequiredArgsConstructor
@Tag(
        name = "Movimentações",
        description = "Operações de movimentações financeiras"
)
public class MovimentacaoController {

    private final MovimentacaoService movimentacaoService;

    @PostMapping
    @Operation(summary = "Criar movimentação")
    public ResponseEntity<MovimentacaoResponseDTO> criar(
                        @Valid @RequestBody MovimentacaoRequestDTO dto,
                        Authentication authentication) {

                MovimentacaoResponseDTO movimentacao = movimentacaoService.criar(dto, authentication.getName());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(movimentacao);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar movimentação")
    public ResponseEntity<MovimentacaoResponseDTO> atualizar(
            @PathVariable Long id,
                        @Valid @RequestBody MovimentacaoRequestDTO dto,
                        Authentication authentication) {

        MovimentacaoResponseDTO movimentacao =
                                movimentacaoService.atualizar(id, dto, authentication.getName());

        return ResponseEntity.ok(movimentacao);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar movimentação")
        public ResponseEntity<Void> deletar(@PathVariable Long id, Authentication authentication) {

                movimentacaoService.deletar(id, authentication.getName());

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Buscar todas as movimentações")
        public ResponseEntity<List<MovimentacaoResponseDTO>> buscarTodas(Authentication authentication) {

        return ResponseEntity.ok(
                                movimentacaoService.buscarTodas(authentication.getName())
        );
    }

    @GetMapping("/data/{data}")
    @Operation(summary = "Buscar movimentações por data")
    public ResponseEntity<List<MovimentacaoResponseDTO>> buscarPorData(
                        @PathVariable LocalDate data,
                        Authentication authentication) {

        return ResponseEntity.ok(
                                movimentacaoService.buscarPorData(data, authentication.getName())
        );
    }

    @GetMapping("/categoria/{categoriaId}")
    @Operation(summary = "Buscar movimentações por categoria")
    public ResponseEntity<List<MovimentacaoResponseDTO>> buscarPorCategoria(
                        @PathVariable Long categoriaId,
                        Authentication authentication) {

        return ResponseEntity.ok(
                                movimentacaoService.buscarPorCategoria(categoriaId, authentication.getName())
        );
    }

    @GetMapping("/conta/{contaId}")
    @Operation(summary = "Buscar movimentações por conta")
    public ResponseEntity<List<MovimentacaoResponseDTO>> buscarPorConta(
                        @PathVariable Long contaId,
                        Authentication authentication) {

        return ResponseEntity.ok(
                                movimentacaoService.buscarPorConta(contaId, authentication.getName())
        );
    }
}