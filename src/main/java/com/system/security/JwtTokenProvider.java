package com.system.security;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import io.jsonwebtoken.*;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtTokenProvider {

    private final SecretKey key;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;
    private final String ACCESS_TYPE = "ACCESS";
    private final String REFRESH_TYPE = "REFRESH";

//    private final UserRepository userRepository;
//    private final RefreshTokenRepository refreshTokenRepository;


    public JwtTokenProvider(
            @Value("${jwt.secret-key}") String secretKey,
            @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration
//            UserRepository userRepository,
//            RefreshTokenRepository refreshTokenRepository

    ){
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
//        this.userRepository = userRepository;
//        this.refreshTokenRepository = refreshTokenRepository;
    }


    public String generateToken(Authentication authentication, long exp, String tokenType){
        String username = authentication.getName();
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + exp))
                .signWith(key,SignatureAlgorithm.HS256)
                .claim("type", tokenType)
                .compact();
    }

    public boolean isRefreshToken(String token){
        System.out.println("s");
        return !extractClaim(token, claims -> claims.get("type", String.class)).equals(ACCESS_TYPE);
    }

    public boolean isAccessToken(String token){
        return !extractClaim(token, claims -> claims.get("type", String.class)).equals(REFRESH_TYPE);
    }


    public String generateAccessToken(Authentication authentication){
        return generateToken(authentication, accessTokenExpiration, ACCESS_TYPE);
    }

    public String generateRefreshToken(Authentication authentication){
        return generateToken(authentication, refreshTokenExpiration, REFRESH_TYPE);
    }

    private Claims extractClaimsFromJwt(String token){
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver){
        Claims claims = extractClaimsFromJwt(token);
        if (claims == null) {
            throw new IllegalArgumentException("Claims are null for token");
        }
        return resolver.apply(claims);
    }

    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean isValidToken(String token){
        try {
            return !extractExpiration(token).before(new Date());
        }catch (JwtException | IllegalArgumentException e){
            return false;
        }
    }


}
