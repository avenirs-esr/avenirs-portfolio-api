package fr.avenirsesr.portfolio.skill.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.skill.domain.port.input.SkillService;
import fr.avenirsesr.portfolio.skill.domain.port.output.repository.NationalSkillDetailsRepository;
import fr.avenirsesr.portfolio.skill.domain.port.output.repository.SkillRepository;
import fr.avenirsesr.portfolio.skill.domain.service.SkillServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SkillServiceConfig {

  @Bean
  public SkillService skillService(
      SkillRepository skillRepository,
      NationalSkillDetailsRepository nationalSkillDetailsRepository) {
    return new SkillServiceImpl(skillRepository, nationalSkillDetailsRepository);
  }
}
