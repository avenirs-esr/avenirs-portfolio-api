package fr.avenirsesr.portfolio.security.infrastructure.configuration;

import fr.avenirsesr.portfolio.security.infrastructure.filter.DevAuthenticationFilter;
import fr.avenirsesr.portfolio.security.infrastructure.filter.HmacAuthenticationFilter;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

  @Value("${security.disabled:false}")
  private boolean securityDisabled;

  @Value("${security.permit-all-paths}")
  private String[] permitAllPaths;

  @Value("${cors.allowed-origins}")
  private String allowedOriginsString;

  @Value("${cors.allowed-methods}")
  private String allowedMethodsString;

  @Value("${cors.allowed-headers}")
  private String allowedHeadersString;

  @Value("${cors.allow-credentials}")
  private boolean allowCredentials;

  public SecurityConfig(CustomAuthenticationEntryPoint customAuthenticationEntryPoint) {
    this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .authorizeHttpRequests(
            authz -> authz.requestMatchers(permitAllPaths).permitAll().anyRequest().authenticated())
        .exceptionHandling(
            exception -> exception.authenticationEntryPoint(customAuthenticationEntryPoint));

    if (securityDisabled) {
      log.warn("Security is disabled");
      http.addFilterBefore(
          new DevAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    } else {
      http.addFilterBefore(
          new HmacAuthenticationFilter(String.join(",", permitAllPaths)),
          UsernamePasswordAuthenticationFilter.class);
    }

    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    for (String origin : allowedOriginsString.split(",")) {
      configuration.addAllowedOrigin(origin.trim());
    }

    for (String method : allowedMethodsString.split(",")) {
      configuration.addAllowedMethod(method.trim());
    }

    for (String header : allowedHeadersString.split(",")) {
      configuration.addAllowedHeader(header.trim());
    }

    configuration.setAllowCredentials(allowCredentials);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
