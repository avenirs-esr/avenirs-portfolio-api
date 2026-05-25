package fr.avenirsesr.portfolio.user.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.selfknowledge.domain.port.input.SelfKnowledgeService;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.user.domain.port.input.StudentService;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StudentRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserRepository;
import fr.avenirsesr.portfolio.user.domain.service.StudentServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
@RequiredArgsConstructor
public class StudentServiceConfig {
  private final StudentRepository studentRepository;
  private final UserRepository userRepository;

  @Bean
  public StudentService studentService(
      @Lazy SelfKnowledgeService selfKnowledgeService,
      @Lazy LoggedInUserService loggedInUserService) {
    return new StudentServiceImpl(
        studentRepository, userRepository, selfKnowledgeService, loggedInUserService);
  }
}
