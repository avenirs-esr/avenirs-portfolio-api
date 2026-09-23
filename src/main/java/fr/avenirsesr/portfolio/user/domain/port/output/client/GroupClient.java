package fr.avenirsesr.portfolio.user.domain.port.output.client;

import fr.avenirsesr.portfolio.common.group.application.adapter.dto.GroupDTO;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupClient {
  Optional<GroupDTO> getById(UUID id);

  Optional<GroupDTO> getProgramOfGroup(UUID groupId);

  boolean hasAccess(List<UUID> affiliatedIds, List<UUID> targetIds);
}
