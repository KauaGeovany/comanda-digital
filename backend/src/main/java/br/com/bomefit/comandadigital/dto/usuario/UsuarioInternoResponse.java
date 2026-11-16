package br.com.bomefit.comandadigital.dto.usuario;

public record UsuarioInternoResponse(Long id, String nome, String email, String perfil,
                                     String telefone, String endereco, String status) {}
