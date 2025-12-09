package fr.avenirsesr.portfolio.student.progress.declared.program.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.declared.program.domain.port.input.DeclaredProgramService;
import fr.avenirsesr.portfolio.student.progress.declared.program.domain.port.output.DeclaredProgramRepository;
import fr.avenirsesr.portfolio.student.progress.declared.program.domain.service.DeclaredProgramServiceImpl;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StudentRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class DeclaredProgramServiceConfig {
  private final StudentRepository studentRepository;
  private final DeclaredProgramRepository declaredProgramRepository;
  private final LoggedInUserService loggedInUserService;

  @Bean
  public DeclaredProgramService declaredProgramService() {
    return new DeclaredProgramServiceImpl(
        studentRepository, declaredProgramRepository, loggedInUserService);
  }
}
