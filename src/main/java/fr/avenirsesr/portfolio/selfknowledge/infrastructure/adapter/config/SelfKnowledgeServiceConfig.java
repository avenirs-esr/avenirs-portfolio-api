package fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.config;

import fr.avenirsesr.portfolio.selfknowledge.domain.port.input.SelfKnowledgeService;
import fr.avenirsesr.portfolio.selfknowledge.domain.port.output.repository.SelfKnowledgeCategoryRepository;
import fr.avenirsesr.portfolio.selfknowledge.domain.port.output.repository.SelfKnowledgeElementRepository;
import fr.avenirsesr.portfolio.selfknowledge.domain.service.SelfKnowledgeServiceImpl;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.user.domain.port.input.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SelfKnowledgeServiceConfig {
  private final StudentService studentService;
  private final SelfKnowledgeElementRepository selfKnowledgeElementRepository;
  private final SelfKnowledgeCategoryRepository selfKnowledgeCategoryRepository;
  private final LoggedInUserService loggedInUserService;

  @Bean
  public SelfKnowledgeService selfKnowledgeService() {
    return new SelfKnowledgeServiceImpl(
        studentService,
        selfKnowledgeElementRepository,
        selfKnowledgeCategoryRepository,
        loggedInUserService);
  }
}
