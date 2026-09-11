package fr.avenirsesr.portfolio.user.application.adapter.mapper;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.user.application.adapter.dto.StudentInfoDTO;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.port.output.client.GroupClient;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Builds the student identity exposed by the API. The program is owned by the back-office, which
 * resolves it from the group the student belongs to.
 */
@Component
@RequiredArgsConstructor
public class StudentInfoDTOMapper {

  private final GroupClient groupClient;

  public StudentInfoDTO toDTO(Student student) {
    if (student == null) {
      return null;
    }

    User user = student.getUser();
    UUID groupId = student.getGroupId();

    return new StudentInfoDTO(
        user.getId(),
        user.getFirstName(),
        user.getLastName(),
        user.getEmail(),
        groupId == null ? null : groupClient.getProgramOfGroup(groupId).orElse(null));
  }
}
