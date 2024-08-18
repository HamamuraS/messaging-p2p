package com.santi.messagesp2p.config;

import com.santi.messagesp2p.config.tokens.JwtAuthenticationFilter;
import com.santi.messagesp2p.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Autowired
  private final UserService userDetailsService;
  @Autowired
  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  @Autowired
  private final PasswordEncoder passwordEncoder;



  public SecurityConfig(
      JwtAuthenticationFilter jwtAuthenticationFilter,
      UserService userDetailsService,
      PasswordEncoder passwordEncoder
  ) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    this.userDetailsService = userDetailsService;
    this.passwordEncoder = passwordEncoder;
  }

  /**
   * Used to validate user logins.
   *
   * @return DaoAuthenticationProvider implementation.
   */
  public DaoAuthenticationProvider daoAuthenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder);
    return authProvider;
  }

  /**
   * Used by AuthController to authenticate users with authenticate() method.
   *
   * @return AuthenticationManager implementation.
   */
  @Bean
  public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
    return http.getSharedObject(AuthenticationManagerBuilder.class)
        .authenticationProvider(daoAuthenticationProvider())
        .build();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)  // Deshabilita CSRF para permitir POST
        .authorizeHttpRequests(authz -> authz
            //.requestMatchers("/api/users").permitAll()  // Permite todas las solicitudes a /api/users sin autenticación
            .requestMatchers("/api/auth/*").permitAll()
            .requestMatchers("/ws/**").permitAll()
            .anyRequest().authenticated()     // Todas las demás solicitudes requieren autenticación
        )
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    return http.build();
  }
}
