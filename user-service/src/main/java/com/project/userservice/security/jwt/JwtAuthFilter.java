package com.project.userservice.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.project.common.constants.TokenStatus;
import com.project.userservice.constants.TokenType;
import com.project.userservice.dto.CustomOAuth2User;
import com.project.userservice.dto.UserProfileDto;
import com.project.userservice.security.service.UserDetailsServiceImpl;

import io.jsonwebtoken.ExpiredJwtException;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.info("AuthTokenFilter called for URI: {}", request.getRequestURI());

        String requestUri = request.getRequestURI();

        if (requestUri.matches("/registrationConfirm(?:\\/.*)?$")) {

            filterChain.doFilter(request, response);
            return;
        }

        if (requestUri.matches("/api/auth/public/signin(?:\\/.*)?$")) {

            filterChain.doFilter(request, response);
            return;
        }

        if (requestUri.matches("^\\/oauth2(?:\\/.*)?$")) {

            filterChain.doFilter(request, response);
            return;
        }

        if (requestUri.matches("/api/token(?:\\/.*)?$")) {

            filterChain.doFilter(request, response);
            return;
        }



        
        String accessToken = parseJwt(request);

        
        
        if (accessToken != null) {

        
            try {

                // TokenStatus tokenStatus = jwtUtils.validateJwtToken(accessToken);

                // if (tokenStatus == TokenStatus.EXPIRED) {

                //     PrintWriter writer = response.getWriter();
                //     writer.print("access token expired");
                //     // response status code
                //     response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                //     return;
                // }

                //if (tokenStatus == TokenStatus.VAILD) {

                    String blackLisTToken = (String) redisTemplate.opsForValue().get(accessToken);

                    log.info("blackLisTToken : {}", blackLisTToken);

                    if (ObjectUtils.isEmpty(blackLisTToken)) {

                        String email = jwtUtils.getIdFromJwtToken(accessToken);

                        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());
                        log.info("Roles from JWT: {}", userDetails.getAuthorities());

                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                //}
            } catch (Exception e) {
                // logger.error("Cannot set user authentication: {}", e);
                // response body
                PrintWriter writer = response.getWriter();
                writer.print("invalid access token");

                // response status code
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

        } else // 토큰이 없다면 다음 필터로 넘김
        {

            filterChain.doFilter(request, response);

            return;
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String jwt = jwtUtils.getJwtFromHeader(request);
        return jwt;
    }
}
