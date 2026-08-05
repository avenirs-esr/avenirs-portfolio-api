package fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.seeder.data.DeclaredSkillProgressFakerDataGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeclaredSkillProgressDataGeneratorConfig {

  @Bean
  public DeclaredSkillProgressFakerDataGenerator declaredSkillProgressFakerDataGenerator() {
    return new DeclaredSkillProgressFakerDataGenerator();
  }
}
