package com.system.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;



    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = getJwtFromRequest(request);
        if(token!=null&& SecurityContextHolder.getContext().getAuthentication()==null){
            String username = jwtTokenProvider.extractUsername(token);
            System.out.println("username: "+username);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            System.out.println(jwtTokenProvider.isValidToken(token));
            if(jwtTokenProvider.isValidToken(token)){
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );

                System.out.println("Aywa Valid");
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);


    }


    private String getJwtFromRequest(HttpServletRequest request){
        String authHeader = request.getHeader("Authorization");
        if(authHeader!=null && authHeader.startsWith("Bearer")){
            return authHeader.substring(7);
        }
        return null;
    }
}
