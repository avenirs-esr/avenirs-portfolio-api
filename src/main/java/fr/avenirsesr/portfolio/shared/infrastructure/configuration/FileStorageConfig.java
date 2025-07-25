package fr.avenirsesr.portfolio.shared.infrastructure.configuration;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "file.storage")
public class FileStorageConfig {
  private String baseUrl;
  public static String BASE_URL;

  @PostConstruct
  private void init() {
    BASE_URL = baseUrl;
  }
}
