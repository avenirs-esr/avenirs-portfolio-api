package fr.avenirsesr.portfolio.program.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.program.domain.port.input.TrainingPathService;
import fr.avenirsesr.portfolio.program.domain.port.output.TrainingPathRepository;
import fr.avenirsesr.portfolio.program.domain.service.TrainingPathServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TrainingPathServiceConfig {
  private final TrainingPathRepository trainingPathRepository;

  public TrainingPathServiceConfig(TrainingPathRepository trainingPathRepository) {
    this.trainingPathRepository = trainingPathRepository;
  }

  @Bean
  public TrainingPathService trainingPathService() {
    return new TrainingPathServiceImpl(trainingPathRepository);
  }
}
