package fr.avenirsesr.portfolio.program.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.program.domain.port.input.InstitutionService;
import fr.avenirsesr.portfolio.program.domain.service.InstitutionServiceImpl;
import fr.avenirsesr.portfolio.student.progress.domain.port.output.repository.StudentProgressRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class InstitutionServiceConfig {
  private final StudentProgressRepository studentProgressRepository;

  public InstitutionServiceConfig(StudentProgressRepository studentProgressRepository) {
    this.studentProgressRepository = studentProgressRepository;
  }

  @Bean
  public InstitutionService institutionService() {
    return new InstitutionServiceImpl(studentProgressRepository);
  }
}
