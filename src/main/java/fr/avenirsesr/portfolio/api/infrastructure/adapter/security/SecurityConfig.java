package fr.avenirsesr.portfolio.api.infrastructure.adapter.security;

import java.util.Arrays;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
  private final HmacAuthenticationFilter hmacAuthenticationFilter;

  @Value("${security.permit-all-paths}")
  private String[] permitAllPaths;

  @Value("${cors.allowed-origins}")
  private String allowedOrigins;

  @Value("${cors.allowed-methods}")
  private String allowedMethodsString;

  @Value("${cors.allowed-headers}")
  private String allowedHeadersString;

  public SecurityConfig(
      CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
      HmacAuthenticationFilter hmacAuthenticationFilter) {
    this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
    this.hmacAuthenticationFilter = hmacAuthenticationFilter;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .authorizeHttpRequests(
            authz -> authz.requestMatchers(permitAllPaths).permitAll().anyRequest().authenticated())
        .exceptionHandling(
            exception -> exception.authenticationEntryPoint(customAuthenticationEntryPoint))
        .addFilterBefore(hmacAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Collections.singletonList(allowedOrigins));
    configuration.setAllowedMethods(Arrays.asList(allowedMethodsString.split(",")));
    configuration.setAllowedHeaders(Arrays.asList(allowedHeadersString.split(",")));
    configuration.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
