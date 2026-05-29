package fr.avenirsesr.portfolio.trace.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.activity.domain.port.output.repository.ActivityDraftRepository;
import fr.avenirsesr.portfolio.file.domain.port.input.FileResourceService;
import fr.avenirsesr.portfolio.file.domain.port.output.repository.FileRepository;
import fr.avenirsesr.portfolio.file.domain.port.output.service.FileStorageService;
import fr.avenirsesr.portfolio.file.domain.service.FileResourceServiceImpl;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.trace.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.trace.domain.port.output.repository.TraceRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StaffRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TraceSeederConfig {
  @Bean
  @Qualifier("MockFileResourceService")
  public FileResourceService MockFileResourceService(
      @Qualifier("seederFileStorageService") FileStorageService fileStorageService,
      FileRepository fileRepository,
      TraceRepository traceRepository,
      StaffRepository staffRepository,
      StudentRepository studentRepository,
      ActivityDraftRepository activityDraftRepository,
      LoggedInUserService loggedInUserService,
      TraceService traceService) {
    return new FileResourceServiceImpl(
        fileStorageService,
        fileRepository,
        traceRepository,
        staffRepository,
        studentRepository,
        activityDraftRepository,
        loggedInUserService,
        traceService);
  }
}
