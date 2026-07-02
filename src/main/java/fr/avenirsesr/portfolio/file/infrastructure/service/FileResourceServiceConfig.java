package fr.avenirsesr.portfolio.file.infrastructure.service;

import fr.avenirsesr.portfolio.activity.domain.port.output.repository.ActivityDraftRepository;
import fr.avenirsesr.portfolio.activity.domain.port.output.repository.ActivityRepository;
import fr.avenirsesr.portfolio.file.domain.port.input.FileResourceService;
import fr.avenirsesr.portfolio.file.domain.port.output.repository.FileRepository;
import fr.avenirsesr.portfolio.file.domain.port.output.service.FileStorageService;
import fr.avenirsesr.portfolio.file.domain.service.FileResourceServiceImpl;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.trace.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.trace.domain.port.output.repository.TraceRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StaffRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@RequiredArgsConstructor
public class FileResourceServiceConfig {
  private final FileStorageService fileStorageService;
  private final FileRepository fileRepository;
  private final TraceRepository traceRepository;
  private final StaffRepository staffRepository;
  private final StudentRepository studentRepository;
  private final ActivityDraftRepository activityDraftRepository;
  private final ActivityRepository activityRepository;
  private final LoggedInUserService loggedInUserService;
  private final TraceService traceService;

  @Bean
  @Primary
  public FileResourceService fileResourceService() {
    return new FileResourceServiceImpl(
        fileStorageService,
        fileRepository,
        traceRepository,
        staffRepository,
        studentRepository,
        activityDraftRepository,
        activityRepository,
        loggedInUserService,
        traceService);
  }
}
