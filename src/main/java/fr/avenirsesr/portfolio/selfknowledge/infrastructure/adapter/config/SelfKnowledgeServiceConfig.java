package fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.config;

import fr.avenirsesr.portfolio.selfknowledge.domain.port.input.SelfKnowledgeService;
import fr.avenirsesr.portfolio.selfknowledge.domain.port.output.repository.SelfKnowledgeCategoryRepository;
import fr.avenirsesr.portfolio.selfknowledge.domain.service.SelfKnowledgeServiceImpl;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SelfKnowledgeServiceConfig {
  private final StudentRepository studentRepository;
  private final SelfKnowledgeCategoryRepository selfKnowledgeCategoryRepository;

  @Bean
  public SelfKnowledgeService selfKnowledgeService() {
    return new SelfKnowledgeServiceImpl(studentRepository, selfKnowledgeCategoryRepository);
  }
}
