package fr.avenirsesr.portfolio.api.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.api.domain.model.*;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.ProgramProgressRepository;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder.FakeInstitution;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder.FakeUser;
import java.util.*;
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

  private Student student;

  @BeforeEach
  void setUp() {
    student = FakeUser.create().toModel().toStudent();
  }

  @Test
  void shouldReturnEmptyListWhenRepositoryReturnsEmptyList() {
    when(programProgressRepository.findAllByStudent(student)).thenReturn(List.of());

    Map<ProgramProgress, Set<Skill>> result = programProgressService.getSkillsOverview(student);

    assertTrue(result.isEmpty());
  }

  @Test
  void shouldReturn3ProgramsSortedByProgramName() {
    UUID programProgressId1 = UUID.randomUUID();
    UUID programProgressId2 = UUID.randomUUID();
    UUID programProgressId3 = UUID.randomUUID();
    ProgramProgress programProgress1 =
        createProgramProgress(programProgressId1, "Z", List.of("a", "b", "c"));
    ProgramProgress programProgress2 =
        createProgramProgress(programProgressId2, "Y", List.of("x", "y"));
    ProgramProgress programProgress3 = createProgramProgress(programProgressId3, "X", List.of("s"));

    when(programProgressRepository.findAllByStudent(any(Student.class)))
        .thenReturn(List.of(programProgress1, programProgress2, programProgress3));

    Map<ProgramProgress, Set<Skill>> result = programProgressService.getSkillsOverview(student);
    List<ProgramProgress> resultPrograms = new ArrayList<>(result.keySet());

    assertEquals(3, resultPrograms.size());
    assertEquals(programProgressId3, resultPrograms.get(0).getId());
    assertEquals(programProgressId2, resultPrograms.get(1).getId());
    assertEquals(programProgressId1, resultPrograms.get(2).getId());
  }

  @Test
  void shouldLimitSkillsTo3WhenBothProgramsHaveAtLeast3Skills() {
    ProgramProgress programProgress1 =
        createProgramProgress(UUID.randomUUID(), "A", List.of("b", "a", "c", "x"));
    ProgramProgress programProgress2 =
        createProgramProgress(UUID.randomUUID(), "B", List.of("d", "e", "f", "g"));

    when(programProgressRepository.findAllByStudent(any(Student.class)))
        .thenReturn(List.of(programProgress1, programProgress2));

    Map<ProgramProgress, Set<Skill>> result = programProgressService.getSkillsOverview(student);
    List<ProgramProgress> resultPrograms = new ArrayList<>(result.keySet());
    List<Skill> skills1 = new ArrayList<>(result.get(resultPrograms.get(0)));
    List<Skill> skills2 = new ArrayList<>(result.get(resultPrograms.get(1)));

    assertEquals(3, skills1.size());
    assertEquals(3, skills2.size());

    assertEquals(List.of("a", "b", "c"), extractSkillNames(skills1));
    assertEquals(List.of("d", "e", "f"), extractSkillNames(skills2));
  }

  @Test
  void shouldHaveATotalOf5SkillsWhenOneProgramHas2Skills() {
    ProgramProgress programProgress1 =
        createProgramProgress(UUID.randomUUID(), "A", List.of("b", "a"));
    ProgramProgress programProgress2 =
        createProgramProgress(UUID.randomUUID(), "B", List.of("d", "e", "f", "g"));

    when(programProgressRepository.findAllByStudent(any(Student.class)))
        .thenReturn(List.of(programProgress1, programProgress2));

    Map<ProgramProgress, Set<Skill>> result = programProgressService.getSkillsOverview(student);
    List<ProgramProgress> resultPrograms = new ArrayList<>(result.keySet());
    List<Skill> skills1 = new ArrayList<>(result.get(resultPrograms.get(0)));
    List<Skill> skills2 = new ArrayList<>(result.get(resultPrograms.get(1)));

    assertEquals(2, skills1.size());
    assertEquals(3, skills2.size());
    assertEquals(List.of("a", "b"), extractSkillNames(skills1));
    assertEquals(List.of("d", "e", "f"), extractSkillNames(skills2));
  }

  @Test
  void shouldHaveATotalOf4SkillsWhenOneProgramHas1Skill() {
    ProgramProgress programProgress1 = createProgramProgress(UUID.randomUUID(), "A", List.of("b"));
    ProgramProgress programProgress2 =
        createProgramProgress(UUID.randomUUID(), "B", List.of("d", "e", "f", "g", "h"));

    when(programProgressRepository.findAllByStudent(any(Student.class)))
        .thenReturn(List.of(programProgress1, programProgress2));

    Map<ProgramProgress, Set<Skill>> result = programProgressService.getSkillsOverview(student);
    List<ProgramProgress> resultPrograms = new ArrayList<>(result.keySet());
    List<Skill> skills1 = new ArrayList<>(result.get(resultPrograms.get(0)));
    List<Skill> skills2 = new ArrayList<>(result.get(resultPrograms.get(1)));

    assertEquals(1, skills1.size());
    assertEquals(3, skills2.size());
    assertEquals(List.of("b"), extractSkillNames(skills1));
    assertEquals(List.of("d", "e", "f"), extractSkillNames(skills2));
  }

  private ProgramProgress createProgramProgress(
      UUID id, String programName, List<String> skillNames) {
    Institution institution = FakeInstitution.create().toModel();
    Program program = Program.create(institution, programName, true);

    LinkedHashSet<Skill> skills =
        skillNames.stream()
            .map(name -> Skill.create(name, null))
            .collect(Collectors.toCollection(LinkedHashSet::new));

    return ProgramProgress.toDomain(id, program, student, skills);
  }

  private List<String> extractSkillNames(List<Skill> skills) {
    return skills.stream().map(Skill::getName).collect(Collectors.toList());
  }
}
