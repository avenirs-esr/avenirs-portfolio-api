package fr.avenirsesr.portfolio.ams.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.ams.domain.port.input.AMSService;
import fr.avenirsesr.portfolio.ams.domain.port.output.repository.AMSRepository;
import fr.avenirsesr.portfolio.ams.domain.service.AMSServiceImpl;
import fr.avenirsesr.portfolio.student.progress.domain.port.output.repository.StudentProgressRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class AMSServiceConfig {
  private final AMSRepository amsRepository;
  private final StudentProgressRepository studentProgressRepository;

  public AMSServiceConfig(
      AMSRepository amsRepository, StudentProgressRepository studentProgressRepository) {
    this.amsRepository = amsRepository;
    this.studentProgressRepository = studentProgressRepository;
  }

  @Bean
  public AMSService amsService() {
    return new AMSServiceImpl(amsRepository, studentProgressRepository);
  }
}
