package br.com.bomefit.comandadigital.dto.fornecedor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FornecedorRequest(@NotBlank @Size(max=180) String razaoSocial,
                                @NotBlank @Size(max=20) String cnpj,
                                @Size(max=30) String telefone,
                                @Email @Size(max=190) String email) {}
