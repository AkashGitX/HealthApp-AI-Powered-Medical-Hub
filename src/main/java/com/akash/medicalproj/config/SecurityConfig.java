package com.akash.medicalproj.config;

import com.akash.medicalproj.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                // Public pages
                .requestMatchers("/", "/home", "/about", "/mental-health").permitAll()
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                // Dashboard pages — public shells; JS handles auth redirect
                .requestMatchers("/dashboard/**").permitAll()
                // Service pages — public (JS handles auth for protected actions)
                .requestMatchers("/services/**").permitAll()
                // Public API reads
                .requestMatchers("/api/doctor/all").permitAll()
                .requestMatchers("/api/doctor/online").permitAll()
                .requestMatchers("/api/doctor/offline").permitAll()
                .requestMatchers("/api/provider/all").permitAll()
                .requestMatchers("/api/payment/config").permitAll()
                .requestMatchers("/api/eye-test/**").permitAll()
                // Protected API endpoints
                .requestMatchers("/api/doctor/profile", "/api/doctor/availability").hasRole("DOCTOR")
                .requestMatchers("/api/provider/profile").hasRole("SERVICE_PROVIDER")
                .requestMatchers("/api/appointments/**").authenticated()
                .requestMatchers("/api/ai/**").authenticated()
                .requestMatchers("/api/payment/**").authenticated()
                .requestMatchers("/api/tests/**").authenticated()
                .requestMatchers("/api/user/**").hasRole("USER")
                .anyRequest().authenticated()
            )
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            // Allow Replit proxy iframe embedding
            .headers(headers -> headers
                .frameOptions(frame -> frame.disable())
                .contentSecurityPolicy(csp -> csp.policyDirectives("frame-ancestors *")));

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
