package fr.avenirsesr.portfolio.file.infrastructure.service;

import fr.avenirsesr.portfolio.file.domain.port.output.seeder.FileDataGenerator;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.seeder.data.FileFakerDataGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileDataGeneratorConfig {
  @Bean
  public FileDataGenerator fileDataGenerator() {
    return new FileFakerDataGenerator();
  }
}
