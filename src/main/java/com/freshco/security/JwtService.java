package com.freshco.security;

import com.freshco.exception.JwtAuthenticationException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
//import java.util.function.Function;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration-milliseconds}")
    private long jwtExpiration;

    private SecretKey secretKey;

    private JwtParser jwtParser;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secret));
        this.jwtParser = Jwts.parser()
                .verifyWith(this.secretKey)
                .build();
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    public String extractValidUsername(String token) {
        try {
            Claims claims = jwtParser
                    .parseSignedClaims(token)
                    .getPayload();

            Date expiration = claims.getExpiration();
            if (expiration == null || expiration.before(new Date())) {
                throw new JwtAuthenticationException("Token has expired", HttpStatus.UNAUTHORIZED);
            }

            String subject = claims.getSubject();
            if (subject == null || subject.isEmpty()) {
                throw new JwtAuthenticationException("Token missing subject", HttpStatus.BAD_REQUEST);
            }

            return subject;
        } catch (ExpiredJwtException e) {
            throw new JwtAuthenticationException("Token has expired", HttpStatus.UNAUTHORIZED, e);
        } catch (MalformedJwtException e) {
            throw new JwtAuthenticationException("Malformed JWT token", HttpStatus.BAD_REQUEST, e);
        } catch (UnsupportedJwtException e) {
            throw new JwtAuthenticationException("Unsupported JWT token", HttpStatus.BAD_REQUEST, e);
        } catch (SignatureException e) {
            throw new JwtAuthenticationException("Invalid JWT signature", HttpStatus.UNAUTHORIZED, e);
        } catch (IllegalArgumentException e) {
            throw new JwtAuthenticationException("JWT claims string is empty or null", HttpStatus.BAD_REQUEST, e);
        } catch (JwtException e) {
            throw new JwtAuthenticationException("JWT validation error", HttpStatus.BAD_REQUEST, e);
        }
    }

    public void validateToken(String token, String username) {
        String extractedUsername = extractValidUsername(token);
        if (!username.equals(extractedUsername)) {
            throw new JwtAuthenticationException("Username mismatch", HttpStatus.UNAUTHORIZED);
        }
    }

    public Claims extractAllClaims(String token) {
        return this.jwtParser
                .parseSignedClaims(token)
                .getPayload();
    }

//    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
//        final Claims claims = extractAllClaims(token);
//        return claimsResolver.apply(claims);
//    }
//
//    public String extractUsername(String token) {
//        return extractClaim(token, Claims::getSubject);
//    }
//
//    public Date extractExpiration(String token) {
//        return extractClaim(token, Claims::getExpiration);
//    }
//
//    public boolean isTokenExpired(String token) {
//        return extractExpiration(token)
//                .before(new Date(System.currentTimeMillis()));
//    }

}
