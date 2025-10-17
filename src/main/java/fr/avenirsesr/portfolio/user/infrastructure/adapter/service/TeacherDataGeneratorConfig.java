package fr.avenirsesr.portfolio.user.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.ESeederSource;
import fr.avenirsesr.portfolio.user.domain.port.output.seeder.TeacherDataGenerator;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.data.TeacherCSVDataGenerator;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.data.TeacherFakerDataGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TeacherDataGeneratorConfig {
  @Value("${seeder.source}")
  private ESeederSource seederSource;

  @Bean
  public TeacherDataGenerator teacherDataGenerator() {
    return switch (seederSource) {
      case CSV -> new TeacherCSVDataGenerator();
      case FAKER -> new TeacherFakerDataGenerator();
    };
  }
}
