package fr.avenirsesr.portfolio.user.infrastructure.adapter.client;

import fr.avenirsesr.portfolio.common.institution.application.adapter.dto.InstitutionDTO;
import fr.avenirsesr.portfolio.user.domain.port.output.client.InstitutionClient;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
@Primary
public class InstitutionClientStub implements InstitutionClient {

  @Override
  public Optional<InstitutionDTO> getById(UUID id) {
    return Optional.empty();
  }

  @Override
  public boolean hasAccess(List<UUID> affiliatedIds, List<UUID> targetIds) {
    return new HashSet<>(affiliatedIds).containsAll(targetIds);
  }
}
