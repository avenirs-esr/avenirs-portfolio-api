package fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.imported.domain.port.input.SkillLevelProgressService;
import fr.avenirsesr.portfolio.student.progress.imported.domain.port.output.repository.SkillLevelProgressRepository;
import fr.avenirsesr.portfolio.student.progress.imported.domain.service.SkillLevelProgressServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SkillLevelProgressServiceConfig {
  private final SkillLevelProgressRepository skillLevelProgressRepository;
  private final LoggedInUserService loggedInUserService;

  @Bean
  public SkillLevelProgressService skillLevelProgressService() {
    return new SkillLevelProgressServiceImpl(skillLevelProgressRepository, loggedInUserService);
  }
}
