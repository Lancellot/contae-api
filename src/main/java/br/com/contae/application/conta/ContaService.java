package br.com.contae.application.conta;

import br.com.contae.api.conta.dto.ContaRequestDTO;
import br.com.contae.api.conta.dto.ContaResponseDTO;
import br.com.contae.api.conta.mapper.ContaMapper;
import br.com.contae.domain.conta.Conta;
import br.com.contae.domain.usuario.Usuario;
import br.com.contae.infrastructure.conta.ContaRepository;
import br.com.contae.infrastructure.usuario.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ContaService {

    private final ContaRepository contaRepository;
    private final UsuarioRepository usuarioRepository;

    public ContaService(ContaRepository contaRepository, UsuarioRepository usuarioRepository) {
        this.contaRepository = contaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<ContaResponseDTO> listar(String email) {
        return contaRepository.findAllByUsuario_Email(email)
                .stream()
                .map(ContaMapper::toResponseDTO)
                .toList();
    }

    public ContaResponseDTO salvar(ContaRequestDTO dto, String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        Conta conta = ContaMapper.toEntity(dto, usuario);
        Conta contaSalva = contaRepository.save(conta);
        return ContaMapper.toResponseDTO(contaSalva);
    }

    public ContaResponseDTO buscarPorId(Long id, String email) {
        Conta conta = contaRepository.findByIdAndUsuario_Email(id, email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Conta não encontrada"));
        return ContaMapper.toResponseDTO(conta);
    }

    public void excluir(Long id, String email) {
        Conta conta = contaRepository.findByIdAndUsuario_Email(id, email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Conta não encontrada"));
        contaRepository.delete(conta);
    }
}