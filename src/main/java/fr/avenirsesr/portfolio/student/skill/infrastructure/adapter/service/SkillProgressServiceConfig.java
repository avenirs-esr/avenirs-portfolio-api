package fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.skill.domain.port.input.SkillService;
import fr.avenirsesr.portfolio.student.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.student.association.domain.port.output.strategy.AssociationStrategy;
import fr.avenirsesr.portfolio.student.skill.domain.port.input.SkillProgressService;
import fr.avenirsesr.portfolio.student.skill.domain.service.SkillProgressAssociationStrategy;
import fr.avenirsesr.portfolio.student.skill.domain.service.SkillProgressServiceImpl;
import fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.repository.SkillProgressDatabaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SkillProgressServiceConfig {
  private final SkillService skillService;
  private final SkillProgressDatabaseRepository skillProgressRepository;
  private final LoggedInUserService loggedInUserService;
  private final AssociationService associationService;

  @Bean
  public SkillProgressService skillProgressService() {
    return new SkillProgressServiceImpl(
        skillService, skillProgressRepository, loggedInUserService, associationService);
  }

  @Bean
  public AssociationStrategy skillProgressAssociationStrategy() {
    return new SkillProgressAssociationStrategy(skillProgressService(), loggedInUserService);
  }
}
