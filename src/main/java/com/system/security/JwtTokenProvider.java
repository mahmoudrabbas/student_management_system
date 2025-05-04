package com.system.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.function.Function;

@Service
public class JwtTokenProvider {

    private final String secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtTokenProvider(
            @Value("${jwt.secret-key}") String secretKey,
            @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration
    ){
        this.secretKey = secretKey;
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;

    }


    public String generateToken(Authentication authentication, long exp){
        String username = authentication.getName();
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + exp))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String generateAccessToken(Authentication authentication){
        return generateToken(authentication, accessTokenExpiration);
    }

    public String generateRefreshToken(Authentication authentication){
        return generateToken(authentication, refreshTokenExpiration);
    }

    private Claims extractClaimsFromJwt(String token){
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver){
        Claims claims = extractClaimsFromJwt(token);
        return resolver.apply(claims);
    }

    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean isValidToken(String token){
        System.out.println("inside checking method");
        System.out.println("Is It Expired: "+extractExpiration(token).before(new Date()));
        try {
            return !extractExpiration(token).before(new Date());
        }catch (JwtException | IllegalArgumentException e){
            return false;
        }
    }


}
