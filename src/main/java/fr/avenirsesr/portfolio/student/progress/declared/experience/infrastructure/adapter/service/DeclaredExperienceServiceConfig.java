package fr.avenirsesr.portfolio.student.progress.declared.experience.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.DeclaredExperienceServiceImpl;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.port.input.service.DeclaredExperienceService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class DeclaredExperienceServiceConfig {

  @Bean
  public DeclaredExperienceService declaredExperienceService() {
    return new DeclaredExperienceServiceImpl();
  }
}
