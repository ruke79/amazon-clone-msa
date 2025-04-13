package com.project.userservice.config;

import org.springframework.security.config.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

import com.project.common.message.dto.request.UserCreatedRequest;
import com.project.userservice.constants.AppRole;
import com.project.userservice.handler.OAuth2SuccessHandler;
import com.project.userservice.message.producer.UserCreatedProducer;
import com.project.userservice.model.Role;
import com.project.userservice.model.User;
import com.project.userservice.repository.RoleRepository;
import com.project.userservice.repository.UserRepository;
import com.project.userservice.security.AuthLoginFilter;
import com.project.userservice.security.AuthLogoutFilter;
import com.project.userservice.security.CustomAuthenticationProvider;
import com.project.userservice.security.RefreshToken;
import com.project.userservice.security.jwt.JwtAuthEntryPoint;
import com.project.userservice.security.jwt.JwtAuthFilter;
import com.project.userservice.security.jwt.JwtUtils;
import com.project.userservice.security.oauth2.repository.CustomClientRegistrationRepository;
import com.project.userservice.security.service.CustomOAuth2UserService;
import com.project.userservice.security.service.UserDetailsServiceImpl;
import com.project.userservice.service.NotificationService;
import com.project.userservice.service.RefreshTokenService;
import com.project.userservice.service.UserService;

import lombok.RequiredArgsConstructor;

