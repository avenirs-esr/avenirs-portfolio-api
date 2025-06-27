package fr.avenirsesr.portfolio.api.infrastructure.adapter.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

  @Value("${security.enabled:true}")
  private boolean securityEnabled;

  @Value("${security.permit-all-paths}")
  private String[] permitAllPaths;

  public SecurityConfig(
      CustomAuthenticationEntryPoint customAuthenticationEntryPoint) {
    this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            authz -> authz.requestMatchers(permitAllPaths).permitAll().anyRequest().authenticated())
        .exceptionHandling(
            exception -> exception.authenticationEntryPoint(customAuthenticationEntryPoint));

    if (securityEnabled) {
      http.addFilterBefore(new HmacAuthenticationFilter(Arrays.toString(permitAllPaths)), UsernamePasswordAuthenticationFilter.class);
    } else {
      http.addFilterBefore(new DevAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    return http.build();
  }
}
