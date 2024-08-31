package com.project.backend.security;


import java.util.Collection;
import java.util.Iterator;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.project.backend.constants.TokenType;
import com.project.backend.model.RefreshToken;
import com.project.backend.security.jwt.JwtUtils;
import com.project.backend.security.service.UserDetailsImpl;
import com.project.backend.service.RefreshTokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    private final JwtUtils jwtUtils;

    private final RefreshTokenService refreshTokenService;
    
    public AuthLoginFilter(AuthenticationManager authenticationManager, JwtUtils jwtUtils, RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        String username = obtainUsername(request);
        String password = obtainPassword(request);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);

        return authenticationManager.authenticate(authToken);

    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {

        			//UserDetailsS
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String accessToken = jwtUtils.generateTokenFromEmail(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
        
        response.setHeader(TokenType.ACCESS.getType(), accessToken);
        response.addCookie(jwtUtils.createCookie(TokenType.REFRESH.getType(), refreshToken.getToken(), 24*60*60));
        response.setStatus(HttpStatus.OK.value());

    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {

        response.setStatus(401);
    }

}
