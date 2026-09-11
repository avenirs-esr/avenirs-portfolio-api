package fr.avenirsesr.portfolio.user.application.adapter.mapper;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.user.application.adapter.dto.StudentInfoDTO;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.port.output.client.GroupClient;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
