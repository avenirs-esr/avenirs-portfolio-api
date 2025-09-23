package fr.avenirsesr.portfolio.ams.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.ams.domain.port.input.AMSService;
import fr.avenirsesr.portfolio.ams.domain.port.output.repository.AMSRepository;
import fr.avenirsesr.portfolio.ams.domain.service.AMSServiceImpl;
import fr.avenirsesr.portfolio.student.progress.domain.port.output.repository.SkillLevelProgressRepository;
import fr.avenirsesr.portfolio.student.progress.domain.port.output.repository.StudentProgressRepository;
import fr.avenirsesr.portfolio.trace.domain.port.output.repository.TraceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class AMSServiceConfig {
  private final AMSRepository amsRepository;
  private final StudentProgressRepository studentProgressRepository;
  private final TraceRepository traceRepository;
  private final SkillLevelProgressRepository skillLevelProgressRepository;

  public AMSServiceConfig(
      AMSRepository amsRepository,
      StudentProgressRepository studentProgressRepository,
      TraceRepository traceRepository,
      SkillLevelProgressRepository skillLevelProgressRepository) {
    this.amsRepository = amsRepository;
    this.studentProgressRepository = studentProgressRepository;
    this.traceRepository = traceRepository;
    this.skillLevelProgressRepository = skillLevelProgressRepository;
  }

  @Bean
  public AMSService amsService() {
    return new AMSServiceImpl(
        amsRepository, studentProgressRepository, traceRepository, skillLevelProgressRepository);
  }
}
