package com.deepaksir.config;

import com.deepaksir.security.JwtAuthenticationEntryPoint;
import com.deepaksir.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            )
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth

                // ============ PUBLIC ENDPOINTS (No token needed) ============
                .requestMatchers(
                    "/auth/**",
                    "/admin/auth/login",
                    "/admin/auth/verify",
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/public/**",
                    "/files/**",
                    "/uploads/**"
                ).permitAll()

                // ============ ADMIN-ONLY (role: ADMIN) ============
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // Course creation/update/delete - only admin
                .requestMatchers(HttpMethod.POST, "/courses/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/courses/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/courses/**").hasRole("ADMIN")

                // Subject creation/update/delete - only admin
                .requestMatchers(HttpMethod.POST, "/subjects/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/subjects/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/subjects/**").hasRole("ADMIN")

                // Question creation/update/delete - only admin
                .requestMatchers(HttpMethod.POST, "/questions/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/questions/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/questions/**").hasRole("ADMIN")

                // Notes creation/update/delete - only admin
                .requestMatchers(HttpMethod.POST, "/notes/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/notes/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/notes/**").hasRole("ADMIN")

                // Presentation upload/delete - only admin
                .requestMatchers(HttpMethod.POST, "/presentations/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/presentations/**").hasRole("ADMIN")

                // Video upload/update/delete - only admin
                .requestMatchers(HttpMethod.POST, "/videos/upload").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/videos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/videos/**").hasRole("ADMIN")

                // Test creation/update/delete - only admin
                .requestMatchers(HttpMethod.POST, "/tests/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/tests/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/tests/**").hasRole("ADMIN")

                // Notification broadcast - only admin
                .requestMatchers(HttpMethod.POST, "/notifications/broadcast").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/notifications/send").hasRole("ADMIN")

                // ============ ADMIN + INSTRUCTOR (Classroom) ============
                .requestMatchers("/classroom/**").hasAnyRole("ADMIN", "INSTRUCTOR")

                // ============ STUDENT-ONLY ============
                .requestMatchers("/courses/*/enroll").hasRole("STUDENT")
                .requestMatchers("/courses/enrollments/my-courses").hasRole("STUDENT")
                .requestMatchers("/progress/**").hasRole("STUDENT")

                // ============ EVERYTHING ELSE requires authentication ============
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT",
                "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization",
                "Content-Type", "X-Requested-With", "Accept", "Origin"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}