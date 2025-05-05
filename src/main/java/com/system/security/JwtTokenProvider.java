package com.system.security;

import com.system.entity.RefreshToken;
import com.system.entity.User;
import com.system.exception.ResourceNotFoundException;
import com.system.repository.RefreshTokenRepository;
import com.system.repository.UserRepository;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
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


    public String generateToken(Authentication authentication, long exp){
        String username = authentication.getName();
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + exp))
                .signWith(key,SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateAccessToken(Authentication authentication){
        return generateToken(authentication, accessTokenExpiration);
    }

    public String generateRefreshToken(Authentication authentication){
        return generateToken(authentication, refreshTokenExpiration);
    }

    private Claims extractClaimsFromJwt(String token){
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
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
//        String username = extractUsername(token);
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
//
//        if(!refreshTokenRepository.findByUserId(user.getId()).isPresent()){
//            return false;
//        }
//        RefreshToken refreshToken = refreshTokenRepository.findByUserId(user.getId()).get();
//
//        if(extractExpiration(refreshToken.getToken()).before(new Date())){
//            return false;
//        }


        try {
            return !extractExpiration(token).before(new Date());
        }catch (JwtException | IllegalArgumentException e){
            return false;
        }
    }


}
