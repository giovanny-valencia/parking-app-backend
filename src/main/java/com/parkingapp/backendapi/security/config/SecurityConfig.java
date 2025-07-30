package com.parkingapp.backendapi.security.config;

import com.parkingapp.backendapi.security.jwt.JwtAuthenticationFilter;
import com.parkingapp.backendapi.security.service.UserDetailsServiceImpl;
import java.util.Arrays;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

  private UserDetailsServiceImpl userDetailsService;

  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.cors(Customizer.withDefaults())
        .sessionManagement(
            session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Jwt are stateless
        .csrf(AbstractHttpConfigurer::disable) // API is stateless (no cookies), safe to disable
        .formLogin(AbstractHttpConfigurer::disable) // client handles
        .httpBasic(AbstractHttpConfigurer::disable) // client handles
        // configure CORS to only allow requests from client origin
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/api/v1/auth/**") // public endpoints
                    .permitAll()
                    .anyRequest()
                    .authenticated()) // all other endpoints are authenticated
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(
        Arrays.asList(
            "http://localhost:8081", // local development
            "exp://192.168.1.XX:8081" // Expo Go on device (example for now)
            // Production client
            ));
    configuration.setAllowedMethods(
        Arrays.asList(
            "GET", "POST", "PUT", "DELETE",
            "OPTIONS")); // added "OPTIONS" as a potential dev env testing caution. Expo might
    // behave like web and issue "preflight OPTIONS" requests. No need for web
    // support now, but this API may be used by gov admins for metrics
    configuration.setAllowedHeaders(
        Arrays.asList("Authorization", "Content-Type")); // JWT auth headers
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration); // apply CORS config to all paths

    return source;
  }

  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
