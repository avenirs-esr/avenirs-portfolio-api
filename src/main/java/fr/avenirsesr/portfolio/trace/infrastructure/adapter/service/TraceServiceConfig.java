package fr.avenirsesr.portfolio.trace.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.ams.domain.port.output.repository.AMSRepository;
import fr.avenirsesr.portfolio.file.domain.port.output.repository.TraceAttachmentRepository;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.port.output.repository.DeclaredSkillProgressRepository;
import fr.avenirsesr.portfolio.student.progress.imported.domain.port.output.repository.SkillLevelProgressRepository;
import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.repository.StudentProgressDatabaseRepository;
import fr.avenirsesr.portfolio.trace.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.trace.domain.service.TraceServiceImpl;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.client.TraceConfigurationClient;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.repository.TraceDatabaseRepository;
import fr.avenirsesr.portfolio.user.domain.port.input.StudentService;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class TraceServiceConfig {
  private final TraceDatabaseRepository traceRepository;
  private final UserRepository userRepository;
  private final StudentProgressDatabaseRepository studentProgressRepository;

  private final DeclaredSkillProgressRepository declaredSkillProgressRepository;
  private final AMSRepository amsRepository;
  private final SkillLevelProgressRepository skillLevelProgressRepository;
  private final TraceConfigurationClient traceConfigurationClient;
  private final TraceAttachmentRepository traceAttachmentRepository;
  private final StudentService studentService;
  private final LoggedInUserService loggedInUserService;

  @Bean
  public TraceService traceService() {
    return new TraceServiceImpl(
        traceRepository,
        userRepository,
        studentProgressRepository,
        declaredSkillProgressRepository,
        amsRepository,
        skillLevelProgressRepository,
        traceAttachmentRepository,
        studentService,
        traceConfigurationClient,
        loggedInUserService);
  }
}
