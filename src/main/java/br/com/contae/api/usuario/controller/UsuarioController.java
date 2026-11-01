package br.com.contae.api.usuario.controller;

import br.com.contae.api.usuario.dto.UsuarioRequestDTO;
import br.com.contae.api.usuario.dto.UsuarioResponseDTO;
import br.com.contae.application.usuario.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> cadastrar(@Valid @RequestBody UsuarioRequestDTO dto) {
        UsuarioResponseDTO response = usuarioService.cadastrar(dto);
        return ResponseEntity.created(URI.create("/api/usuarios/" + response.id())).body(response);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos(Authentication authentication) {
        return ResponseEntity.ok(usuarioService.listarTodos(authentication.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id, authentication.getName()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRequestDTO dto,
                                                        Authentication authentication) {
        return ResponseEntity.ok(usuarioService.atualizar(id, dto, authentication.getName()));
    }

    @PatchMapping("/{id}/inativar")
    public ResponseEntity<Void> inativar(@PathVariable Long id, Authentication authentication) {
        usuarioService.inativar(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id, Authentication authentication) {
        usuarioService.excluir(id, authentication.getName());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}