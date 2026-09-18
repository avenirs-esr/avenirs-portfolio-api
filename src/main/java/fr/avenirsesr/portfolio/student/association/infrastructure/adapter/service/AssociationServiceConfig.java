package fr.avenirsesr.portfolio.student.association.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.student.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.student.association.domain.port.output.handler.AssociationContextHandler;
import fr.avenirsesr.portfolio.student.association.domain.port.output.repository.AssociationRepository;
import fr.avenirsesr.portfolio.student.association.domain.service.AssociationServiceImpl;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
@RequiredArgsConstructor
public class AssociationServiceConfig {
  private final AssociationRepository associationRepository;

  @Bean
  public AssociationService associationService(
      @Lazy List<AssociationContextHandler> contextHandlers) {
    return new AssociationServiceImpl(associationRepository, contextHandlers);
  }
}
