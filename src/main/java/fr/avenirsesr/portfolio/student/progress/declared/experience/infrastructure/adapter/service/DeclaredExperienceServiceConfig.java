package fr.avenirsesr.portfolio.student.progress.declared.experience.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.association.domain.service.AssociationSearchHelper;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.port.input.DeclaredExperienceService;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.port.output.repository.DeclaredExperienceRepository;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.service.DeclaredExperienceServiceImpl;
import fr.avenirsesr.portfolio.trace.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.user.domain.port.input.StudentService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
@AllArgsConstructor
public class DeclaredExperienceServiceConfig {
  private final LoggedInUserService loggedInUserService;
  private final AssociationService associationService;
  private final AssociationSearchHelper associationSearchHelper;
  private final DeclaredExperienceRepository experienceRepository;
  private final StudentService studentService;

  @Bean
  public DeclaredExperienceService declaredExperienceService(@Lazy TraceService traceService) {
    return new DeclaredExperienceServiceImpl(
        loggedInUserService,
        associationService,
        associationSearchHelper,
        traceService,
        experienceRepository,
        studentService);
  }
}
