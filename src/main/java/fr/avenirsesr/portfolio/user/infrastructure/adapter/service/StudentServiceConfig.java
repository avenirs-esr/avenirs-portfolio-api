package fr.avenirsesr.portfolio.user.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.selfknowledge.domain.port.input.SelfKnowledgeService;
import fr.avenirsesr.portfolio.user.domain.port.input.StudentService;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StudentRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserRepository;
import fr.avenirsesr.portfolio.user.domain.service.StudentServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class StudentServiceConfig {
  private final StudentRepository studentRepository;
  private final UserRepository userRepository;
  private final SelfKnowledgeService selfKnowledgeService;

  @Bean
  public StudentService studentService() {
    return new StudentServiceImpl(studentRepository, userRepository, selfKnowledgeService);
  }
}
