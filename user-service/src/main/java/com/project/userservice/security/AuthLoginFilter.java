package com.project.userservice.security;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.common.response.MessageResponse;
import com.project.userservice.constants.TokenType;
import com.project.userservice.dto.UserProfileDto;
import com.project.userservice.model.User;
import com.project.userservice.security.jwt.JwtUtils;
import com.project.userservice.security.request.LoginRequest;
import com.project.userservice.security.response.LoginResponse;
import com.project.userservice.security.service.UserDetailsImpl;
import com.project.userservice.service.RefreshTokenService;
import com.project.userservice.service.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    private final JwtUtils jwtUtils;

    
    private final RefreshTokenService refreshTokenService;

    @Autowired
    public AuthLoginFilter(AuthenticationManager authenticationManager, JwtUtils jwtUtils,
            RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        // jwtUtils = new JwtUtils();
        // refreshTokenService = new RefreshTokenService();
        this.jwtUtils = jwtUtils;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        try {

            ObjectMapper om = new ObjectMapper();
            LoginRequest loginInfo = om.readValue(request.getInputStream(), LoginRequest.class);
            // String username = request.getParameter("email");
            // String password = request.getParameter("password");

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    loginInfo.getEmail(), loginInfo.getPassword());

            Authentication authentication = authenticationManager.authenticate(authToken);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            return authentication;
        } catch (Exception e) {

            log.error(e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authentication) throws IOException {

        // UserDetailsS
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // Collect roles from the UserDetails
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();

        RefreshToken refreshToken = null;

        ObjectMapper om = new ObjectMapper();

        String data = null;

        String message = "Login Success";

        if (refreshTokenService.findByUserId(userDetails.getEmail()).isPresent()) {
            refreshTokenService.deleteByKey(userDetails.getEmail());
            message = "User attempted to log in multiply, logging out from previous logged in state";
        }
        
        String accessToken = jwtUtils.generateToken(userDetails.getEmail(), role, userDetails.is2faEnabled());        

        
        refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        
        LoginResponse loginInfo = new LoginResponse(userDetails.getUsername(),  roles, message);                
        
        
        try {

            data = om.writeValueAsString(loginInfo);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");

            response.setHeader(TokenType.ACCESS.getType(), accessToken);

            response.addCookie(
                    jwtUtils.createCookie(TokenType.REFRESH.getType(), refreshToken.getToken(), jwtUtils.getJwtRefreshExpirationMs() ));
            // refreshTokenService.deleteByUserId(userDetails.getId());
            // response.setHeader(HttpHeaders.SET_COOKIE,
            // jwtUtils.generateRefreshJwtCookie(refreshToken.getToken()).toString());
            response.getWriter().write(data);
            response.setStatus(HttpStatus.OK.value());
            
            
            log.info("Login Success");

        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            response.setStatus(401);
        }

    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException failed) {

        response.setStatus(401);
    }

}
