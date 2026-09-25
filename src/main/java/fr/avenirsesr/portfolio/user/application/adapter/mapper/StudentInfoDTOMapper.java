package fr.avenirsesr.portfolio.user.application.adapter.mapper;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.error.domain.exception.UserNotFoundException;
import fr.avenirsesr.portfolio.common.group.application.adapter.dto.GroupDTO;
import fr.avenirsesr.portfolio.user.application.adapter.dto.StudentInfoDTO;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.port.output.client.AccessClient;
import fr.avenirsesr.portfolio.user.domain.port.output.client.GroupClient;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserPrincipalRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudentInfoDTOMapper {

  private final GroupClient groupClient;
  private final AccessClient accessClient;
  private final UserPrincipalRepository userPrincipalRepository;

  public StudentInfoDTO toDTO(Student student) {
    if (student == null) {
      return null;
    }

    User user = student.getUser();
    String eppn =
        userPrincipalRepository
            .findEppnByUserId(user.getId())
            .orElseThrow(UserNotFoundException::new);
    List<UUID> groupIds = accessClient.getStudentScope(eppn).groupIds();
    List<GroupDTO> programs =
        groupIds.stream()
            .map(groupClient::getProgramOfGroup)
            .flatMap(Optional::stream)
            .distinct()
            .toList();

    return new StudentInfoDTO(
        user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), programs);
  }
}
