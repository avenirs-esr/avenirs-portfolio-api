package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.seeder.data.AdditionalSkillProgressFakerDataGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdditionalSkillProgressDataGeneratorConfig {

  @Bean
  public AdditionalSkillProgressFakerDataGenerator additionalSkillProgressFakerDataGenerator() {
    return new AdditionalSkillProgressFakerDataGenerator();
  }
}
