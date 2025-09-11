package fr.avenirsesr.portfolio.file.infrastructure.service;

import fr.avenirsesr.portfolio.file.domain.port.output.seeder.FileDataGenerator;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.seeder.data.FileCSVDataGenerator;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.seeder.data.FileFakerDataGenerator;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.data.ESeederSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileDataGeneratorConfig {
  @Value("${seeder.source}")
  private ESeederSource seederSource;

  @Bean
  public FileDataGenerator fileDataGenerator() {
    return switch (seederSource) {
      case CSV -> new FileCSVDataGenerator();
      case FAKER -> new FileFakerDataGenerator();
    };
  }
}
