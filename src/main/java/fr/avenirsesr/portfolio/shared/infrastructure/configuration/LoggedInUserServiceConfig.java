package fr.avenirsesr.portfolio.shared.infrastructure.configuration;

import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.shared.domain.service.LoggedInUserServiceImpl;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class LoggedInUserServiceConfig {
  private final StudentRepository studentRepository;

  @Bean
  public LoggedInUserService loggedInUserService() {
    return new LoggedInUserServiceImpl(studentRepository);
  }
}
