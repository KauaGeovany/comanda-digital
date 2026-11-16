package br.com.bomefit.comandadigital.service;

import br.com.bomefit.comandadigital.dto.usuario.*;
import br.com.bomefit.comandadigital.model.entity.Usuario;
import br.com.bomefit.comandadigital.model.enums.PerfilUsuario;
import br.com.bomefit.comandadigital.model.enums.StatusRegistro;
import br.com.bomefit.comandadigital.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UsuarioAdminService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    public UsuarioAdminService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository; this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly=true)
    public Page<UsuarioInternoResponse> listar(Pageable pageable) {
        return usuarioRepository.findAllByPerfilNot(PerfilUsuario.CLIENTE, pageable).map(this::response);
    }

    @Transactional
    public UsuarioInternoResponse criar(UsuarioInternoRequest request) {
        validarPerfil(request.perfil());
        String email = request.email().trim().toLowerCase();
        if (usuarioRepository.existsByEmailIgnoreCase(email)) throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado.");
        Usuario usuario = new Usuario();
        usuario.setNome(request.nome().trim()); usuario.setEmail(email);
        usuario.setSenhaHash(passwordEncoder.encode(request.senha())); usuario.setPerfil(request.perfil());
        usuario.setTelefone(request.telefone()); usuario.setEndereco(request.endereco()); usuario.setStatus(StatusRegistro.ATIVO);
        return response(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioInternoResponse atualizar(Long id, AtualizarUsuarioInternoRequest request) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));
        if (usuario.getPerfil() == PerfilUsuario.CLIENTE) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Esta área gerencia somente usuários internos.");
        validarPerfil(request.perfil());
        String email = request.email().trim().toLowerCase();
        usuarioRepository.findByEmailIgnoreCase(email).filter(u -> !u.getId().equals(id))
            .ifPresent(u -> { throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado."); });
        usuario.setNome(request.nome().trim()); usuario.setEmail(email); usuario.setPerfil(request.perfil());
        usuario.setTelefone(request.telefone()); usuario.setEndereco(request.endereco());
        if (request.senha() != null && !request.senha().isBlank()) usuario.setSenhaHash(passwordEncoder.encode(request.senha()));
        return response(usuarioRepository.save(usuario));
    }

    @Transactional
    public void desativar(Long id) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));
        if (usuario.getPerfil() == PerfilUsuario.ADMIN) throw new ResponseStatusException(HttpStatus.CONFLICT, "A conta ADMIN seed não pode ser desativada por esta tela.");
        usuario.setStatus(StatusRegistro.INATIVO); usuarioRepository.save(usuario);
    }

    private void validarPerfil(PerfilUsuario perfil) {
        if (perfil != PerfilUsuario.GERENTE && perfil != PerfilUsuario.COZINHEIRO)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O cadastro interno permite somente GERENTE ou COZINHEIRO.");
    }
    private UsuarioInternoResponse response(Usuario u) {
        return new UsuarioInternoResponse(u.getId(), u.getNome(), u.getEmail(), u.getPerfil().name(), u.getTelefone(), u.getEndereco(), u.getStatus().name());
    }
}