import static org.springframework.security.config.Customizer.withDefaults;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

        private final CorsConfigurationSource corsConfigurationSource;

        private final JwtAuthEntryPoint unauthorizedHandler;

        private final CustomAuthenticationProvider customAuthenticationProvider;

        private final JwtUtils jwtUtils;

        private final RefreshTokenService refreshTokenService;

        private final NotificationService notificationService;

        private final CustomOAuth2UserService customOAuth2UserService;
        private final CustomClientRegistrationRepository customClientRegistrationRepository;
        private final OAuth2SuccessHandler oAuth2SuccessHandler;

        private final UserCreatedProducer userCreatedProducer;

        @Bean
        public JwtAuthFilter authenticationJwtTokenFilter() {
                return new JwtAuthFilter();
        }

        @Bean
        SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
                // http.csrf(csrf ->
                // csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                // .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                // .ignoringRequestMatchers("/api/user/**")
                // .ignoringRequestMatchers("/api/auth/public/**").ignoringRequestMatchers("/api/admin/**")
                // // .ignoringRequestMatchers("/api/order/**")
                // .ignoringRequestMatchers("/api/product/**")
                // );
                // http.cors(cors -> cors.configurationSource(corsConfigurationSource));
                http.cors(cors -> cors.disable());

                http.formLogin(login -> login.disable());
                http.logout((logout) -> logout.disable());

                http.csrf(AbstractHttpConfigurer::disable);
                http.authorizeHttpRequests((requests) -> requests
                                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                                .requestMatchers("/api/product/**").permitAll()
                                .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")
                                .requestMatchers("/api/review/**").permitAll()
                                .requestMatchers("/api/search/**").permitAll()
                                // .requestMatchers("/api/csrf-token").permitAll()
                                .requestMatchers("/api/token/**").permitAll()
                                .requestMatchers("/api/auth/user/**").hasAnyRole("USER", "ADMIN")
                                .requestMatchers("/api/auth/logout").hasAnyRole("USER", "ADMIN")
                                .requestMatchers("/api/auth/public/**").permitAll()
                                .requestMatchers("/registrationConfirm").permitAll()
                                .requestMatchers("/chat/**").hasAnyRole("USER", "ADMIN")
                                .requestMatchers("/sse").permitAll() //hasAnyRole("USER", "ADMIN")
                                .requestMatchers("/oauth2/**").permitAll()
                                .anyRequest().authenticated());

            

                http.oauth2Login((oauth2) -> oauth2
                                .loginPage("/signin")
                                .clientRegistrationRepository(
                                                customClientRegistrationRepository.clientRegistrationRepository())
                                .userInfoEndpoint(
                                                (userInfoEndpointConfig) -> userInfoEndpointConfig
                                                                .userService(customOAuth2UserService))
                                .successHandler(oAuth2SuccessHandler));

                http.exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler));

                http.addFilterBefore(authenticationJwtTokenFilter(),
                                AuthLoginFilter.class);

                http.addFilterBefore(new AuthLogoutFilter(jwtUtils, refreshTokenService), LogoutFilter.class);

                AuthLoginFilter loginFilter = new AuthLoginFilter(authenticationManager(http), jwtUtils,
                                refreshTokenService, notificationService);
                loginFilter.setFilterProcessesUrl("/api/auth/public/signin");
                http.addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);

                http.sessionManagement(
                                (sessionManagement) -> sessionManagement
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

                return http.build();
        }

        @Bean
        public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
                AuthenticationManagerBuilder authenticationManagerBuilder = http
                                .getSharedObject(AuthenticationManagerBuilder.class);
                authenticationManagerBuilder.authenticationProvider(customAuthenticationProvider);
                return authenticationManagerBuilder.build();
        }

        // @Bean
        // public CommandLineRunner initData(RoleRepository roleRepository,
        //                 UserRepository userRepository,
        //                 PasswordEncoder passwordEncoder) {
        //         return args -> {
        //                 Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
        //                                 .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_USER)));

        //                 Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
        //                                 .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_ADMIN)));

        //                 if (!userRepository.existsByUsername("user1")) {
        //                         User user1 = new User("user1", "user1@example.com",
        //                                         passwordEncoder.encode("1234"));
        //                         user1.setName("name1");
        //                         user1.setAccountNonLocked(false);
        //                         user1.setAccountNonExpired(true);
        //                         user1.setCredentialsNonExpired(true);
        //                         user1.setEnabled(true);
        //                         user1.setCredentialsExpiryDate(LocalDate.now().plusYears(1));
        //                         user1.setAccountExpiryDate(LocalDate.now().plusYears(1));
        //                         user1.setTwoFactorEnabled(false);
        //                         user1.setSignUpMethod("email");
        //                         user1.setRole(userRole);
        //                         user1 = userRepository.save(user1);

        //                         UserCreatedRequest dto = UserCreatedRequest.builder()
        //                                         .userId(user1.getUserId())
        //                                         .email(user1.getEmail())
        //                                         .image(user1.getImage())
        //                                         .nickname(user1.getName())
        //                                         .username(user1.getUsername())
        //                                         .build();

        //                         // userCreatedProducer.publish(dto);
        //                 } else {
        //                         User user1 = userRepository.findByUsername("user1").get();
        //                         UserCreatedRequest dto = UserCreatedRequest.builder()
        //                                         .userId(user1.getUserId())
        //                                         .email(user1.getEmail())
        //                                         .image(user1.getImage())
        //                                         .nickname(user1.getName())
        //                                         .username(user1.getUsername())
        //                                         .build();

        //                      //   userCreatedProducer.publish(dto);
        //                 }

        //                 if (!userRepository.existsByUsername("user2")) {
        //                         User user1 = new User("user2", "user2@example.com",
        //                                         passwordEncoder.encode("1234"));
        //                         user1.setAccountNonLocked(false);
        //                         user1.setAccountNonExpired(true);
        //                         user1.setCredentialsNonExpired(true);
        //                         user1.setEnabled(true);
        //                         user1.setCredentialsExpiryDate(LocalDate.now().plusYears(1));
        //                         user1.setAccountExpiryDate(LocalDate.now().plusYears(1));
        //                         user1.setTwoFactorEnabled(false);
        //                         user1.setSignUpMethod("email");
        //                         user1.setRole(userRole);
        //                         user1.setName("name2");
        //                         user1 = userRepository.save(user1);

        //                         UserCreatedRequest dto = UserCreatedRequest.builder()
        //                                         .userId(user1.getUserId())
        //                                         .email(user1.getEmail())
        //                                         .image(user1.getImage())
        //                                         .nickname(user1.getName())
        //                                         .username(user1.getUsername())
        //                                         .build();

        //                         // userCreatedProducer.publish(dto);

        //                 } else {
        //                         User user1 = userRepository.findByUsername("user2").get();
        //                         UserCreatedRequest dto = UserCreatedRequest.builder()
        //                                         .userId(user1.getUserId())
        //                                         .email(user1.getEmail())
        //                                         .image(user1.getImage())
        //                                         .nickname(user1.getName())
        //                                         .username(user1.getUsername())
        //                                         .build();

        //                      //  userCreatedProducer.publish(dto);
        //                 }

        //                 if (!userRepository.existsByUsername("admin")) {
        //                 User admin = new User("admin", "admin@example.com",
        //                 passwordEncoder.encode("adminPass"));
        //                 admin.setAccountNonLocked(true);
        //                 admin.setAccountNonExpired(true);
        //                 admin.setCredentialsNonExpired(true);
        //                 admin.setEnabled(true);
        //                 admin.setCredentialsExpiryDate(LocalDate.now().plusYears(1));
        //                 admin.setAccountExpiryDate(LocalDate.now().plusYears(1));
        //                 admin.setTwoFactorEnabled(false);
        //                 admin.setSignUpMethod("email");
        //                 admin.setRole(adminRole);
        //                 userRepository.save(admin);
        //                 }
        //         };
        // }
}
