package fr.avenirsesr.portfolio.porgramprogress.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.porgramprogress.infrastructure.fixture.ProgramFixture;
import fr.avenirsesr.portfolio.porgramprogress.infrastructure.fixture.ProgramProgressFixture;
import fr.avenirsesr.portfolio.porgramprogress.infrastructure.fixture.SkillFixture;
import fr.avenirsesr.portfolio.porgramprogress.infrastructure.fixture.SkillLevelFixture;
import fr.avenirsesr.portfolio.programprogress.domain.model.Program;
import fr.avenirsesr.portfolio.programprogress.domain.model.ProgramProgress;
import fr.avenirsesr.portfolio.programprogress.domain.model.Skill;
import fr.avenirsesr.portfolio.programprogress.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.programprogress.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.programprogress.domain.port.output.repository.ProgramProgressRepository;
import fr.avenirsesr.portfolio.programprogress.domain.service.ProgramProgressServiceImpl;
import fr.avenirsesr.portfolio.shared.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ESortField;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ESortOrder;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import java.time.LocalDate;
import java.time.Period;
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
  private final ELanguage language = ELanguage.FRENCH;
  private final SortCriteria sortCriteria = new SortCriteria(ESortField.NAME, ESortOrder.ASC);

  @BeforeEach
  void setUp() {
    student = UserFixture.createStudent().toModel().toStudent();
  }

  @Test
  void shouldReturnEmptyListWhenRepositoryReturnsEmptyList() {
    when(programProgressRepository.findAllByStudent(any(Student.class), any(SortCriteria.class)))
        .thenReturn(List.of());

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

    when(programProgressRepository.findAllByStudent(any(Student.class), any(SortCriteria.class)))
        .thenReturn(List.of(programProgress3, programProgress2, programProgress1));

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

    when(programProgressRepository.findAllByStudent(any(Student.class), any(SortCriteria.class)))
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

    when(programProgressRepository.findAllByStudent(any(Student.class), any(SortCriteria.class)))
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

    when(programProgressRepository.findAllByStudent(any(Student.class), any(SortCriteria.class)))
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

    when(programProgressRepository.findAllByStudent(any(Student.class), any(SortCriteria.class)))
        .thenReturn(List.of(progress));

    // When
    Map<ProgramProgress, Set<Skill>> result = programProgressService.getSkillsOverview(student);
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

    when(programProgressRepository.findAllByStudent(any(Student.class), any(SortCriteria.class)))
        .thenReturn(List.of(progress));

    // When
    Map<ProgramProgress, Set<Skill>> result = programProgressService.getSkillsOverview(student);
    List<ProgramProgress> resultPrograms = new ArrayList<>(result.keySet());
    List<Skill> skills1 = new ArrayList<>(result.get(resultPrograms.getFirst()));
    List<SkillLevel> skillLevels1 = new ArrayList<>(skills1.getFirst().getSkillLevels());

    // Then
    assertEquals(1, skillLevels1.size());
    assertEquals(ESkillLevelStatus.UNDER_REVIEW, skillLevels1.getFirst().getStatus());
  }

  @Test
  void shouldReturnOnlyUnderAcquisitionSkillLevelBySkill() {
    // Given
    SkillLevel skillLevel1 =
        SkillLevelFixture.create().withStatus(ESkillLevelStatus.NOT_STARTED).toModel();
    SkillLevel skillLevel2 =
        SkillLevelFixture.create().withStatus(ESkillLevelStatus.UNDER_ACQUISITION).toModel();
    Skill skill1 =
        SkillFixture.create().withSkillLevels(Set.of(skillLevel1, skillLevel2)).toModel();

    ProgramProgress progress =
        ProgramProgressFixture.create().withStudent(student).withSkills(Set.of(skill1)).toModel();

    when(programProgressRepository.findAllByStudent(any(Student.class), any(SortCriteria.class)))
        .thenReturn(List.of(progress));

    // When
    Map<ProgramProgress, Set<Skill>> result = programProgressService.getSkillsOverview(student);
    List<ProgramProgress> resultPrograms = new ArrayList<>(result.keySet());
    List<Skill> skills1 = new ArrayList<>(result.get(resultPrograms.getFirst()));
    List<SkillLevel> skillLevels1 = new ArrayList<>(skills1.getFirst().getSkillLevels());

    // Then
    assertEquals(1, skillLevels1.size());
    assertEquals(ESkillLevelStatus.UNDER_ACQUISITION, skillLevels1.getFirst().getStatus());
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

    when(programProgressRepository.findAllByStudent(any(Student.class), any(SortCriteria.class)))
        .thenReturn(List.of(progress));

    // When
    Map<ProgramProgress, Set<Skill>> result = programProgressService.getSkillsOverview(student);
    List<ProgramProgress> resultPrograms = new ArrayList<>(result.keySet());
    List<Skill> skills1 = new ArrayList<>(result.get(resultPrograms.getFirst()));
    List<SkillLevel> skillLevels1 = new ArrayList<>(skills1.getFirst().getSkillLevels());

    // Then
    assertEquals(1, skillLevels1.size());
    assertEquals(ESkillLevelStatus.NOT_STARTED, skillLevels1.getFirst().getStatus());
  }

  @Test
  void shouldReturnNotStartedWhenUnderReviewSkillEndDateIsOutDatedForOverview() {
    // Given
    SkillLevel skillLevel1 =
        SkillLevelFixture.create().withStatus(ESkillLevelStatus.VALIDATED).toModel();
    SkillLevel skillLevel2 =
        SkillLevelFixture.create()
            .withStatus(ESkillLevelStatus.UNDER_REVIEW)
            .withEndDate(LocalDate.now().minus(Period.ofDays(1)))
            .toModel();
    SkillLevel skillLevel3 =
        SkillLevelFixture.create().withStatus(ESkillLevelStatus.NOT_STARTED).toModel();
    Skill skill1 =
        SkillFixture.create()
            .withSkillLevels(Set.of(skillLevel1, skillLevel2, skillLevel3))
            .toModel();

    ProgramProgress progress =
        ProgramProgressFixture.create().withStudent(student).withSkills(Set.of(skill1)).toModel();

    when(programProgressRepository.findAllByStudent(any(Student.class), any(SortCriteria.class)))
        .thenReturn(List.of(progress));

    // When
    Map<ProgramProgress, Set<Skill>> result = programProgressService.getSkillsOverview(student);
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

    when(programProgressRepository.findAllByStudent(any(Student.class), any(SortCriteria.class)))
        .thenReturn(List.of(progress));
    // When
    Map<ProgramProgress, Set<Skill>> result = programProgressService.getSkillsOverview(student);
    List<ProgramProgress> resultPrograms = new ArrayList<>(result.keySet());
    List<Skill> skills1 = new ArrayList<>(result.get(resultPrograms.getFirst()));

    // Then
    assertEquals(0, skills1.size());
  }

  @Test
  void shouldReturnAllSkillsInAllPrograms() {
    // Given
    ProgramProgress programProgress1 =
        createProgramProgress(UUID.randomUUID(), "A", List.of("b", "a", "c", "d", "e"));
    ProgramProgress programProgress2 =
        createProgramProgress(UUID.randomUUID(), "B", List.of("f", "g", "h"));
    ProgramProgress programProgress3 =
        createProgramProgress(
            UUID.randomUUID(), "B", List.of("f", "g", "h", "i", "j", "k", "l", "m", "n"));

    when(programProgressRepository.findAllByStudent(any(Student.class), any(SortCriteria.class)))
        .thenReturn(List.of(programProgress1, programProgress2, programProgress3));

    // When
    Map<ProgramProgress, Set<Skill>> result =
        programProgressService.getSkillsView(student, sortCriteria);
    List<ProgramProgress> resultPrograms = new ArrayList<>(result.keySet());

    // Then
    assertEquals(3, resultPrograms.size());
    assertEquals(5, resultPrograms.get(0).getSkills().size());
    assertEquals(3, resultPrograms.get(1).getSkills().size());
    assertEquals(9, resultPrograms.get(2).getSkills().size());
  }

  @Test
  void shouldReturnOnlySkillsWithCurrentSkillLevel() {
    // Given
    ProgramProgress programProgress1 =
        createProgramProgress(UUID.randomUUID(), "A", List.of("b", "a", "c", "d", "e"));
    ProgramProgress programProgress2 =
        createProgramProgress(UUID.randomUUID(), "B", List.of("f", "g", "h"));
    ProgramProgress programProgress3 =
        createProgramProgress(
            UUID.randomUUID(), "B", List.of("f", "g", "h", "i", "j", "k", "l", "m", "n"));

    when(programProgressRepository.findAllByStudent(any(Student.class), any(SortCriteria.class)))
        .thenReturn(List.of(programProgress1, programProgress2, programProgress3));

    // When
    Map<ProgramProgress, Set<Skill>> result =
        programProgressService.getSkillsView(student, sortCriteria);
    List<ProgramProgress> resultPrograms = new ArrayList<>(result.keySet());

    // Then
    assertEquals(3, resultPrograms.size());
    assertEquals(5, resultPrograms.get(0).getSkills().size());
    assertEquals(3, resultPrograms.get(1).getSkills().size());
    assertEquals(9, resultPrograms.get(2).getSkills().size());
  }

  @Test
  void shouldReturnNotStartedWhenUnderReviewSkillEndDateIsOutDatedForView() {
    // Given
    SkillLevel skillLevel1 =
        SkillLevelFixture.create().withStatus(ESkillLevelStatus.VALIDATED).toModel();
    SkillLevel skillLevel2 =
        SkillLevelFixture.create()
            .withStatus(ESkillLevelStatus.UNDER_REVIEW)
            .withEndDate(LocalDate.now().minus(Period.ofDays(1)))
            .toModel();
    SkillLevel skillLevel3 =
        SkillLevelFixture.create().withStatus(ESkillLevelStatus.NOT_STARTED).toModel();
    Skill skill1 =
        SkillFixture.create()
            .withSkillLevels(Set.of(skillLevel1, skillLevel2, skillLevel3))
            .toModel();

    ProgramProgress progress =
        ProgramProgressFixture.create().withStudent(student).withSkills(Set.of(skill1)).toModel();

    when(programProgressRepository.findAllByStudent(student, sortCriteria))
        .thenReturn(List.of(progress));

    // When
    Map<ProgramProgress, Set<Skill>> result =
        programProgressService.getSkillsView(student, sortCriteria);
    List<ProgramProgress> resultPrograms = new ArrayList<>(result.keySet());
    List<Skill> skills1 = new ArrayList<>(result.get(resultPrograms.getFirst()));
    List<SkillLevel> skillLevels1 = new ArrayList<>(skills1.getFirst().getSkillLevels());

    // Then
    assertEquals(1, skills1.size());
    assertEquals(ESkillLevelStatus.NOT_STARTED, skillLevels1.getFirst().getStatus());
  }

  @Test
  void shouldReturnNullWhenDontHaveValidCurrentSkillsForView() {
    // Given
    SkillLevel skillLevel1 =
        SkillLevelFixture.create().withStatus(ESkillLevelStatus.VALIDATED).toModel();
    SkillLevel skillLevel2 =
        SkillLevelFixture.create()
            .withStatus(ESkillLevelStatus.UNDER_REVIEW)
            .withEndDate(LocalDate.now().minus(Period.ofDays(1)))
            .toModel();
    Skill skill1 =
        SkillFixture.create().withSkillLevels(Set.of(skillLevel1, skillLevel2)).toModel();

    ProgramProgress progress =
        ProgramProgressFixture.create().withStudent(student).withSkills(Set.of(skill1)).toModel();

    when(programProgressRepository.findAllByStudent(student, sortCriteria))
        .thenReturn(List.of(progress));

    // When
    Map<ProgramProgress, Set<Skill>> result =
        programProgressService.getSkillsView(student, sortCriteria);
    List<ProgramProgress> resultPrograms = new ArrayList<>(result.keySet());
    List<Skill> skills1 = new ArrayList<>(result.get(resultPrograms.getFirst()));

    // Then
    assertEquals(0, skills1.size());
  }

  @Test
  void shouldReturnAllProgramProgressForStudent() {
    // Given
    Program program1 = ProgramFixture.create().withName("Beta").toModel();
    Program program2 = ProgramFixture.create().withName("Alpha").toModel();
    ProgramProgress programProgress1 =
        ProgramProgressFixture.create().withProgram(program1).toModel();
    ProgramProgress programProgress2 =
        ProgramProgressFixture.create().withProgram(program2).toModel();
    when(programProgressRepository.findAllWithoutSkillsByStudent(student))
        .thenReturn(List.of(programProgress1, programProgress2));

    // When
    List<ProgramProgress> result = programProgressService.getAllProgramProgress(student);

    // Then
    assertEquals(2, result.size());
    assertEquals("Alpha", result.get(0).getProgram().getName());
    assertEquals("Beta", result.get(1).getProgram().getName());
  }
}
