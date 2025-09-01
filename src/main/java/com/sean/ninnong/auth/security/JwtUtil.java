package com.sean.ninnong.auth.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.access-token-key}")
    private String accessTokenSecret;

    @Value("${jwt.refresh-token-key}")
    private String refreshTokenSecret;

    @Value("${jwt.access-token-ttl}")
    private long accessTtlSeconds;

    @Value("${jwt.refresh-token-ttl}")
    private long refreshTtlSeconds;


    private JwtDecoder accessDecoder() {
        var key = new SecretKeySpec(accessTokenSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(key).build();
    }

    private JwtDecoder refreshDecoder() {
        var key = new SecretKeySpec(refreshTokenSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(key).build();
    }


    private JwtEncoder accessEncoder() {
        var key = new SecretKeySpec(accessTokenSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        var jwk = new com.nimbusds.jose.jwk.OctetSequenceKey.Builder(key.getEncoded())
                .algorithm(com.nimbusds.jose.JWSAlgorithm.HS256)
                .keyID("access")
                .build();
        var jwks = new com.nimbusds.jose.jwk.source.ImmutableJWKSet<>(
                new com.nimbusds.jose.jwk.JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }

    private JwtEncoder refreshEncoder() {
        var key = new SecretKeySpec(refreshTokenSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        var jwk = new com.nimbusds.jose.jwk.OctetSequenceKey.Builder(key.getEncoded())
                .algorithm(com.nimbusds.jose.JWSAlgorithm.HS256)
                .keyID("refresh")
                .build();
        var jwks = new com.nimbusds.jose.jwk.source.ImmutableJWKSet<>(
                new com.nimbusds.jose.jwk.JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }


    private String stripBearer(String token) {
        if (token == null) return null;
        var t = token.trim();
        return t.startsWith("Bearer ") ? t.substring(7).trim() : t;
    }


    public String generateAccessToken(String email) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject(email)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(accessTtlSeconds))
                .claim("typ", "access")
                .build();
        var header = JwsHeader.with(MacAlgorithm.HS256).keyId("access").build();
        return accessEncoder().encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public String generateRefreshToken(Long userId) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject(String.valueOf(userId))
                .issuedAt(now)
                .expiresAt(now.plusSeconds(refreshTtlSeconds))
                .claim("typ", "refresh")
                .build();
        var header = JwsHeader.with(MacAlgorithm.HS256).keyId("refresh").build();
        return refreshEncoder().encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }


    public boolean validateAccessToken(String token) {
        try {
            Jwt jwt = accessDecoder().decode(stripBearer(token));
            Object typ = jwt.getClaims().get("typ");
            return typ == null || "access".equals(typ.toString());
        } catch (JwtException e) {
            return false;
        }
    }

    public boolean isInvalidRefreshToken(String token) {
        try {
            Jwt jwt = refreshDecoder().decode(stripBearer(token));
            Object typ = jwt.getClaims().get("typ");
            return !(typ != null && "refresh".equals(typ.toString()));
        } catch (JwtException e) {
            return true;
        }
    }


    public String extractEmailFromAccessToken(String token) {
        Jwt jwt = accessDecoder().decode(stripBearer(token));
        String sub = jwt.getSubject();
        if (sub == null || sub.isBlank()) throw new JwtException("subject(sub) is missing");
        return sub;
    }

    public Long extractUserIdFromRefresh(String refreshToken) {
        Jwt jwt = refreshDecoder().decode(stripBearer(refreshToken));
        Object typ = jwt.getClaims().get("typ");
        if (typ == null || !"refresh".equals(typ.toString())) {
            throw new JwtException("not a refresh token");
        }
        try {
            return Long.parseLong(jwt.getSubject());
        } catch (NumberFormatException e) {
            throw new JwtException("invalid subject (userId): " + jwt.getSubject());
        }
    }

    public int extractTokenVersionFromAccess(String token) {
        Object ver = accessDecoder().decode(stripBearer(token)).getClaims().get("ver");
        if (ver == null) return 1;
        try {
            return Integer.parseInt(ver.toString());
        } catch (NumberFormatException e) {
            throw new JwtException("invalid token version: " + ver);
        }
    }
}
