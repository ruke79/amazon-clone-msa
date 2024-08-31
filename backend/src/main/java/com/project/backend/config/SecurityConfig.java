package com.project.backend.config;


import org.springframework.security.config.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.core.userdetails.User;
// import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

import com.project.backend.model.AppRole;
import com.project.backend.model.RefreshToken;
import com.project.backend.model.Role;
import com.project.backend.model.User;
import com.project.backend.repository.RoleRepository;
import com.project.backend.repository.UserRepository;
import com.project.backend.security.AuthLoginFilter;
import com.project.backend.security.AuthLogoutFilter;
import com.project.backend.security.jwt.JwtAuthEntryPoint;
import com.project.backend.security.jwt.JwtAuthFilter;
import com.project.backend.security.jwt.JwtUtils;
import com.project.backend.service.RefreshTokenService;
import com.project.backend.service.UserService;

import static org.springframework.security.config.Customizer.withDefaults;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true)
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    
    private final JwtAuthEntryPoint unauthorizedHandler;

    
    @Lazy
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    private final JwtUtils jwtUtils;

    private final RefreshTokenService refreshTokenService;

    private final UserService userService;

    @Bean
    public JwtAuthFilter authenticationJwtTokenFilter() {
        return new JwtAuthFilter();
    }


    @Autowired
    public SecurityConfig(JwtAuthEntryPoint unauthorizedHandler, OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler,
            JwtUtils jwtUtils, AuthenticationConfiguration authenticationConfiguration, RefreshTokenService refreshTokenService, UserService userService) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.unauthorizedHandler = unauthorizedHandler;
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
        this.jwtUtils = jwtUtils;
        this.refreshTokenService = refreshTokenService;
        this.userService = userService;
    }


    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf ->
                csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                .ignoringRequestMatchers("/api/user/**")
                        .ignoringRequestMatchers("/api/auth/public/**").ignoringRequestMatchers("/api/admin/**")
                        //.ignoringRequestMatchers("/api/order/**")
                        //.ignoringRequestMatchers("/api/product/**")
        );
        //http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests((requests)
                -> requests
                //.requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/admin/**").permitAll()
                .requestMatchers("/api/product/**").permitAll()
                .requestMatchers("/api/user/**").permitAll()
                .requestMatchers("/api/search/**").permitAll()
                .requestMatchers("/api/csrf-token").permitAll()
                .requestMatchers("/api/refreshtoken").permitAll()
                .requestMatchers("/api/auth/public/**").permitAll()
                .requestMatchers("/registrationConfirm").permitAll()
                .requestMatchers("/oauth2/**").permitAll()
                .anyRequest().authenticated())
                .oauth2Login(oauth2 -> {
                    oauth2.successHandler(oAuth2LoginSuccessHandler);
                });
        http.exceptionHandling(exception
                -> exception.authenticationEntryPoint(unauthorizedHandler));
        http.addFilterBefore(authenticationJwtTokenFilter(),
                AuthLoginFilter.class);
        http .addFilterBefore(new AuthLogoutFilter(jwtUtils, refreshTokenService, userService ), LogoutFilter.class);
        http.addFilterAt(new AuthLoginFilter(authenticationManager(authenticationConfiguration), jwtUtils, refreshTokenService), UsernamePasswordAuthenticationFilter.class);
        http.sessionManagement((sessionManagement) -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .invalidSessionUrl("/invalidSession.html")
             .maximumSessions(1)
              .sessionRegistry(sessionRegistry()));
                
        //http.formLogin(withDefaults());
        http.httpBasic(withDefaults());
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository,
                                      UserRepository userRepository,
                                      PasswordEncoder passwordEncoder) {
        return args -> {
            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_USER)));

            Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                    .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_ADMIN)));

            // if (!userRepository.existsByUserName("user1")) {
            //     User user1 = new User("user1", "user1@example.com",
            //             passwordEncoder.encode("password1"));
            //     user1.setAccountNonLocked(false);
            //     user1.setAccountNonExpired(true);
            //     user1.setCredentialsNonExpired(true);
            //     user1.setEnabled(true);
            //     user1.setCredentialsExpiryDate(LocalDate.now().plusYears(1));
            //     user1.setAccountExpiryDate(LocalDate.now().plusYears(1));
            //     user1.setTwoFactorEnabled(false);
            //     user1.setSignUpMethod("email");
            //     user1.setRole(userRole);
            //     userRepository.save(user1);
            // }

            // if (!userRepository.existsByUserName("admin")) {
            //     User admin = new User("admin", "admin@example.com",
            //             passwordEncoder.encode("adminPass"));
            //     admin.setAccountNonLocked(true);
            //     admin.setAccountNonExpired(true);
            //     admin.setCredentialsNonExpired(true);
            //     admin.setEnabled(true);
            //     admin.setCredentialsExpiryDate(LocalDate.now().plusYears(1));
            //     admin.setAccountExpiryDate(LocalDate.now().plusYears(1));
            //     admin.setTwoFactorEnabled(false);
            //     admin.setSignUpMethod("email");
            //     admin.setRole(adminRole);
            //     userRepository.save(admin);
            // }
        };
    }
}
