package fr.avenirsesr.portfolio.user.infrastructure.adapter.client;

import fr.avenirsesr.portfolio.common.group.application.adapter.dto.GroupDTO;
import fr.avenirsesr.portfolio.user.domain.port.output.client.GroupClient;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
@Primary
public class GroupClientStub implements GroupClient {

  @Override
  public Optional<GroupDTO> getById(UUID id) {
    return Optional.empty();
  }

  @Override
  public Optional<GroupDTO> getProgramOfGroup(UUID groupId) {
    return Optional.empty();
  }

  @Override
  public boolean hasAccess(List<UUID> affiliatedIds, List<UUID> targetIds) {
    return new HashSet<>(affiliatedIds).containsAll(targetIds);
  }

  @Override
  public List<UUID> getStudentAccessibleIds(List<UUID> ids) {
    return List.copyOf(new LinkedHashSet<>(ids));
  }
}
