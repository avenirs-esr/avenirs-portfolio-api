package fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.skill.domain.port.input.SkillService;
import fr.avenirsesr.portfolio.student.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.student.association.domain.port.output.strategy.AssociationStrategy;
import fr.avenirsesr.portfolio.student.skill.domain.port.input.DeclaredSkillProgressService;
import fr.avenirsesr.portfolio.student.skill.domain.service.DeclaredSkillAssociationStrategy;
import fr.avenirsesr.portfolio.student.skill.domain.service.DeclaredSkillProgressServiceImpl;
import fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.repository.DeclaredSkillProgressDatabaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DeclaredSkillProgressServiceConfig {
  private final SkillService skillService;
  private final DeclaredSkillProgressDatabaseRepository declaredSkillProgressRepository;
  private final LoggedInUserService loggedInUserService;
  private final AssociationService associationService;

  @Bean
  public DeclaredSkillProgressService declaredSkillProgressService() {
    return new DeclaredSkillProgressServiceImpl(
        skillService, declaredSkillProgressRepository, loggedInUserService, associationService);
  }

  @Bean
  public AssociationStrategy declaredSkillAssociationStrategy() {
    return new DeclaredSkillAssociationStrategy(
        declaredSkillProgressService(), loggedInUserService);
  }
}
