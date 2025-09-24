package fr.avenirsesr.portfolio.trace.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository.AdditionalSkillProgressRepository;
import fr.avenirsesr.portfolio.ams.domain.port.output.repository.AMSRepository;
import fr.avenirsesr.portfolio.backoffice.configuration.trace.domain.port.input.TraceConfigurationService;
import fr.avenirsesr.portfolio.student.progress.domain.port.output.repository.SkillLevelProgressRepository;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.repository.StudentProgressDatabaseRepository;
import fr.avenirsesr.portfolio.trace.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.trace.domain.service.TraceServiceImpl;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.repository.TraceDatabaseRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TraceServiceConfig {
  private final TraceDatabaseRepository traceRepository;
  private final StudentProgressDatabaseRepository studentProgressRepository;
  private final AdditionalSkillProgressRepository additionalSkillProgressRepository;
  private final AMSRepository amsRepository;
  private final SkillLevelProgressRepository skillLevelProgressRepository;
  private final TraceConfigurationService traceConfigurationService;

  public TraceServiceConfig(
      TraceDatabaseRepository traceRepository,
      StudentProgressDatabaseRepository studentProgressRepository,
      AdditionalSkillProgressRepository additionalSkillProgressRepository,
      AMSRepository amsRepository,
      SkillLevelProgressRepository skillLevelProgressRepository,
      TraceConfigurationService traceConfigurationService) {
    this.traceRepository = traceRepository;
    this.studentProgressRepository = studentProgressRepository;
    this.additionalSkillProgressRepository = additionalSkillProgressRepository;
    this.amsRepository = amsRepository;
    this.skillLevelProgressRepository = skillLevelProgressRepository;
    this.traceConfigurationService = traceConfigurationService;
  }

  @Bean
  public TraceService traceService() {
    return new TraceServiceImpl(
        traceRepository,
        studentProgressRepository,
        additionalSkillProgressRepository,
        amsRepository,
        skillLevelProgressRepository,
        traceConfigurationService);
  }
}
