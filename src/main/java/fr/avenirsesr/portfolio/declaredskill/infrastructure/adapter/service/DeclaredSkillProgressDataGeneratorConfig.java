package fr.avenirsesr.portfolio.declaredskill.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.declaredskill.infrastructure.adapter.seeder.data.DeclaredSkillProgressFakerDataGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeclaredSkillProgressDataGeneratorConfig {

  @Bean
  public DeclaredSkillProgressFakerDataGenerator declaredSkillProgressFakerDataGenerator() {
    return new DeclaredSkillProgressFakerDataGenerator();
  }
}
