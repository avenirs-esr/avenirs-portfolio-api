package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.additionalskill.domain.port.input.AdditionalSkillService;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository.AdditionalSkillRepository;
import fr.avenirsesr.portfolio.additionalskill.domain.service.AdditionalSkillServiceImpl;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.repository.AdditionalSkillProgressDatabaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AdditionalSkillServiceConfig {
  private final AdditionalSkillRepository additionalSkillRepository;

  @Bean
  public AdditionalSkillService additionalSkillService() {
    return new AdditionalSkillServiceImpl(
        additionalSkillRepository, additionalSkillProgressRepository);
  }
}
