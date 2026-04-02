package fr.avenirsesr.portfolio.trace.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.file.domain.port.input.TraceAttachmentService;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.input.DeclaredActivityService;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.port.input.DeclaredSkillProgressService;
import fr.avenirsesr.portfolio.student.progress.imported.domain.port.input.StudentProgressService;
import fr.avenirsesr.portfolio.trace.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.trace.domain.service.TraceServiceImpl;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.client.TraceConfigurationClient;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.repository.TraceDatabaseRepository;
import fr.avenirsesr.portfolio.user.domain.port.input.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class TraceServiceConfig {
  private final TraceDatabaseRepository traceRepository;
  private final UserService userService;
  private final StudentProgressService studentProgressService;
  private final TraceAttachmentService traceAttachmentService;
  private final DeclaredActivityService declaredActivityService;
  private final DeclaredSkillProgressService declaredSkillProgressService;
  private final TraceConfigurationClient traceConfigurationClient;
  private final LoggedInUserService loggedInUserService;
  private final AssociationService associationService;

  @Bean
  public TraceService traceService() {
    return new TraceServiceImpl(
        traceRepository,
        userService,
        studentProgressService,
        traceAttachmentService,
        declaredActivityService,
        declaredSkillProgressService,
        traceConfigurationClient,
        loggedInUserService,
        associationService);
  }
}
