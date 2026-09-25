package fr.avenirsesr.portfolio.user.application.adapter.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.common.group.application.adapter.dto.GroupDTO;
import fr.avenirsesr.portfolio.common.group.domain.model.enums.EGroupType;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.user.application.adapter.dto.StudentInfoDTO;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.port.output.client.AccessClient;
import fr.avenirsesr.portfolio.user.domain.port.output.client.GroupClient;
import fr.avenirsesr.portfolio.user.domain.port.output.client.StudentAccessScope;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserPrincipalRepository;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StudentInfoDTOMapperTest {

  private static final String EPPN = "student@university.com";

  @Mock private GroupClient groupClient;
  @Mock private AccessClient accessClient;
  @Mock private UserPrincipalRepository userPrincipalRepository;

  @InjectMocks private StudentInfoDTOMapper mapper;

  @Test
  void should_map_identity_and_the_programs_resolved_from_the_groups() {
    BddLogger.given("A student belonging to two student groups");
    UUID groupId1 = UUID.randomUUID();
    UUID groupId2 = UUID.randomUUID();
    Student student = StudentFixture.create().toModel();
    when(userPrincipalRepository.findEppnByUserId(student.getId())).thenReturn(Optional.of(EPPN));
    when(accessClient.getStudentScope(EPPN))
        .thenReturn(new StudentAccessScope(List.of(), List.of(groupId1, groupId2)));
    GroupDTO program1 =
        new GroupDTO(UUID.randomUUID(), "Licence Informatique", EGroupType.PROGRAM, null);
    GroupDTO program2 =
        new GroupDTO(UUID.randomUUID(), "Licence Mathématiques", EGroupType.PROGRAM, null);
    when(groupClient.getProgramOfGroup(groupId1)).thenReturn(Optional.of(program1));
    when(groupClient.getProgramOfGroup(groupId2)).thenReturn(Optional.of(program2));

    BddLogger.when("mapping the student to a StudentInfoDTO");
    StudentInfoDTO dto = mapper.toDTO(student);

    BddLogger.then("the identity and the programs of those groups are present");
    assertThat(dto.id()).isEqualTo(student.getUser().getId());
    assertThat(dto.firstName()).isEqualTo(student.getUser().getFirstName());
    assertThat(dto.lastName()).isEqualTo(student.getUser().getLastName());
    assertThat(dto.email()).isEqualTo(student.getUser().getEmail());
    assertThat(dto.programs()).containsExactly(program1, program2);
  }

  @Test
  void should_deduplicate_programs_shared_by_several_groups() {
    BddLogger.given("A student whose two groups resolve to the same program");
    UUID groupId1 = UUID.randomUUID();
    UUID groupId2 = UUID.randomUUID();
    Student student = StudentFixture.create().toModel();
    when(userPrincipalRepository.findEppnByUserId(student.getId())).thenReturn(Optional.of(EPPN));
    when(accessClient.getStudentScope(EPPN))
        .thenReturn(new StudentAccessScope(List.of(), List.of(groupId1, groupId2)));
    GroupDTO program =
        new GroupDTO(UUID.randomUUID(), "Licence Informatique", EGroupType.PROGRAM, null);
    when(groupClient.getProgramOfGroup(groupId1)).thenReturn(Optional.of(program));
    when(groupClient.getProgramOfGroup(groupId2)).thenReturn(Optional.of(program));

    BddLogger.when("mapping the student to a StudentInfoDTO");
    StudentInfoDTO dto = mapper.toDTO(student);

    BddLogger.then("the program appears only once");
    assertThat(dto.programs()).containsExactly(program);
  }

  @Test
  void should_not_call_the_group_client_when_the_student_has_no_group() {
    BddLogger.given("A student without group");
    Student student = StudentFixture.create().toModel();
    when(userPrincipalRepository.findEppnByUserId(student.getId())).thenReturn(Optional.of(EPPN));
    when(accessClient.getStudentScope(EPPN)).thenReturn(StudentAccessScope.empty());

    BddLogger.when("mapping the student to a StudentInfoDTO");
    StudentInfoDTO dto = mapper.toDTO(student);

    BddLogger.then("the programs are empty and no back-office group lookup is made");
    assertThat(dto.programs()).isEmpty();
    verifyNoInteractions(groupClient);
  }

  @Test
  void should_skip_a_group_that_the_back_office_does_not_know() {
    BddLogger.given("A student whose group is unknown to the back-office");
    UUID groupId = UUID.randomUUID();
    Student student = StudentFixture.create().toModel();
    when(userPrincipalRepository.findEppnByUserId(student.getId())).thenReturn(Optional.of(EPPN));
    when(accessClient.getStudentScope(EPPN))
        .thenReturn(new StudentAccessScope(List.of(), List.of(groupId)));
    when(groupClient.getProgramOfGroup(groupId)).thenReturn(Optional.empty());

    BddLogger.when("mapping the student to a StudentInfoDTO");
    StudentInfoDTO dto = mapper.toDTO(student);

    BddLogger.then("the identity is still mapped and the programs are empty");
    assertThat(dto.id()).isEqualTo(student.getUser().getId());
    assertThat(dto.programs()).isEmpty();
  }

  @Test
  void should_return_null_when_student_is_null() {
    BddLogger.given("No student");
    BddLogger.when("mapping a null student");
    BddLogger.then("the result is null");
    assertThat(mapper.toDTO(null)).isNull();
  }
}
