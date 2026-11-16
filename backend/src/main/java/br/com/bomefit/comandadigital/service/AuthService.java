package br.com.bomefit.comandadigital.service;

import br.com.bomefit.comandadigital.dto.auth.AuthResponse;
import br.com.bomefit.comandadigital.dto.auth.CadastroClienteRequest;
import br.com.bomefit.comandadigital.dto.auth.LoginRequest;
import br.com.bomefit.comandadigital.dto.auth.UsuarioResumo;
import br.com.bomefit.comandadigital.model.entity.Usuario;
import br.com.bomefit.comandadigital.model.enums.PerfilUsuario;
import br.com.bomefit.comandadigital.model.enums.StatusRegistro;
import br.com.bomefit.comandadigital.repository.UsuarioRepository;
import br.com.bomefit.comandadigital.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse cadastrarCliente(CadastroClienteRequest request) {
        String email = request.email().trim().toLowerCase();
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe uma conta com este e-mail.");
        }
        Usuario usuario = new Usuario();
        usuario.setNome(request.nome().trim());
        usuario.setEmail(email);
        usuario.setSenhaHash(passwordEncoder.encode(request.senha()));
        usuario.setPerfil(PerfilUsuario.CLIENTE);
        usuario.setTelefone(request.telefone());
        usuario.setEndereco(request.endereco().trim());
        usuario.setStatus(StatusRegistro.ATIVO);
        usuario = usuarioRepository.save(usuario);
        return resposta(usuario);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(request.email().trim())
            .filter(u -> u.getStatus() == StatusRegistro.ATIVO)
            .filter(u -> passwordEncoder.matches(request.senha(), u.getSenhaHash()))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos."));
        return resposta(usuario);
    }

    private AuthResponse resposta(Usuario usuario) {
        return new AuthResponse(
            jwtService.generateToken(usuario),
            "Bearer",
            jwtService.expirationSeconds(),
            new UsuarioResumo(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getPerfil().name())
        );
    }
}
