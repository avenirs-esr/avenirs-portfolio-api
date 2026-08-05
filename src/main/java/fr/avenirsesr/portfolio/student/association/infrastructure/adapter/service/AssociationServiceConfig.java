package fr.avenirsesr.portfolio.student.association.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.student.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.student.association.domain.port.output.repository.AssociationRepository;
import fr.avenirsesr.portfolio.student.association.domain.service.AssociationSearchHelper;
import fr.avenirsesr.portfolio.student.association.domain.service.AssociationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AssociationServiceConfig {
  private final AssociationRepository associationRepository;

  @Bean
  public AssociationService AssociationService() {
    return new AssociationServiceImpl(associationRepository);
  }

  @Bean
  public AssociationSearchHelper associationSearchHelper(AssociationService associationService) {
    return new AssociationSearchHelper(associationService);
  }
}
