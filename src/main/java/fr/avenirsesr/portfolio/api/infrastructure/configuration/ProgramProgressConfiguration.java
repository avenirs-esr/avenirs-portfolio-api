package fr.avenirsesr.portfolio.api.infrastructure.configuration;

import fr.avenirsesr.portfolio.api.domain.port.input.ProgramProgressService;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.ProgramProgressRepository;
import fr.avenirsesr.portfolio.api.domain.service.ProgramProgressServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProgramProgressConfiguration {

  @Bean
  ProgramProgressService programProgressService(
      ProgramProgressRepository programProgressRepository) {
    return new ProgramProgressServiceImpl(programProgressRepository);
  }
}
