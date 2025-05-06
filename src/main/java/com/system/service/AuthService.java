package com.system.service;

import com.system.dto.LoginRequest;
import com.system.dto.RefreshRequest;
import com.system.dto.TokenResponse;
import com.system.entity.RefreshToken;
import com.system.entity.User;
import com.system.exception.ResourceNotFoundException;
import com.system.repository.RefreshTokenRepository;
import com.system.repository.UserRepository;
import com.system.security.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RefreshTokenRepository tokenRepository;
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);


    public TokenResponse login(LoginRequest request){
        logger.info("Process Login for user: {} ", request.getUsername());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        String username = jwtTokenProvider.extractUsername(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));

        logger.info("Refresh Token Generated And Saved For User LOGGER LOGGER {}",request.getUsername());
        saveRefreshToken(user, refreshToken);

        return new TokenResponse(accessToken,refreshToken);
    }

    public void register(User user){
        logger.info("New Registration {} ", user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }


    private void saveRefreshToken(User user, String token){
        logger.info("Refresh token for user {}", user.getUsername());
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUser(user);
        refreshToken.setCreatedAt(LocalDateTime.now());
        refreshToken.setExpiryAt(jwtTokenProvider.extractExpiration(token));
        logger.info("Save new Refresh token for user {}", user.getUsername());
        tokenRepository.save(refreshToken);
    }


    public void logout(RefreshRequest refreshRequest){
        String username = jwtTokenProvider.extractUsername(refreshRequest.getRefreshToken());
        logger.info("user {} is logging out", username);
        if(jwtTokenProvider.isRefreshToken(refreshRequest.getRefreshToken()))
            throw new ResourceNotFoundException("Expected Refresh Token");

        if(tokenRepository.findByToken(refreshRequest.getRefreshToken()).isPresent()){
            tokenRepository.deleteByToken(refreshRequest.getRefreshToken());
        }else {
            logger.info("user {} is logging out and they are Already signed out", username);
            throw new ResourceNotFoundException("You Already Signed Out");
        }
    }

    public TokenResponse refreshAccess(RefreshRequest refreshToken){
        String usernameForLogger = jwtTokenProvider.extractUsername(refreshToken.getRefreshToken());
        logger.info("processing of refreshing new token for {}", usernameForLogger);
        if(jwtTokenProvider.isRefreshToken(refreshToken.getRefreshToken()))
            throw new ResourceNotFoundException("Expected Refresh Token");
        RefreshToken refreshedTokenFromDB = tokenRepository.findByToken(refreshToken.getRefreshToken()).get();
        logger.info("getting the refresh token for user {} from database", usernameForLogger);

        if(!jwtTokenProvider.isValidToken(refreshToken.getRefreshToken())){
            tokenRepository.delete(refreshedTokenFromDB);
            throw new ResourceNotFoundException("You are Already signed out, Please Login again");
        }


        String username = jwtTokenProvider.extractUsername(refreshToken.getRefreshToken());
        User user = userRepository.findByUsername(username).get();

        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, null);

        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        logger.info("deleting the old refresh token and renew token for {}", username);
        tokenRepository.delete(refreshedTokenFromDB);
        tokenRepository.flush();
        saveRefreshToken(user, newRefreshToken);

        return new TokenResponse(accessToken, newRefreshToken);

    }
}
