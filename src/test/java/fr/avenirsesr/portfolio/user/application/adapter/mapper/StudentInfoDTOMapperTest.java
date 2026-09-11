package fr.avenirsesr.portfolio.user.application.adapter.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.common.group.application.adapter.dto.GroupDTO;
import fr.avenirsesr.portfolio.common.group.domain.model.enums.EGroupType;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.user.application.adapter.dto.StudentInfoDTO;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.port.output.client.GroupClient;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StudentInfoDTOMapperTest {

  @Mock private GroupClient groupClient;

  @InjectMocks private StudentInfoDTOMapper mapper;

  @Test
  void should_map_identity_and_the_program_resolved_from_the_group() {
    BddLogger.given("A student belonging to a student group");
    UUID groupId = UUID.randomUUID();
    Student student = StudentFixture.create().withGroupId(groupId).toModel();
    GroupDTO program =
        new GroupDTO(UUID.randomUUID(), "Licence Informatique", EGroupType.PROGRAM, null);
    when(groupClient.getProgramOfGroup(groupId)).thenReturn(Optional.of(program));

    BddLogger.when("mapping the student to a StudentInfoDTO");
    StudentInfoDTO dto = mapper.toDTO(student);

    BddLogger.then("the identity and the program of that group are present");
    assertThat(dto.id()).isEqualTo(student.getUser().getId());
    assertThat(dto.firstName()).isEqualTo(student.getUser().getFirstName());
    assertThat(dto.lastName()).isEqualTo(student.getUser().getLastName());
    assertThat(dto.email()).isEqualTo(student.getUser().getEmail());
    assertThat(dto.program()).isEqualTo(program);
  }

  @Test
  void should_not_call_the_back_office_when_the_student_has_no_group() {
    BddLogger.given("A student without group");
    Student student = StudentFixture.create().toModel();

    BddLogger.when("mapping the student to a StudentInfoDTO");
    StudentInfoDTO dto = mapper.toDTO(student);

    BddLogger.then("the program is null and no back-office call is made");
    assertThat(dto.program()).isNull();
    verifyNoInteractions(groupClient);
  }

  @Test
  void should_map_a_null_program_when_the_back_office_does_not_know_the_group() {
    BddLogger.given("A student whose group is unknown to the back-office");
    UUID groupId = UUID.randomUUID();
    Student student = StudentFixture.create().withGroupId(groupId).toModel();
    when(groupClient.getProgramOfGroup(groupId)).thenReturn(Optional.empty());

    BddLogger.when("mapping the student to a StudentInfoDTO");
    StudentInfoDTO dto = mapper.toDTO(student);

    BddLogger.then("the identity is still mapped and the program is null");
    assertThat(dto.id()).isEqualTo(student.getUser().getId());
    assertThat(dto.program()).isNull();
  }

  @Test
  void should_return_null_when_student_is_null() {
    BddLogger.given("No student");
    BddLogger.when("mapping a null student");
    BddLogger.then("the result is null");
    assertThat(mapper.toDTO(null)).isNull();
  }
}
