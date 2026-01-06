package fr.avenirsesr.portfolio.user.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.user.domain.port.output.seeder.TeacherDataGenerator;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.data.TeacherFakerDataGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TeacherDataGeneratorConfig {
  @Bean
  public TeacherDataGenerator teacherDataGenerator() {
    return new TeacherFakerDataGenerator();
  }
}
