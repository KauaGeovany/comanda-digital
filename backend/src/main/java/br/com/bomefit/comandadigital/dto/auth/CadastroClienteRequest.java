package br.com.bomefit.comandadigital.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CadastroClienteRequest(
    @NotBlank @Size(max = 120) String nome,
    @NotBlank @Email @Size(max = 190) String email,
    @NotBlank @Size(min = 8, max = 72) String senha,
    @NotBlank @Size(max = 30) String telefone,
    @NotBlank @Size(max = 500) String endereco
) {}
