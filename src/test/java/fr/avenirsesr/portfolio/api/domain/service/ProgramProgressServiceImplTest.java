package fr.avenirsesr.portfolio.api.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.api.domain.model.*;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.ProgramProgressRepository;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProgramProgressServiceImplTest {
  @Mock private ProgramProgressRepository programProgressRepository;

  @InjectMocks private ProgramProgressServiceImpl programProgressService;

  private UUID userId;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
  }

  @Test
  void shouldReturnEmptyListWhenRepositoryReturnsEmptyList() {
    when(programProgressRepository.getSkillsOverview(userId)).thenReturn(List.of());

    List<ProgramProgress> result = programProgressService.getSkillsOverview(userId);

    assertTrue(result.isEmpty());
  }

  @Test
  void shouldReturnMax2ProgramsSortedByProgramName() {
    UUID programProgressId1 = UUID.randomUUID();
    UUID programProgressId2 = UUID.randomUUID();
    UUID programProgressId3 = UUID.randomUUID();
    ProgramProgress programProgress1 =
        createProgramProgress(programProgressId1, "Z", List.of("a", "b", "c"));
    ProgramProgress programProgress2 =
        createProgramProgress(programProgressId2, "Y", List.of("x", "y"));
    ProgramProgress programProgress3 = createProgramProgress(programProgressId3, "X", List.of("s"));

    when(programProgressRepository.getSkillsOverview(any(UUID.class)))
        .thenReturn(List.of(programProgress1, programProgress2, programProgress3));

    List<ProgramProgress> result = programProgressService.getSkillsOverview(userId);

    assertEquals(2, result.size());
    assertEquals(programProgressId3, result.get(0).getId());
    assertEquals(programProgressId2, result.get(1).getId());
  }

  @Test
  void shouldLimitSkillsTo3WhenBothProgramsHaveAtLeast3Skills() {
    ProgramProgress programProgress1 =
        createProgramProgress(UUID.randomUUID(), "A", List.of("b", "a", "c", "x"));
    ProgramProgress programProgress2 =
        createProgramProgress(UUID.randomUUID(), "B", List.of("d", "e", "f", "g"));

    when(programProgressRepository.getSkillsOverview(any(UUID.class)))
        .thenReturn(List.of(programProgress1, programProgress2));

    List<ProgramProgress> result = programProgressService.getSkillsOverview(userId);

    assertEquals(3, result.get(0).getSkills().size());
    assertEquals(3, result.get(1).getSkills().size());

    assertEquals(List.of("a", "b", "c"), extractSkillNames(result.get(0)));
    assertEquals(List.of("d", "e", "f"), extractSkillNames(result.get(1)));
  }

  @Test
  void shouldLimitSkillsTo4WhenOneProgramHas2Skills() {
    ProgramProgress programProgress1 =
        createProgramProgress(UUID.randomUUID(), "A", List.of("b", "a"));
    ProgramProgress programProgress2 =
        createProgramProgress(UUID.randomUUID(), "B", List.of("d", "e", "f", "g"));

    when(programProgressRepository.getSkillsOverview(any(UUID.class)))
        .thenReturn(List.of(programProgress1, programProgress2));

    List<ProgramProgress> result = programProgressService.getSkillsOverview(userId);

    assertEquals(2, result.get(0).getSkills().size());
    assertEquals(4, result.get(1).getSkills().size());
    assertEquals(List.of("a", "b"), extractSkillNames(result.get(0)));
    assertEquals(List.of("d", "e", "f", "g"), extractSkillNames(result.get(1)));
  }

  @Test
  void shouldLimitSkillsTo5WhenOneProgramHas1Skill() {
    ProgramProgress programProgress1 = createProgramProgress(UUID.randomUUID(), "A", List.of("b"));
    ProgramProgress programProgress2 =
        createProgramProgress(UUID.randomUUID(), "B", List.of("d", "e", "f", "g", "h"));

    when(programProgressRepository.getSkillsOverview(any(UUID.class)))
        .thenReturn(List.of(programProgress1, programProgress2));

    List<ProgramProgress> result = programProgressService.getSkillsOverview(userId);

    assertEquals(1, result.get(0).getSkills().size());
    assertEquals(5, result.get(1).getSkills().size());
    assertEquals(List.of("b"), extractSkillNames(result.get(0)));
    assertEquals(List.of("d", "e", "f", "g", "h"), extractSkillNames(result.get(1)));
  }

  private ProgramProgress createProgramProgress(
      UUID id, String programName, List<String> skillNames) {
    Institution institution = Institution.create("institution");
    Program program = Program.create(institution, programName);

    User user = User.create("John", "Doe");
    Student student = Student.create(user);

    LinkedHashSet<Skill> skills =
        skillNames.stream()
            .map(name -> Skill.create(name, null))
            .collect(Collectors.toCollection(LinkedHashSet::new));

    return ProgramProgress.toDomain(id, program, student, skills);
  }

  private List<String> extractSkillNames(ProgramProgress progress) {
    return progress.getSkills().stream().map(Skill::getName).collect(Collectors.toList());
  }
}
