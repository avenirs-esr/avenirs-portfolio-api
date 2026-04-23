package fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.imported.domain.port.input.StudentProgressService;
import fr.avenirsesr.portfolio.student.progress.imported.domain.service.StudentProgressServiceImpl;
import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.repository.StudentProgressDatabaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class StudentProgressServiceConfig {
  private final StudentProgressDatabaseRepository studentProgressRepository;
  private final LoggedInUserService loggedInUserService;

  @Bean
  public StudentProgressService studentProgressService() {
    return new StudentProgressServiceImpl(studentProgressRepository, loggedInUserService);
  }
}
