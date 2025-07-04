package fr.avenirsesr.portfolio.student.progress.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.student.progress.domain.model.*;
import fr.avenirsesr.portfolio.student.progress.infrastructure.fixture.ProgramFixture;
import fr.avenirsesr.portfolio.student.progress.infrastructure.fixture.ProgramProgressFixture;
import fr.avenirsesr.portfolio.student.progress.infrastructure.fixture.SkillFixture;
import fr.avenirsesr.portfolio.student.progress.infrastructure.fixture.SkillLevelFixture;
import fr.avenirsesr.portfolio.student.progress.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.student.progress.domain.port.output.repository.StudentProgressRepository;
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
public class StudentProgressServiceImplTest {
  @Mock private StudentProgressRepository studentProgressRepository;

  @InjectMocks private StudentProgressServiceImpl programProgressService;

  private Student student;
  private final ELanguage language = ELanguage.FRENCH;
  private final SortCriteria sortCriteria = new SortCriteria(ESortField.NAME, ESortOrder.ASC);

  @BeforeEach
  void setUp() {
    student = UserFixture.createStudent().toModel().toStudent();
  }

  @Test
  void shouldReturnEmptyListWhenRepositoryReturnsEmptyList() {
    when(studentProgressRepository.findAllByStudent(any(Student.class), any(SortCriteria.class)))
        .thenReturn(List.of());

    Map<TrainingPath, Set<StudentProgress>> result = programProgressService.getSkillsOverview(student);

    assertTrue(result.isEmpty());
  }

  @Test
  void shouldReturn3ProgramsSortedByProgramName() {
    UUID programProgressId1 = UUID.randomUUID();
    UUID programProgressId2 = UUID.randomUUID();
    UUID programProgressId3 = UUID.randomUUID();
    TrainingPath trainingPath1 =
        createStudentProgress(programProgressId1, "Z", List.of("a", "b", "c"));
    TrainingPath trainingPath2 =
        createStudentProgress(programProgressId2, "Y", List.of("x", "y"));
    TrainingPath trainingPath3 = createStudentProgress(programProgressId3, "X", List.of("s"));

    when(studentProgressRepository.findAllByStudent(any(Student.class), any(SortCriteria.class)))
        .thenReturn(List.of(trainingPath3, trainingPath2, trainingPath1));

    Map<TrainingPath, Set<StudentProgress>> result = programProgressService.getSkillsOverview(student);
    List<TrainingPath> resultPrograms = new ArrayList<>(result.keySet());

    assertEquals(3, resultPrograms.size());
    assertEquals(programProgressId3, resultPrograms.get(0).getId());
    assertEquals(programProgressId2, resultPrograms.get(1).getId());
    assertEquals(programProgressId1, resultPrograms.get(2).getId());
  }

  @Test
  void shouldLimitSkillsTo3WhenBothProgramsHaveAtLeast3Skills() {
    TrainingPath trainingPath1 =
        createStudentProgress(UUID.randomUUID(), "A", List.of("b", "a", "c", "x"));
    TrainingPath trainingPath2 =
        createStudentProgress(UUID.randomUUID(), "B", List.of("d", "e", "f", "g"));

    when(studentProgressRepository.findAllByStudent(any(Student.class), any(SortCriteria.class)))
        .thenReturn(List.of(trainingPath1, trainingPath2));

    Map<TrainingPath, Set<StudentProgress>> result = programProgressService.getSkillsOverview(student);
    List<TrainingPath> resultPrograms = new ArrayList<>(result.keySet());
    List<Skill> skills1 = new ArrayList<>(result.get(resultPrograms.get(0)));
    List<Skill> skills2 = new ArrayList<>(result.get(resultPrograms.get(1)));

    assertEquals(3, skills1.size());
    assertEquals(3, skills2.size());

    assertEquals(List.of("a", "b", "c"), extractSkillNames(skills1));
    assertEquals(List.of("d", "e", "f"), extractSkillNames(skills2));
  }

  @Test
  void shouldHaveATotalOf5SkillsWhenOneProgramHas2Skills() {
    TrainingPath trainingPath1 =
        createStudentProgress(UUID.randomUUID(), "A", List.of("b", "a"));
    TrainingPath trainingPath2 =
        createStudentProgress(UUID.randomUUID(), "B", List.of("d", "e", "f", "g"));

    when(studentProgressRepository.findAllByStudent(any(Student.class), any(SortCriteria.class)))
        .thenReturn(List.of(trainingPath1, trainingPath2));

    Map<TrainingPath, Set<StudentProgress>> result = programProgressService.getSkillsOverview(student);
    List<TrainingPath> resultPrograms = new ArrayList<>(result.keySet());
    List<Skill> skills1 = new ArrayList<>(result.get(resultPrograms.get(0)));
    List<Skill> skills2 = new ArrayList<>(result.get(resultPrograms.get(1)));

    assertEquals(2, skills1.size());
    assertEquals(3, skills2.size());
    assertEquals(List.of("a", "b"), extractSkillNames(skills1));
    assertEquals(List.of("d", "e", "f"), extractSkillNames(skills2));
  }

