package io.gaboja9.mockstock.global.config;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(
                        auth ->
                                auth.requestMatchers(
                                                "/swagger-ui/**",
                                                "/v3/api-docs/**",
                                                "/actuator/**",
                                                "/auth/signup",
                                                "/auth/login",
                                                "/auth/email",
                                                "/auth/email/passwordFind",
                                                "/auth/passwordFind",
                                                "/auth/refresh",
                                                "/api/stocks",
                                                "/ws-stock",
                                                "/ws-stock/**",
                                                "/ranks/**",
                                                "/payments/**",
                                                "/stocks/**",
                                                "mockPrice/**")
                                        .permitAll()
                                        .anyRequest()
                                        .authenticated())
                .oauth2Login(
                        oauth -> {
                            oauth.successHandler(oAuth2SuccessHandler);
                        })
                .formLogin(form -> form.disable())
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(
                        jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        //        configuration.setAllowedOrigins(
        //                List.of(
        //                        "https://mock-stock.pages.dev",
        //                        "http://localhost:3000",
        //                        "https://mockstocks.duckdns.org",
        //                        "https://mockstocks.duckdns.org:3000",
        //                        "http://127.0.0.1:5500",
        //                        "http://127.0.0.1:5173"));

        configuration.setAllowedOriginPatterns(List.of("*"));

        configuration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        configuration.setAllowedHeaders(List.of("*"));

        configuration.setAllowCredentials(true);

        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
