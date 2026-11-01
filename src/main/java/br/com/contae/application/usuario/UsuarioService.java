package br.com.contae.application.usuario;

import br.com.contae.api.usuario.dto.UsuarioRequestDTO;
import br.com.contae.api.usuario.dto.UsuarioResponseDTO;
import br.com.contae.api.usuario.mapper.UsuarioMapper;
import br.com.contae.domain.usuario.Usuario;
import br.com.contae.infrastructure.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponseDTO cadastrar(UsuarioRequestDTO dto) {
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um usuário cadastrado com este email");
        }

        Usuario usuario = usuarioMapper.toEntity(dto);
        usuario.setSenha(passwordEncoder.encode(dto.senha()));
        usuario.setAtivo(true);

        Usuario salvo = usuarioRepository.save(usuario);
        return usuarioMapper.toResponseDTO(salvo);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos(String email) {
        return usuarioRepository.findByEmail(email)
                .stream()
                .map(usuarioMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Long id, String email) {
        Usuario usuario = buscarEntidadePorId(id, email);
        return usuarioMapper.toResponseDTO(usuario);
    }

    @Transactional
    public UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO dto, String email) {
        Usuario usuario = buscarEntidadePorId(id, email);

        usuarioRepository.findByEmail(dto.email())
                .filter(outro -> !outro.getId().equals(id))
                .ifPresent(outro -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um usuário cadastrado com este email");
                });

        usuarioMapper.atualizarEntity(usuario, dto);

        if (dto.senha() != null && !dto.senha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(dto.senha()));
        }

        Usuario atualizado = usuarioRepository.save(usuario);
        return usuarioMapper.toResponseDTO(atualizado);
    }

    @Transactional
    public void inativar(Long id, String email) {
        Usuario usuario = buscarEntidadePorId(id, email);
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void excluir(Long id, String email) {
        Usuario usuario = buscarEntidadePorId(id, email);
        usuarioRepository.delete(usuario);
    }

    private Usuario buscarEntidadePorId(Long id, String email) {
        return usuarioRepository.findById(id)
                .filter(usuario -> usuario.getEmail().equals(email))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
    }
}