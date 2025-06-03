package fr.avenirsesr.portfolio.api.application.adapter.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import fixtures.ProgramProgressFixture;
import fixtures.SkillFixture;
import fixtures.UserFixture;
import fr.avenirsesr.portfolio.api.application.adapter.dto.ProgramProgressDTO;
import fr.avenirsesr.portfolio.api.domain.exception.UserIsNotStudentException;
import fr.avenirsesr.portfolio.api.domain.exception.UserNotFoundException;
import fr.avenirsesr.portfolio.api.domain.model.ProgramProgress;
import fr.avenirsesr.portfolio.api.domain.model.Skill;
import fr.avenirsesr.portfolio.api.domain.model.Student;
import fr.avenirsesr.portfolio.api.domain.model.User;
import fr.avenirsesr.portfolio.api.domain.port.input.ProgramProgressService;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.UserRepository;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProgramProgressControllerTest {
  @Mock private ProgramProgressService programProgressService;
  @Mock private UserRepository userRepository;

  @InjectMocks private ProgramProgressController programProgressController;

  private UUID userId;
  private User user;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    user = UserFixture.createStudent().withId(userId).toModel();
  }

  @Test
  void shouldReturnSkillsOverviewForStudent() {
    // Given
    ProgramProgress programProgress = ProgramProgressFixture.create().toModel();
    Skill skill = SkillFixture.create().toModel();
    Map<ProgramProgress, Set<Skill>> serviceResponse = Map.of(programProgress, Set.of(skill));

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(programProgressService.getSkillsOverview(any(Student.class))).thenReturn(serviceResponse);

    // When
    List<ProgramProgressDTO> result =
        programProgressController.getSkillsOverview(userId.toString());

    // Then
    assertEquals(1, result.size());
    ProgramProgressDTO dto = result.getFirst();
    assertEquals(programProgress.getId(), dto.id());
  }

  @Test
  void shouldThrowUserNotFoundExceptionIfUserNotExist() {
    // Given
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    // Then
    assertThrows(
        UserNotFoundException.class,
        () -> programProgressController.getSkillsOverview(userId.toString()));
  }

  @Test
  void shouldThrowUserIsNotStudentExceptionIfUserNotStudent() {
    // Given
    user.setStudent(false);
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    // Then
    assertThrows(
        UserIsNotStudentException.class,
        () -> programProgressController.getSkillsOverview(userId.toString()));
  }
}
