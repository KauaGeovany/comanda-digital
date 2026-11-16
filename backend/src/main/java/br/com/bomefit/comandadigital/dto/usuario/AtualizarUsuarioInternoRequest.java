package br.com.bomefit.comandadigital.dto.usuario;

import br.com.bomefit.comandadigital.model.enums.PerfilUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AtualizarUsuarioInternoRequest(@NotBlank @Size(max=120) String nome, @NotBlank @Email @Size(max=190) String email,
                                              @Size(min=8, max=72) String senha, @NotNull PerfilUsuario perfil,
                                              @Size(max=30) String telefone, @Size(max=500) String endereco) {}
