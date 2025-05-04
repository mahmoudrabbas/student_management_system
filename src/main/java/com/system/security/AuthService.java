package com.system.security;

import com.system.dto.LoginRequest;
import com.system.dto.TokenResponse;
import com.system.entity.RefreshToken;
import com.system.entity.User;
import com.system.exception.ResourceNotFoundException;
import com.system.repository.RefreshTokenRepository;
import com.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RefreshTokenRepository tokenRepository;


    public TokenResponse login(LoginRequest request){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        String username = jwtTokenProvider.extractUsername(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));

        saveRefreshToken(user, refreshToken);

        return new TokenResponse(accessToken,refreshToken);
    }

    public void register(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }


    private void saveRefreshToken(User user, String token){
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUser(user);
        refreshToken.setCreatedAt(LocalDateTime.now());
        refreshToken.setExpiryAt(jwtTokenProvider.extractExpiration(token));
        tokenRepository.save(refreshToken);
    }


    public void logout(String token){
        if(tokenRepository.findByToken(token).isPresent()){
            tokenRepository.deleteByToken(token);
        }else {
            throw new ResourceNotFoundException("You Already Signed Out");
        }
    }

    public TokenResponse refreshAccess(String refreshToken){

    }
}
