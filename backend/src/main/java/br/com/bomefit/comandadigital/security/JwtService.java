package br.com.bomefit.comandadigital.security;

import br.com.bomefit.comandadigital.model.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private final String secret;
    private final long expirationMs;

    public JwtService(
        @Value("${app.jwt.secret:}") String secret,
        @Value("${app.jwt.expiration-ms:28800000}") long expirationMs
    ) {
        this.secret = secret;
        this.expirationMs = expirationMs;
        signingKey();
    }

    public String generateToken(Usuario usuario) {
        Instant now = Instant.now();
        return Jwts.builder()
            .subject(usuario.getEmail())
            .claim("role", usuario.getPerfil().name())
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusMillis(expirationMs)))
            .signWith(signingKey())
            .compact();
    }

    public String extractSubject(String token) {
        return claims(token).getSubject();
    }

    public boolean isValid(String token, Usuario usuario) {
        Claims claims = claims(token);
        return claims.getSubject().equalsIgnoreCase(usuario.getEmail())
            && claims.getExpiration().after(new Date());
    }

    public long expirationSeconds() {
        return expirationMs / 1000;
    }

    private Claims claims(String token) {
        return Jwts.parser().verifyWith(signingKey()).build().parseSignedClaims(token).getPayload();
    }

    private SecretKey signingKey() {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("Configure JWT_SECRET com uma chave forte de pelo menos 32 bytes.");
        }
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(secret);
        } catch (RuntimeException ignored) {
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }
        if (keyBytes.length < 32) {
            throw new IllegalStateException("JWT_SECRET precisa ter pelo menos 32 bytes (256 bits).");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