  @Test
  void shouldHaveATotalOf4SkillsWhenOneProgramHas1Skill() {
    TrainingPath trainingPath1 = createStudentProgress(UUID.randomUUID(), "A", List.of("b"));
    TrainingPath trainingPath2 =
        createStudentProgress(UUID.randomUUID(), "B", List.of("d", "e", "f", "g", "h"));

    when(studentProgressRepository.findAllByStudent(any(Student.class), any(SortCriteria.class)))
        .thenReturn(List.of(trainingPath1, trainingPath2));

    Map<TrainingPath, Set<StudentProgress>> result = programProgressService.getSkillsOverview(student);
    List<TrainingPath> resultPrograms = new ArrayList<>(result.keySet());
    List<Skill> skills1 = new ArrayList<>(result.get(resultPrograms.get(0)));
    List<Skill> skills2 = new ArrayList<>(result.get(resultPrograms.get(1)));

    assertEquals(1, skills1.size());
    assertEquals(3, skills2.size());
    assertEquals(List.of("b"), extractSkillNames(skills1));
    assertEquals(List.of("d", "e", "f"), extractSkillNames(skills2));
  }

  private TrainingPath createStudentProgress(
      UUID id, String programName, List<String> skillNames) {
    Program program = ProgramFixture.create().withName(programName).toModel();

    LinkedHashSet<StudentProgress> skills =
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

    when(studentProgressRepository.findAllAPCByStudent(student)).thenReturn(List.of(progressAPC));

    // When
    boolean result = programProgressService.isStudentFollowingAPCProgram(student);

    // Then
    assertTrue(result);
    verify(studentProgressRepository).findAllAPCByStudent(student);
  }

  @Test
  void shouldReturnFalseWhenStudentIsNotFollowingAnyProgramWithLearningMethod() {
    // Given
    when(studentProgressRepository.findAllAPCByStudent(student)).thenReturn(List.of());

    // When
    boolean result = programProgressService.isStudentFollowingAPCProgram(student);

    // Then
    assertFalse(result);
    verify(studentProgressRepository).findAllAPCByStudent(student);
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

    TrainingPath progress =
        ProgramProgressFixture.create().withStudent(student).withSkills(Set.of(skill1)).toModel();

    when(studentProgressRepository.findAllByStudent(any(Student.class), any(SortCriteria.class)))
        .thenReturn(List.of(progress));

    // When
    Map<TrainingPath, Set<StudentProgress>> result = programProgressService.getSkillsOverview(student);
    List<TrainingPath> resultPrograms = new ArrayList<>(result.keySet());
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

    TrainingPath progress =
        ProgramProgressFixture.create().withStudent(student).withSkills(Set.of(skill1)).toModel();

    when(studentProgressRepository.findAllByStudent(any(Student.class), any(SortCriteria.class)))
        .thenReturn(List.of(progress));

    // When
    Map<TrainingPath, Set<StudentProgress>> result = programProgressService.getSkillsOverview(student);
    List<TrainingPath> resultPrograms = new ArrayList<>(result.keySet());
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

    TrainingPath progress =
        ProgramProgressFixture.create().withStudent(student).withSkills(Set.of(skill1)).toModel();

    when(studentProgressRepository.findAllByStudent(any(Student.class), any(SortCriteria.class)))
        .thenReturn(List.of(progress));

    // When
    Map<TrainingPath, Set<StudentProgress>> result = programProgressService.getSkillsOverview(student);
    List<TrainingPath> resultPrograms = new ArrayList<>(result.keySet());
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

    TrainingPath progress =
        ProgramProgressFixture.create().withStudent(student).withSkills(Set.of(skill1)).toModel();

    when(studentProgressRepository.findAllByStudent(any(Student.class), any(SortCriteria.class)))
        .thenReturn(List.of(progress));

    // When
    Map<TrainingPath, Set<StudentProgress>> result = programProgressService.getSkillsOverview(student);
    List<TrainingPath> resultPrograms = new ArrayList<>(result.keySet());
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

    TrainingPath progress =
        ProgramProgressFixture.create().withStudent(student).withSkills(Set.of(skill1)).toModel();

    when(studentProgressRepository.findAllByStudent(any(Student.class), any(SortCriteria.class)))
        .thenReturn(List.of(progress));

    // When
    Map<TrainingPath, Set<StudentProgress>> result = programProgressService.getSkillsOverview(student);
    List<TrainingPath> resultPrograms = new ArrayList<>(result.keySet());
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
    TrainingPath progress =
        ProgramProgressFixture.create().withStudent(student).withSkills(Set.of(skill)).toModel();

    when(studentProgressRepository.findAllByStudent(any(Student.class), any(SortCriteria.class)))
        .thenReturn(List.of(progress));
    // When
    Map<TrainingPath, Set<StudentProgress>> result = programProgressService.getSkillsOverview(student);
    List<TrainingPath> resultPrograms = new ArrayList<>(result.keySet());
    List<Skill> skills1 = new ArrayList<>(result.get(resultPrograms.getFirst()));

    // Then
    assertEquals(0, skills1.size());
  }

