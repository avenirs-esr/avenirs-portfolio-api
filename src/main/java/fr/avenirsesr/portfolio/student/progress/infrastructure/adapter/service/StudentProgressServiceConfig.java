package fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository.AdditionalSkillRepository;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.domain.port.input.StudentProgressService;
import fr.avenirsesr.portfolio.student.progress.domain.port.output.repository.SkillLevelProgressRepository;
import fr.avenirsesr.portfolio.student.progress.domain.service.StudentProgressServiceImpl;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.repository.AdditionalSkillProgressDatabaseRepository;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.repository.StudentProgressDatabaseRepository;
import fr.avenirsesr.portfolio.trace.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.trace.domain.port.output.repository.TraceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class StudentProgressServiceConfig {
  private final StudentProgressDatabaseRepository studentProgressRepository;
  private final SkillLevelProgressRepository skillLevelProgressRepository;
  private final TraceService traceService;
  private final TraceRepository traceRepository;
  private final AdditionalSkillRepository additionalSkillRepository;
  private final AdditionalSkillProgressDatabaseRepository additionalSkillProgressRepository;
  private final LoggedInUserService loggedInUserService;

  @Bean
  public StudentProgressService studentProgressService() {
    return new StudentProgressServiceImpl(
        studentProgressRepository,
        skillLevelProgressRepository,
        traceService,
        traceRepository,
        additionalSkillRepository,
        additionalSkillProgressRepository,
        loggedInUserService);
  }
}
