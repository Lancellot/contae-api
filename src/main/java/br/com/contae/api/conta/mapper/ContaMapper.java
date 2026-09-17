package br.com.contae.api.conta.mapper;

import br.com.contae.api.conta.dto.ContaRequestDTO;
import br.com.contae.api.conta.dto.ContaResponseDTO;
import br.com.contae.domain.conta.Conta;
import br.com.contae.domain.usuario.Usuario;

public class ContaMapper {

    public static Conta toEntity(ContaRequestDTO dto, Usuario usuario) {
        return new Conta(usuario, dto.getNome(), dto.getTipo(), dto.getSaldo());
    }

    public static ContaResponseDTO toResponseDTO(Conta conta) {
        return new ContaResponseDTO(
                conta.getId(),
                conta.getNome(),
                conta.getTipo(),
                conta.getSaldo(),
                conta.getUsuario().getId()
        );
    }
}