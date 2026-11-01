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
            @Valid @RequestBody MovimentacaoRequestDTO dto) {

        MovimentacaoResponseDTO movimentacao = movimentacaoService.criar(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(movimentacao);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar movimentação")
    public ResponseEntity<MovimentacaoResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody MovimentacaoRequestDTO dto) {

        MovimentacaoResponseDTO movimentacao =
                movimentacaoService.atualizar(id, dto);

        return ResponseEntity.ok(movimentacao);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar movimentação")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {

        movimentacaoService.deletar(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Buscar todas as movimentações")
    public ResponseEntity<List<MovimentacaoResponseDTO>> buscarTodas() {

        return ResponseEntity.ok(
                movimentacaoService.buscarTodas()
        );
    }

    @GetMapping("/data/{data}")
    @Operation(summary = "Buscar movimentações por data")
    public ResponseEntity<List<MovimentacaoResponseDTO>> buscarPorData(
            @PathVariable LocalDate data) {

        return ResponseEntity.ok(
                movimentacaoService.buscarPorData(data)
        );
    }

    @GetMapping("/categoria/{categoriaId}")
    @Operation(summary = "Buscar movimentações por categoria")
    public ResponseEntity<List<MovimentacaoResponseDTO>> buscarPorCategoria(
            @PathVariable Long categoriaId) {

        return ResponseEntity.ok(
                movimentacaoService.buscarPorCategoria(categoriaId)
        );
    }

    @GetMapping("/conta/{contaId}")
    @Operation(summary = "Buscar movimentações por conta")
    public ResponseEntity<List<MovimentacaoResponseDTO>> buscarPorConta(
            @PathVariable Long contaId) {

        return ResponseEntity.ok(
                movimentacaoService.buscarPorConta(contaId)
        );
    }
}