package sv.edu.udb.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    // 1. Usa una llave de al menos 32 caracteres y sin caracteres especiales inválidos
    // Esta es una llave de ejemplo en Base64 (puedes usar esta misma)
    private final String SECRET = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 24 horas
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // 2. Usa el objeto Key
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey()) // 3. Usa parserBuilder para versiones modernas
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Método auxiliar para decodificar la llave correctamente
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Método para validar si el token es correcto (útil para tu JwtFilter)
    public boolean isTokenValid(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());
    }
}