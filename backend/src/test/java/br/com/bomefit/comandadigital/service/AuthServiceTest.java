package br.com.bomefit.comandadigital.service;

import br.com.bomefit.comandadigital.dto.auth.AuthResponse;
import br.com.bomefit.comandadigital.dto.auth.CadastroClienteRequest;
import br.com.bomefit.comandadigital.dto.auth.LoginRequest;
import br.com.bomefit.comandadigital.model.entity.Usuario;
import br.com.bomefit.comandadigital.model.enums.PerfilUsuario;
import br.com.bomefit.comandadigital.model.enums.StatusRegistro;
import br.com.bomefit.comandadigital.repository.UsuarioRepository;
import br.com.bomefit.comandadigital.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("RN10: Não deve permitir cadastro com e-mail já existente (lança 409 Conflict)")
    void testRN10_EmailDuplicado_Lanca409() {
        CadastroClienteRequest request = new CadastroClienteRequest(
            "Cliente", "duplicado@email.com", "senha12345", "11999999999", "Rua 1, 100"
        );

        when(usuarioRepository.existsByEmailIgnoreCase("duplicado@email.com")).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
            authService.cadastrarCliente(request)
        );

        assertEquals(409, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("Já existe uma conta com este e-mail"));
    }

    @Test
    @DisplayName("RNF01: Cadastro de cliente deve criptografar a senha com BCrypt via PasswordEncoder")
    void testRNF01_CadastroCliente_CriptografaSenha() {
        CadastroClienteRequest request = new CadastroClienteRequest(
            "Novo Cliente", "novo@email.com", "senhaForte123", "11988887777", "Av Central, 500"
        );

        when(usuarioRepository.existsByEmailIgnoreCase("novo@email.com")).thenReturn(false);
        when(passwordEncoder.encode("senhaForte123")).thenReturn("$2a$10$hashedPasswordHere");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            u.setId(15L);
            return u;
        });
        when(jwtService.generateToken(any(Usuario.class))).thenReturn("jwt-token-fake");
        when(jwtService.expirationSeconds()).thenReturn(28800L);

        AuthResponse response = authService.cadastrarCliente(request);

        assertNotNull(response);
        assertEquals("jwt-token-fake", response.token());
        assertEquals("novo@email.com", response.usuario().email());
        assertEquals("CLIENTE", response.usuario().perfil());

        verify(passwordEncoder, times(1)).encode("senhaForte123");
        verify(usuarioRepository, times(1)).save(argThat(u ->
            u.getSenhaHash().equals("$2a$10$hashedPasswordHere") &&
            u.getPerfil() == PerfilUsuario.CLIENTE
        ));
    }

    @Test
    @DisplayName("RNF01 / RF-005: Login com credenciais inválidas deve lançar 401 Unauthorized")
    void testLogin_CredenciaisInvalidas_Lanca401() {
        LoginRequest request = new LoginRequest("admin@email.com", "senhaErrada");

        Usuario usuario = new Usuario();
        usuario.setEmail("admin@email.com");
        usuario.setSenhaHash("$2a$10$hashedRealPassword");
        usuario.setStatus(StatusRegistro.ATIVO);

        when(usuarioRepository.findByEmailIgnoreCase("admin@email.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senhaErrada", "$2a$10$hashedRealPassword")).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
            authService.login(request)
        );

        assertEquals(401, ex.getStatusCode().value());
    }
}
