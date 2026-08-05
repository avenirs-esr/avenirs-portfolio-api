package fr.avenirsesr.portfolio.student.program.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.program.domain.port.input.DeclaredProgramService;
import fr.avenirsesr.portfolio.student.program.domain.port.output.DeclaredProgramRepository;
import fr.avenirsesr.portfolio.student.program.domain.service.DeclaredProgramServiceImpl;
import fr.avenirsesr.portfolio.user.domain.port.input.StudentService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class DeclaredProgramServiceConfig {
  private final StudentService studentService;
  private final DeclaredProgramRepository declaredProgramRepository;
  private final LoggedInUserService loggedInUserService;

  @Bean
  public DeclaredProgramService declaredProgramService() {
    return new DeclaredProgramServiceImpl(
        studentService, declaredProgramRepository, loggedInUserService);
  }
}
