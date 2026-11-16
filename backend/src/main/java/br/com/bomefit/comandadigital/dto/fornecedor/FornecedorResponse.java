package br.com.bomefit.comandadigital.dto.fornecedor;

public record FornecedorResponse(Long id, String razaoSocial, String cnpj,
                                 String telefone, String email, String status) {}
