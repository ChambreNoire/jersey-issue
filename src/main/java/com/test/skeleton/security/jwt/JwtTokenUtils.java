package com.test.skeleton.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.test.skeleton.security.service.UserDetailsImpl;

import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtTokenUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenUtils.class);

    @Value("${sca.scar.security.jwtSecret}")
    private String jwtSecret;

    @Value("${sca.scar.security.jwtExpiration}")
    private int jwtExpiration;

    public String generateJwtToken(UserDetailsImpl user) {
        return generateJwtTokenFromUsername(user.getUsername());
    }

    public String generateJwtTokenFromUsername(String username) {
        return Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .setSubject(username)
            .setIssuer("sca-reporter")
            .setIssuedAt(new Date())
            .setExpiration(new Date(new Date().getTime() + jwtExpiration))
            .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
            .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }

    public boolean validateJwtToken(final String jwtToken) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(jwtToken);
            return true;
        } catch (SignatureException e) {
            LOGGER.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            LOGGER.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            LOGGER.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            LOGGER.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            LOGGER.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}
