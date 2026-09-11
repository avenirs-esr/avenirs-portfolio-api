package fr.avenirsesr.portfolio.user.domain.port.output.client;

import fr.avenirsesr.portfolio.common.group.application.adapter.dto.GroupDTO;
import java.util.Optional;
import java.util.UUID;

public interface GroupClient {
  Optional<GroupDTO> getById(UUID id);

  /**
   * Returns the program the given group belongs to. The group may itself be a program, a program
   * option or a student group: the back-office walks the parent chain up to the program.
   */
  Optional<GroupDTO> getProgramOfGroup(UUID groupId);
}