  @Test
  void shouldReturnAllSkillsInAllPrograms() {
    // Given
    TrainingPath trainingPath1 =
        createStudentProgress(UUID.randomUUID(), "A", List.of("b", "a", "c", "d", "e"));
    TrainingPath trainingPath2 =
        createStudentProgress(UUID.randomUUID(), "B", List.of("f", "g", "h"));
    TrainingPath trainingPath3 =
        createStudentProgress(
            UUID.randomUUID(), "B", List.of("f", "g", "h", "i", "j", "k", "l", "m", "n"));

    when(studentProgressRepository.findAllByStudent(any(Student.class), any(SortCriteria.class)))
        .thenReturn(List.of(trainingPath1, trainingPath2, trainingPath3));

    // When
    Map<TrainingPath, Set<StudentProgress>> result =
        programProgressService.getSkillsView(student, sortCriteria);
    List<TrainingPath> resultPrograms = new ArrayList<>(result.keySet());

    // Then
    assertEquals(3, resultPrograms.size());
    assertEquals(5, resultPrograms.get(0).getSkillLevels().size());
    assertEquals(3, resultPrograms.get(1).getSkillLevels().size());
    assertEquals(9, resultPrograms.get(2).getSkillLevels().size());
  }

  @Test
  void shouldReturnOnlySkillsWithCurrentSkillLevel() {
    // Given
    TrainingPath trainingPath1 =
        createStudentProgress(UUID.randomUUID(), "A", List.of("b", "a", "c", "d", "e"));
    TrainingPath trainingPath2 =
        createStudentProgress(UUID.randomUUID(), "B", List.of("f", "g", "h"));
    TrainingPath trainingPath3 =
        createStudentProgress(
            UUID.randomUUID(), "B", List.of("f", "g", "h", "i", "j", "k", "l", "m", "n"));

    when(studentProgressRepository.findAllByStudent(any(Student.class), any(SortCriteria.class)))
        .thenReturn(List.of(trainingPath1, trainingPath2, trainingPath3));

    // When
    Map<TrainingPath, Set<StudentProgress>> result =
        programProgressService.getSkillsView(student, sortCriteria);
    List<TrainingPath> resultPrograms = new ArrayList<>(result.keySet());

    // Then
    assertEquals(3, resultPrograms.size());
    assertEquals(5, resultPrograms.get(0).getSkillLevels().size());
    assertEquals(3, resultPrograms.get(1).getSkillLevels().size());
    assertEquals(9, resultPrograms.get(2).getSkillLevels().size());
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

    TrainingPath progress =
        ProgramProgressFixture.create().withStudent(student).withSkills(Set.of(skill1)).toModel();

    when(studentProgressRepository.findAllByStudent(student, sortCriteria))
        .thenReturn(List.of(progress));

    // When
    Map<TrainingPath, Set<StudentProgress>> result =
        programProgressService.getSkillsView(student, sortCriteria);
    List<TrainingPath> resultPrograms = new ArrayList<>(result.keySet());
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

    TrainingPath progress =
        ProgramProgressFixture.create().withStudent(student).withSkills(Set.of(skill1)).toModel();

    when(studentProgressRepository.findAllByStudent(student, sortCriteria))
        .thenReturn(List.of(progress));

    // When
    Map<TrainingPath, Set<StudentProgress>> result =
        programProgressService.getSkillsView(student, sortCriteria);
    List<TrainingPath> resultPrograms = new ArrayList<>(result.keySet());
    List<Skill> skills1 = new ArrayList<>(result.get(resultPrograms.getFirst()));

    // Then
    assertEquals(0, skills1.size());
  }

  @Test
  void shouldReturnAllProgramProgressForStudent() {
    // Given
    Program program1 = ProgramFixture.create().withName("Beta").toModel();
    Program program2 = ProgramFixture.create().withName("Alpha").toModel();
    TrainingPath trainingPath1 =
        ProgramProgressFixture.create().withProgram(program1).toModel();
    TrainingPath trainingPath2 =
        ProgramProgressFixture.create().withProgram(program2).toModel();
    when(studentProgressRepository.findAllWithoutSkillsByStudent(student))
        .thenReturn(List.of(trainingPath1, trainingPath2));

    // When
    List<TrainingPath> result = programProgressService.getAllStudentProgress(student);

    // Then
    assertEquals(2, result.size());
    assertEquals("Alpha", result.get(0).getProgram().getName());
    assertEquals("Beta", result.get(1).getProgram().getName());
  }
}
