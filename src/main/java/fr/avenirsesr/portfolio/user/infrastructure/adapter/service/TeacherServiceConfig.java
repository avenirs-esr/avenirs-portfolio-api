package fr.avenirsesr.portfolio.user.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.user.domain.port.input.TeacherService;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.TeacherRepository;
import fr.avenirsesr.portfolio.user.domain.service.TeacherServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class TeacherServiceConfig {
  private final TeacherRepository teacherRepository;

  @Bean
  public TeacherService teacherService() {
    return new TeacherServiceImpl(teacherRepository);
  }
}
