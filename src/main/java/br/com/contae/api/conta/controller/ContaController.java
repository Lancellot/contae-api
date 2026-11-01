package br.com.contae.api.conta.controller;

import br.com.contae.api.conta.dto.ContaRequestDTO;
import br.com.contae.api.conta.dto.ContaResponseDTO;
import br.com.contae.application.conta.ContaService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contas")
public class ContaController {

    private final ContaService contaService;

    public ContaController(ContaService contaService) {
        this.contaService = contaService;
    }

    @GetMapping
    public List<ContaResponseDTO> listar(Authentication authentication) {
        return contaService.listar(authentication.getName());
    }

    @GetMapping("/{id}")
    public ContaResponseDTO buscarPorId(@PathVariable Long id, Authentication authentication) {
        return contaService.buscarPorId(id, authentication.getName());
    }

    @PostMapping
    public ContaResponseDTO salvar(@Valid @RequestBody ContaRequestDTO dto,
                                   Authentication authentication) {
        return contaService.salvar(dto, authentication.getName());
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id, Authentication authentication) {
        contaService.excluir(id, authentication.getName());
    }
}