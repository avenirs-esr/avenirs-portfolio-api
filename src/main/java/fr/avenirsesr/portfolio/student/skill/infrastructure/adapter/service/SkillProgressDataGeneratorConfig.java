package fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.seeder.data.SkillProgressFakerDataGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SkillProgressDataGeneratorConfig {

  @Bean
  public SkillProgressFakerDataGenerator skillProgressFakerDataGenerator() {
    return new SkillProgressFakerDataGenerator();
  }
}
