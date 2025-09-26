package fr.avenirsesr.portfolio.trace.infrastructure.adapter.service;

<<<<<<< HEAD
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository.AdditionalSkillProgressRepository;
import fr.avenirsesr.portfolio.ams.domain.port.output.repository.AMSRepository;
import fr.avenirsesr.portfolio.backoffice.configuration.trace.domain.port.input.TraceConfigurationService;
import fr.avenirsesr.portfolio.file.domain.port.output.repository.TraceAttachmentRepository;
import fr.avenirsesr.portfolio.student.progress.domain.port.output.repository.SkillLevelProgressRepository;
=======
>>>>>>> cdc9b075 (refactor: back office feature moved in specific microservice)
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
<<<<<<< HEAD
  private final AdditionalSkillProgressRepository additionalSkillProgressRepository;
  private final AMSRepository amsRepository;
  private final SkillLevelProgressRepository skillLevelProgressRepository;
  private final TraceConfigurationService traceConfigurationService;
  private final TraceAttachmentRepository traceAttachmentRepository;
=======
  private final TraceConfigurationClient traceConfigurationClient;
>>>>>>> cdc9b075 (refactor: back office feature moved in specific microservice)

  public TraceServiceConfig(
      TraceDatabaseRepository traceRepository,
      StudentProgressDatabaseRepository studentProgressRepository,
<<<<<<< HEAD
      AdditionalSkillProgressRepository additionalSkillProgressRepository,
      AMSRepository amsRepository,
      SkillLevelProgressRepository skillLevelProgressRepository,
      TraceConfigurationService traceConfigurationService,
      TraceAttachmentRepository traceAttachmentRepository) {
    this.traceRepository = traceRepository;
    this.studentProgressRepository = studentProgressRepository;
    this.additionalSkillProgressRepository = additionalSkillProgressRepository;
    this.amsRepository = amsRepository;
    this.skillLevelProgressRepository = skillLevelProgressRepository;
    this.traceConfigurationService = traceConfigurationService;
    this.traceAttachmentRepository = traceAttachmentRepository;
=======
      TraceConfigurationClient traceConfigurationClient) {
    this.traceRepository = traceRepository;
    this.studentProgressRepository = studentProgressRepository;
    this.traceConfigurationClient = traceConfigurationClient;
>>>>>>> cdc9b075 (refactor: back office feature moved in specific microservice)
  }

  @Bean
  public TraceService traceService() {
    return new TraceServiceImpl(
<<<<<<< HEAD
        traceRepository,
        studentProgressRepository,
        additionalSkillProgressRepository,
        amsRepository,
        skillLevelProgressRepository,
        traceConfigurationService,
        traceAttachmentRepository);
=======
        traceRepository, studentProgressRepository, traceConfigurationClient);
>>>>>>> cdc9b075 (refactor: back office feature moved in specific microservice)
  }
}
