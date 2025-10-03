package fr.avenirsesr.portfolio.trace.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository.AdditionalSkillProgressRepository;
import fr.avenirsesr.portfolio.ams.domain.port.output.repository.AMSRepository;
import fr.avenirsesr.portfolio.file.domain.port.output.repository.TraceAttachmentRepository;
import fr.avenirsesr.portfolio.student.progress.domain.port.output.repository.SkillLevelProgressRepository;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.repository.StudentProgressDatabaseRepository;
import fr.avenirsesr.portfolio.trace.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.trace.domain.service.TraceServiceImpl;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.client.TraceConfigurationClient;
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
  private final TraceConfigurationClient traceConfigurationClient;
  private final TraceAttachmentRepository traceAttachmentRepository;

  public TraceServiceConfig(
      TraceDatabaseRepository traceRepository,
      StudentProgressDatabaseRepository studentProgressRepository,
      AdditionalSkillProgressRepository additionalSkillProgressRepository,
      AMSRepository amsRepository,
      SkillLevelProgressRepository skillLevelProgressRepository,
      TraceConfigurationClient traceConfigurationClient,
      TraceAttachmentRepository traceAttachmentRepository) {
    this.traceRepository = traceRepository;
    this.studentProgressRepository = studentProgressRepository;
    this.additionalSkillProgressRepository = additionalSkillProgressRepository;
    this.amsRepository = amsRepository;
    this.skillLevelProgressRepository = skillLevelProgressRepository;
    this.traceConfigurationClient = traceConfigurationClient;
    this.traceAttachmentRepository = traceAttachmentRepository;
  }

  @Bean
  public TraceService traceService() {
    return new TraceServiceImpl(
        traceRepository,
        studentProgressRepository,
        additionalSkillProgressRepository,
        amsRepository,
        skillLevelProgressRepository,
        traceAttachmentRepository,
        traceConfigurationClient);
  }
}
