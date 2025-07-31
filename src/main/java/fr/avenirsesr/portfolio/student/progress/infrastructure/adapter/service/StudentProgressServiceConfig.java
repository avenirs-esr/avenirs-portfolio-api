package fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.student.progress.domain.port.input.StudentProgressService;
import fr.avenirsesr.portfolio.student.progress.domain.service.StudentProgressServiceImpl;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.repository.StudentProgressDatabaseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class StudentProgressServiceConfig {

  private final StudentProgressDatabaseRepository studentProgressRepository;

  public StudentProgressServiceConfig(StudentProgressDatabaseRepository studentProgressRepository) {
    this.studentProgressRepository = studentProgressRepository;
  }

  @Bean
  public StudentProgressService studentProgressService() {
    return new StudentProgressServiceImpl(studentProgressRepository);
  }
}
