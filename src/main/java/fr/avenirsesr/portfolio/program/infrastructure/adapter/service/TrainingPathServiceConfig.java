package fr.avenirsesr.portfolio.program.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.program.domain.port.input.TrainingPathService;
import fr.avenirsesr.portfolio.program.domain.port.output.repository.TrainingPathRepository;
import fr.avenirsesr.portfolio.program.domain.service.TrainingPathServiceImpl;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class TrainingPathServiceConfig {
  private final StudentRepository studentRepository;
  private final TrainingPathRepository trainingPathRepository;

  @Bean
  public TrainingPathService trainingPathService() {
    return new TrainingPathServiceImpl(studentRepository, trainingPathRepository);
  }
}
