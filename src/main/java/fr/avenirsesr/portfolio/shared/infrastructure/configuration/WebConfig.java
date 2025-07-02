package fr.avenirsesr.portfolio.shared.infrastructure.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class WebConfig {

  @Value("${cors.allowed-origins}")
  private String allowedOrigins;

  @Value("${cors.allowed-methods}")
  private String allowedMethodsString;

  @Value("${cors.allowed-headers}")
  private String allowedHeadersString;

  @Bean
  public CorsFilter corsFilter() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration();

    config.setAllowCredentials(true);
    config.addAllowedOrigin(allowedOrigins);

    for (String header : allowedHeadersString.split(",")) {
      config.addAllowedHeader(header.trim());
    }

    for (String method : allowedMethodsString.split(",")) {
      config.addAllowedMethod(method.trim());
    }

    source.registerCorsConfiguration("/**", config);
    return new CorsFilter(source);
  }
}
