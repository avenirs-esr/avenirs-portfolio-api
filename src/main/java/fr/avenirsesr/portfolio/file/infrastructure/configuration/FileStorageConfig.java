package fr.avenirsesr.portfolio.file.infrastructure.configuration;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
public class FileStorageConfig {
  @Value("${file.storage.base-url}")
  private String baseUrl;

  @Value("${file.storage.local-path}")
  private String storagePath;

  public static String BASE_URL;
  public static String STORAGE_PATH;

  @PostConstruct
  private void init() {
    BASE_URL = baseUrl;
    STORAGE_PATH = storagePath;
  }
}
