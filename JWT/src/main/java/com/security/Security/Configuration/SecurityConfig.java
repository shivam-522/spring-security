package com.security.Security.Configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.security.Security.Config.CustomUserDetailsService;
import com.security.Security.JWT.JwtFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JwtFilter jwtFilter;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
         .csrf(csrf -> csrf.disable())
         .authorizeHttpRequests(auth -> auth
                 .requestMatchers("/public/**", "/authenticate/**", "/authenticate").permitAll()
                 .anyRequest().authenticated())
         .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))//Don’t use HTTP session. Every request is authenticated using JWT. No memory usage, no server-side session tracking.
         .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)//Ensures your JWT validation happens before Spring tries to handle login. It sets the authenticated user manually using token.
         .build();

           // return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Password compare isse hota hai jo hum plain password dete h request header me ye use encode karke match karta h db wale se
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
