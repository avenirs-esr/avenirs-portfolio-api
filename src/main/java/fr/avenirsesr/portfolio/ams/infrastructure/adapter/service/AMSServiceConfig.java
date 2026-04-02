package fr.avenirsesr.portfolio.ams.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.ams.domain.port.input.AMSService;
import fr.avenirsesr.portfolio.ams.domain.port.output.repository.AMSRepository;
import fr.avenirsesr.portfolio.ams.domain.service.AMSServiceImpl;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.imported.domain.port.input.SkillLevelProgressService;
import fr.avenirsesr.portfolio.trace.domain.port.input.TraceService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@AllArgsConstructor
public class AMSServiceConfig {
  private final AMSRepository amsRepository;
  private final TraceService traceService;
  private final SkillLevelProgressService skillLevelProgressService;
  private final LoggedInUserService loggedInUserService;

  @Bean
  public AMSService amsService() {
    return new AMSServiceImpl(
        amsRepository, traceService, skillLevelProgressService, loggedInUserService);
  }
}
