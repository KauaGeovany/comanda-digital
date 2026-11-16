package br.com.bomefit.comandadigital.dto.auth;

public record AuthResponse(String token, String tipo, long expiraEmSegundos, UsuarioResumo usuario) {}
