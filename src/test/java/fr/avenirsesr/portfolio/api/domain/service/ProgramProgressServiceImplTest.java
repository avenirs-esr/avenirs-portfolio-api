package fr.avenirsesr.portfolio.api.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fixtures.*;
import fr.avenirsesr.portfolio.api.domain.model.*;
import fr.avenirsesr.portfolio.api.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.api.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.ProgramProgressRepository;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder.*;
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
  private ELanguage language = ELanguage.FRENCH;

  @BeforeEach
  void setUp() {
    student = UserFixture.createStudent().toModel().toStudent();
  }

  @Test
  void shouldReturnEmptyListWhenRepositoryReturnsEmptyList() {
    when(programProgressRepository.findAllByStudent(student, language)).thenReturn(List.of());

    Map<ProgramProgress, Set<Skill>> result =
        programProgressService.getSkillsOverview(student, language);

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

    when(programProgressRepository.findAllByStudent(any(Student.class), any(ELanguage.class)))
        .thenReturn(List.of(programProgress1, programProgress2, programProgress3));

    Map<ProgramProgress, Set<Skill>> result =
        programProgressService.getSkillsOverview(student, language);
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

    when(programProgressRepository.findAllByStudent(any(Student.class), any(ELanguage.class)))
        .thenReturn(List.of(programProgress1, programProgress2));

    Map<ProgramProgress, Set<Skill>> result =
        programProgressService.getSkillsOverview(student, language);
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

    when(programProgressRepository.findAllByStudent(any(Student.class), any(ELanguage.class)))
        .thenReturn(List.of(programProgress1, programProgress2));

    Map<ProgramProgress, Set<Skill>> result =
        programProgressService.getSkillsOverview(student, language);
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

    when(programProgressRepository.findAllByStudent(any(Student.class), any(ELanguage.class)))
        .thenReturn(List.of(programProgress1, programProgress2));

    Map<ProgramProgress, Set<Skill>> result =
        programProgressService.getSkillsOverview(student, language);
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
    Program program = ProgramFixture.create().withName(programName).toModel();

    LinkedHashSet<Skill> skills =
        skillNames.stream()
            .map(name -> SkillFixture.create().withName(name).toModel())
            .collect(Collectors.toCollection(LinkedHashSet::new));
    return ProgramProgressFixture.create()
        .withId(id)
        .withProgram(program)
        .withStudent(student)
        .withSkills(skills)
        .toModel();
  }

  private List<String> extractSkillNames(List<Skill> skills) {
    return skills.stream().map(Skill::getName).collect(Collectors.toList());
  }

  @Test
  void shouldReturnTrueWhenStudentIsFollowingProgramWithLearningMethod() {
    // Given
    var progressAPC = ProgramProgressFixture.createWithAPC().withStudent(student).toModel();

    when(programProgressRepository.findAllAPCByStudent(student)).thenReturn(List.of(progressAPC));

    // When
    boolean result = programProgressService.isStudentFollowingAPCProgram(student);

    // Then
    assertTrue(result);
    verify(programProgressRepository).findAllAPCByStudent(student);
  }

  @Test
  void shouldReturnFalseWhenStudentIsNotFollowingAnyProgramWithLearningMethod() {
    // Given
    when(programProgressRepository.findAllAPCByStudent(student)).thenReturn(List.of());

    // When
    boolean result = programProgressService.isStudentFollowingAPCProgram(student);

    // Then
    assertFalse(result);
    verify(programProgressRepository).findAllAPCByStudent(student);
  }

  @Test
  void shouldReturnOnlyCurrentSkillLevelBySkill() {
    // Given
    SkillLevel skillLevel1 =
        SkillLevelFixture.create().withStatus(ESkillLevelStatus.VALIDATED).toModel();
    SkillLevel skillLevel2 =
        SkillLevelFixture.create().withStatus(ESkillLevelStatus.TO_BE_EVALUATED).toModel();
    Skill skill1 =
        SkillFixture.create().withSkillLevels(Set.of(skillLevel1, skillLevel2)).toModel();

    ProgramProgress progress =
        ProgramProgressFixture.create().withStudent(student).withSkills(Set.of(skill1)).toModel();

    when(programProgressRepository.findAllByStudent(student, language))
        .thenReturn(List.of(progress));

    // When
    Map<ProgramProgress, Set<Skill>> result =
        programProgressService.getSkillsOverview(student, language);
    List<ProgramProgress> resultPrograms = new ArrayList<>(result.keySet());
    List<Skill> skills1 = new ArrayList<>(result.get(resultPrograms.getFirst()));
    List<SkillLevel> skillLevels1 = new ArrayList<>(skills1.getFirst().getSkillLevels());

    // Then
    assertEquals(1, skillLevels1.size());
    assertEquals(ESkillLevelStatus.TO_BE_EVALUATED, skillLevels1.getFirst().getStatus());
  }

  @Test
  void shouldReturnOnlyUnderReviewSkillLevelBySkill() {
    // Given
    SkillLevel skillLevel1 =
        SkillLevelFixture.create().withStatus(ESkillLevelStatus.UNDER_REVIEW).toModel();
    SkillLevel skillLevel2 =
        SkillLevelFixture.create().withStatus(ESkillLevelStatus.TO_BE_EVALUATED).toModel();
    Skill skill1 =
        SkillFixture.create().withSkillLevels(Set.of(skillLevel1, skillLevel2)).toModel();

    ProgramProgress progress =
        ProgramProgressFixture.create().withStudent(student).withSkills(Set.of(skill1)).toModel();

    when(programProgressRepository.findAllByStudent(student, language))
        .thenReturn(List.of(progress));

    // When
    Map<ProgramProgress, Set<Skill>> result =
        programProgressService.getSkillsOverview(student, language);
    List<ProgramProgress> resultPrograms = new ArrayList<>(result.keySet());
    List<Skill> skills1 = new ArrayList<>(result.get(resultPrograms.getFirst()));
    List<SkillLevel> skillLevels1 = new ArrayList<>(skills1.getFirst().getSkillLevels());

    // Then
    assertEquals(1, skillLevels1.size());
    assertEquals(ESkillLevelStatus.UNDER_REVIEW, skillLevels1.getFirst().getStatus());
  }

  @Test
  void shouldReturnOnlyToBeEvaluatedSkillLevelBySkill() {
    // Given
    SkillLevel skillLevel1 =
        SkillLevelFixture.create().withStatus(ESkillLevelStatus.NOT_STARTED).toModel();
    SkillLevel skillLevel2 =
        SkillLevelFixture.create().withStatus(ESkillLevelStatus.TO_BE_EVALUATED).toModel();
    Skill skill1 =
        SkillFixture.create().withSkillLevels(Set.of(skillLevel1, skillLevel2)).toModel();

    ProgramProgress progress =
        ProgramProgressFixture.create().withStudent(student).withSkills(Set.of(skill1)).toModel();

    when(programProgressRepository.findAllByStudent(student, language))
        .thenReturn(List.of(progress));

    // When
    Map<ProgramProgress, Set<Skill>> result =
        programProgressService.getSkillsOverview(student, language);
    List<ProgramProgress> resultPrograms = new ArrayList<>(result.keySet());
    List<Skill> skills1 = new ArrayList<>(result.get(resultPrograms.getFirst()));
    List<SkillLevel> skillLevels1 = new ArrayList<>(skills1.getFirst().getSkillLevels());

    // Then
    assertEquals(1, skillLevels1.size());
    assertEquals(ESkillLevelStatus.TO_BE_EVALUATED, skillLevels1.getFirst().getStatus());
  }

  @Test
  void shouldReturnOnlyNotStartedSkillLevelBySkill() {
    // Given
    SkillLevel skillLevel1 =
        SkillLevelFixture.create().withStatus(ESkillLevelStatus.VALIDATED).toModel();
    SkillLevel skillLevel2 =
        SkillLevelFixture.create().withStatus(ESkillLevelStatus.FAILED).toModel();
    SkillLevel skillLevel3 =
        SkillLevelFixture.create().withStatus(ESkillLevelStatus.NOT_STARTED).toModel();
    Skill skill1 =
        SkillFixture.create()
            .withSkillLevels(Set.of(skillLevel1, skillLevel2, skillLevel3))
            .toModel();

    ProgramProgress progress =
        ProgramProgressFixture.create().withStudent(student).withSkills(Set.of(skill1)).toModel();

    when(programProgressRepository.findAllByStudent(student, language))
        .thenReturn(List.of(progress));

    // When
    Map<ProgramProgress, Set<Skill>> result =
        programProgressService.getSkillsOverview(student, language);
    List<ProgramProgress> resultPrograms = new ArrayList<>(result.keySet());
    List<Skill> skills1 = new ArrayList<>(result.get(resultPrograms.getFirst()));
    List<SkillLevel> skillLevels1 = new ArrayList<>(skills1.getFirst().getSkillLevels());

    // Then
    assertEquals(1, skillLevels1.size());
    assertEquals(ESkillLevelStatus.NOT_STARTED, skillLevels1.getFirst().getStatus());
  }

  @Test
  void shouldReturnNullWhenSkillLevelsIsEmpty() {
    // Given
    Skill skill = SkillFixture.create().withSkillLevels(Set.of()).toModel();
    ProgramProgress progress =
        ProgramProgressFixture.create().withStudent(student).withSkills(Set.of(skill)).toModel();

    when(programProgressRepository.findAllByStudent(student, language))
        .thenReturn(List.of(progress));
    // When
    Map<ProgramProgress, Set<Skill>> result =
        programProgressService.getSkillsOverview(student, language);
    List<ProgramProgress> resultPrograms = new ArrayList<>(result.keySet());
    List<Skill> skills1 = new ArrayList<>(result.get(resultPrograms.getFirst()));

    // Then
    assertEquals(1, skills1.size());
    assertTrue(skills1.getFirst().getSkillLevels().isEmpty());
  }
}
