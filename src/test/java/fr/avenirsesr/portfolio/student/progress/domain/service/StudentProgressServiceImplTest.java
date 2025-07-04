package fr.avenirsesr.portfolio.student.progress.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.shared.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ESortField;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ESortOrder;
import fr.avenirsesr.portfolio.student.progress.domain.model.*;
import fr.avenirsesr.portfolio.student.progress.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.student.progress.domain.port.output.repository.StudentProgressRepository;
import fr.avenirsesr.portfolio.student.progress.infrastructure.fixture.*;
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
  private final ELanguage language = ELanguage.FRENCH;
  private final SortCriteria sortCriteria = new SortCriteria(ESortField.NAME, ESortOrder.ASC);
  @Mock private StudentProgressRepository studentProgressRepository;
  @InjectMocks private StudentProgressServiceImpl programProgressService;
  private Student student;

  @BeforeEach
  void setUp() {
    student = UserFixture.createStudent().toModel().toStudent();
  }

  @Test
  void shouldReturnEmptyListWhenRepositoryReturnsEmptyList() {
    when(studentProgressRepository.findAllByStudent(any(Student.class), any(SortCriteria.class)))
        .thenReturn(List.of());

    Map<TrainingPath, Set<StudentProgress>> result =
        programProgressService.getSkillsOverview(student);

    assertTrue(result.isEmpty());
  }

  @Test
  void shouldReturn3ProgramsSortedByProgramName() {
    UUID programProgressId1 = UUID.randomUUID();
    UUID programProgressId2 = UUID.randomUUID();
    UUID programProgressId3 = UUID.randomUUID();
    List<StudentProgress> studentProgress1 =
        createStudentProgress(programProgressId1, "Z", List.of("a", "b", "c"));
    List<StudentProgress> studentProgress2 =
        createStudentProgress(programProgressId2, "Y", List.of("x", "y"));
    List<StudentProgress> studentProgress3 =
        createStudentProgress(programProgressId3, "X", List.of("s"));
    List<StudentProgress> allProgress = new ArrayList<>();
    allProgress.addAll(studentProgress3);
    allProgress.addAll(studentProgress2);
    allProgress.addAll(studentProgress1);

    when(studentProgressRepository.findAllByStudent(any(Student.class), any(SortCriteria.class)))
        .thenReturn(allProgress);

    Map<TrainingPath, Set<StudentProgress>> result =
        programProgressService.getSkillsOverview(student);
    List<TrainingPath> resultPrograms = new ArrayList<>(result.keySet());

    assertEquals(3, resultPrograms.size());
    assertEquals(programProgressId3, resultPrograms.get(0).getId());
    assertEquals(programProgressId2, resultPrograms.get(1).getId());
    assertEquals(programProgressId1, resultPrograms.get(2).getId());
  }

  @Test
  void shouldLimitSkillsTo3WhenBothProgramsHaveAtLeast3Skills() {
    List<StudentProgress> studentProgress1 =
        createStudentProgress(UUID.randomUUID(), "A", List.of("b", "a", "c", "x"));
    List<StudentProgress> studentProgress2 =
        createStudentProgress(UUID.randomUUID(), "B", List.of("d", "e", "f", "g"));
    List<StudentProgress> allProgress = new ArrayList<>();
    allProgress.addAll(studentProgress2);
    allProgress.addAll(studentProgress1);

    when(studentProgressRepository.findAllByStudent(any(Student.class), any(SortCriteria.class)))
        .thenReturn(allProgress);

    Map<TrainingPath, Set<StudentProgress>> result =
        programProgressService.getSkillsOverview(student);
    List<TrainingPath> resultPrograms = new ArrayList<>(result.keySet());
    List<StudentProgress> studentProgresses1 = new ArrayList<>(result.get(resultPrograms.get(0)));
    List<StudentProgress> studentProgresses2 = new ArrayList<>(result.get(resultPrograms.get(1)));

    assertEquals(3, studentProgresses1.size());
    assertEquals(3, studentProgresses2.size());

    assertEquals(List.of("a", "b", "c"), extractSkillNames(studentProgresses1));
    assertEquals(List.of("d", "e", "f"), extractSkillNames(studentProgresses2));
  }

  @Test
  void shouldHaveATotalOf5SkillsWhenOneProgramHas2Skills() {
    List<StudentProgress> studentProgress1 =
        createStudentProgress(UUID.randomUUID(), "A", List.of("b", "a"));
    List<StudentProgress> studentProgress2 =
        createStudentProgress(UUID.randomUUID(), "B", List.of("d", "e", "f", "g"));
    List<StudentProgress> allProgress = new ArrayList<>();
    allProgress.addAll(studentProgress2);
    allProgress.addAll(studentProgress1);

    when(studentProgressRepository.findAllByStudent(any(Student.class), any(SortCriteria.class)))
        .thenReturn(allProgress);

    Map<TrainingPath, Set<StudentProgress>> result =
        programProgressService.getSkillsOverview(student);
    List<TrainingPath> resultPrograms = new ArrayList<>(result.keySet());
    List<StudentProgress> studentProgresses1 = new ArrayList<>(result.get(resultPrograms.get(0)));
    List<StudentProgress> studentProgresses2 = new ArrayList<>(result.get(resultPrograms.get(1)));

    assertEquals(2, studentProgresses1.size());
    assertEquals(3, studentProgresses2.size());
    assertEquals(List.of("a", "b"), extractSkillNames(studentProgresses1));
    assertEquals(List.of("d", "e", "f"), extractSkillNames(studentProgresses2));
  }

  @Test
  void shouldHaveATotalOf4SkillsWhenOneProgramHas1Skill() {
    List<StudentProgress> studentProgress1 =
        createStudentProgress(UUID.randomUUID(), "A", List.of("b"));
    List<StudentProgress> studentProgress2 =
        createStudentProgress(UUID.randomUUID(), "B", List.of("d", "e", "f", "g", "h"));
    List<StudentProgress> allProgress = new ArrayList<>();
    allProgress.addAll(studentProgress2);
    allProgress.addAll(studentProgress1);

    when(studentProgressRepository.findAllByStudent(any(Student.class), any(SortCriteria.class)))
        .thenReturn(allProgress);

    Map<TrainingPath, Set<StudentProgress>> result =
        programProgressService.getSkillsOverview(student);
    List<TrainingPath> resultPrograms = new ArrayList<>(result.keySet());
    List<StudentProgress> studentProgresses1 = new ArrayList<>(result.get(resultPrograms.get(0)));
    List<StudentProgress> studentProgresses2 = new ArrayList<>(result.get(resultPrograms.get(1)));

    assertEquals(1, studentProgresses1.size());
    assertEquals(3, studentProgresses2.size());
    assertEquals(List.of("b"), extractSkillNames(studentProgresses1));
    assertEquals(List.of("d", "e", "f"), extractSkillNames(studentProgresses2));
  }

  private List<StudentProgress> createStudentProgress(
      UUID id, String programName, List<String> skillNames) {
    Program program = ProgramFixture.create().withName(programName).toModel();
    TrainingPath trainingPath =
        TrainingPathFixture.create().withId(id).withProgram(program).toModel();
    List<StudentProgress> studentProgressList = new ArrayList<>();
    for (String skillName : skillNames) {
      Skill skill = SkillFixture.create().withName(skillName).toModel();
      StudentProgress studentProgress =
          StudentProgressFixture.create()
              .withTrainingPath(trainingPath)
              .withSkillLevel(SkillLevelFixture.create().withSkill(skill).toModel())
              .withUser(student.getUser())
              .toModel();
      studentProgressList.add(studentProgress);
    }

    return studentProgressList;
  }

  private List<String> extractSkillNames(List<StudentProgress> studentProgresses) {
    return studentProgresses.stream()
        .map(sp -> sp.getSkillLevel().getSkill().getName())
        .collect(Collectors.toList());
  }

  @Test
  void shouldReturnTrueWhenStudentIsFollowingProgramWithLearningMethod() {
    // Given
    var progressAPC = TrainingPathFixture.createWithAPC().toModel();
    StudentProgress progressAPCModel =
        StudentProgressFixture.create()
            .withTrainingPath(progressAPC)
            .withUser(student.getUser())
            .toModel();

    when(studentProgressRepository.findAllAPCByStudent(student))
        .thenReturn(List.of(progressAPCModel));

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
    TrainingPath trainingPath = TrainingPathFixture.create().toModel();
    StudentProgress progress =
        StudentProgressFixture.create()
            .withTrainingPath(trainingPath)
            .withSkillLevel(SkillLevelFixture.create().withSkill(skill1).toModel())
            .withUser(student.getUser())
            .toModel();

    when(studentProgressRepository.findAllByStudent(any(Student.class), any(SortCriteria.class)))
        .thenReturn(List.of(progress));

    // When
    Map<TrainingPath, Set<StudentProgress>> result =
        programProgressService.getSkillsOverview(student);
    List<TrainingPath> resultPrograms = new ArrayList<>(result.keySet());
    List<StudentProgress> studentProgresses1 =
        new ArrayList<>(result.get(resultPrograms.getFirst()));

    // Then
    assertEquals(1, studentProgresses1.size());
    assertEquals(ESkillLevelStatus.TO_BE_EVALUATED, studentProgresses1.getFirst().getStatus());
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
    TrainingPath trainingPath = TrainingPathFixture.create().toModel();
    StudentProgress progress =
        StudentProgressFixture.create()
            .withTrainingPath(trainingPath)
            .withSkillLevel(SkillLevelFixture.create().withSkill(skill1).toModel())
            .withUser(student.getUser())
            .toModel();

    when(studentProgressRepository.findAllByStudent(any(Student.class), any(SortCriteria.class)))
        .thenReturn(List.of(progress));

    // When
    Map<TrainingPath, Set<StudentProgress>> result =
        programProgressService.getSkillsOverview(student);
    List<TrainingPath> resultPrograms = new ArrayList<>(result.keySet());
    List<StudentProgress> studentProgresses1 =
        new ArrayList<>(result.get(resultPrograms.getFirst()));

    // Then
    assertEquals(1, studentProgresses1.size());
    assertEquals(ESkillLevelStatus.UNDER_REVIEW, studentProgresses1.getFirst().getStatus());
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
    TrainingPath trainingPath = TrainingPathFixture.create().toModel();
    StudentProgress progress =
        StudentProgressFixture.create()
            .withTrainingPath(trainingPath)
            .withSkillLevel(SkillLevelFixture.create().withSkill(skill1).toModel())
            .withUser(student.getUser())
            .toModel();

    when(studentProgressRepository.findAllByStudent(any(Student.class), any(SortCriteria.class)))
        .thenReturn(List.of(progress));

    // When
    Map<TrainingPath, Set<StudentProgress>> result =
        programProgressService.getSkillsOverview(student);
    List<TrainingPath> resultPrograms = new ArrayList<>(result.keySet());
    List<StudentProgress> studentProgresses1 =
        new ArrayList<>(result.get(resultPrograms.getFirst()));

    // Then
    assertEquals(1, studentProgresses1.size());
    assertEquals(ESkillLevelStatus.UNDER_ACQUISITION, studentProgresses1.getFirst().getStatus());
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
    TrainingPath trainingPath = TrainingPathFixture.create().toModel();
    StudentProgress progress =
        StudentProgressFixture.create()
            .withTrainingPath(trainingPath)
            .withSkillLevel(SkillLevelFixture.create().withSkill(skill1).toModel())
            .withUser(student.getUser())
            .toModel();

    when(studentProgressRepository.findAllByStudent(any(Student.class), any(SortCriteria.class)))
        .thenReturn(List.of(progress));

    // When
    Map<TrainingPath, Set<StudentProgress>> result =
        programProgressService.getSkillsOverview(student);
    List<TrainingPath> resultPrograms = new ArrayList<>(result.keySet());
    List<StudentProgress> studentProgresses1 =
        new ArrayList<>(result.get(resultPrograms.getFirst()));
    // Then
    assertEquals(1, studentProgresses1.size());
    assertEquals(ESkillLevelStatus.NOT_STARTED, studentProgresses1.getFirst().getStatus());
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
    TrainingPath trainingPath = TrainingPathFixture.create().toModel();
    StudentProgress progress =
        StudentProgressFixture.create()
            .withTrainingPath(trainingPath)
            .withSkillLevel(SkillLevelFixture.create().withSkill(skill1).toModel())
            .withUser(student.getUser())
            .toModel();

    when(studentProgressRepository.findAllByStudent(any(Student.class), any(SortCriteria.class)))
        .thenReturn(List.of(progress));

    // When
    Map<TrainingPath, Set<StudentProgress>> result =
        programProgressService.getSkillsOverview(student);
    List<TrainingPath> resultPrograms = new ArrayList<>(result.keySet());
    List<StudentProgress> studentProgresses1 =
        new ArrayList<>(result.get(resultPrograms.getFirst()));

    // Then
    assertEquals(1, studentProgresses1.size());
    assertEquals(ESkillLevelStatus.NOT_STARTED, studentProgresses1.getFirst().getStatus());
  }

  @Test
  void shouldReturnNullWhenSkillLevelsIsEmpty() {
    // Given
    Skill skill = SkillFixture.create().withSkillLevels(Set.of()).toModel();
    TrainingPath trainingPath = TrainingPathFixture.create().toModel();
    StudentProgress progress =
        StudentProgressFixture.create()
            .withTrainingPath(trainingPath)
            .withSkillLevel(SkillLevelFixture.create().withSkill(skill).toModel())
            .withUser(student.getUser())
            .toModel();

    when(studentProgressRepository.findAllByStudent(any(Student.class), any(SortCriteria.class)))
        .thenReturn(List.of(progress));
    // When
    Map<TrainingPath, Set<StudentProgress>> result =
        programProgressService.getSkillsOverview(student);
    List<TrainingPath> resultPrograms = new ArrayList<>(result.keySet());
    List<StudentProgress> studentProgresses1 =
        new ArrayList<>(result.get(resultPrograms.getFirst()));

    // Then
    assertEquals(0, studentProgresses1.size());
  }

  @Test
  void shouldReturnAllSkillsInAllPrograms() {
    // Given
    List<StudentProgress> studentProgress1 =
        createStudentProgress(UUID.randomUUID(), "A", List.of("b", "a", "c", "d", "e"));
    List<StudentProgress> studentProgress2 =
        createStudentProgress(UUID.randomUUID(), "B", List.of("f", "g", "h"));
    List<StudentProgress> studentProgress3 =
        createStudentProgress(
            UUID.randomUUID(), "B", List.of("f", "g", "h", "i", "j", "k", "l", "m", "n"));
    List<StudentProgress> allProgress = new ArrayList<>();
    allProgress.addAll(studentProgress1);
    allProgress.addAll(studentProgress2);
    allProgress.addAll(studentProgress3);

    when(studentProgressRepository.findAllByStudent(any(Student.class), any(SortCriteria.class)))
        .thenReturn(allProgress);

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
    List<StudentProgress> studentProgress1 =
        createStudentProgress(UUID.randomUUID(), "A", List.of("b", "a", "c", "d", "e"));
    List<StudentProgress> studentProgress2 =
        createStudentProgress(UUID.randomUUID(), "B", List.of("f", "g", "h"));
    List<StudentProgress> studentProgress3 =
        createStudentProgress(
            UUID.randomUUID(), "B", List.of("f", "g", "h", "i", "j", "k", "l", "m", "n"));
    List<StudentProgress> allProgress = new ArrayList<>();
    allProgress.addAll(studentProgress1);
    allProgress.addAll(studentProgress2);
    allProgress.addAll(studentProgress3);

    when(studentProgressRepository.findAllByStudent(any(Student.class), any(SortCriteria.class)))
        .thenReturn(allProgress);

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
    TrainingPath trainingPath = TrainingPathFixture.create().toModel();
    StudentProgress progress =
        StudentProgressFixture.create()
            .withTrainingPath(trainingPath)
            .withSkillLevel(SkillLevelFixture.create().withSkill(skill1).toModel())
            .withUser(student.getUser())
            .toModel();

    when(studentProgressRepository.findAllByStudent(student, sortCriteria))
        .thenReturn(List.of(progress));

    // When
    Map<TrainingPath, Set<StudentProgress>> result =
        programProgressService.getSkillsView(student, sortCriteria);
    List<TrainingPath> resultPrograms = new ArrayList<>(result.keySet());
    List<StudentProgress> studentProgresses1 =
        new ArrayList<>(result.get(resultPrograms.getFirst()));

    // Then
    assertEquals(1, studentProgresses1.size());
    assertEquals(ESkillLevelStatus.NOT_STARTED, studentProgresses1.getFirst().getStatus());
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
    TrainingPath trainingPath = TrainingPathFixture.create().toModel();
    StudentProgress progress =
        StudentProgressFixture.create()
            .withTrainingPath(trainingPath)
            .withSkillLevel(SkillLevelFixture.create().withSkill(skill1).toModel())
            .withUser(student.getUser())
            .toModel();

    when(studentProgressRepository.findAllByStudent(student, sortCriteria))
        .thenReturn(List.of(progress));

    // When
    Map<TrainingPath, Set<StudentProgress>> result =
        programProgressService.getSkillsView(student, sortCriteria);
    List<TrainingPath> resultPrograms = new ArrayList<>(result.keySet());
    List<StudentProgress> studentProgresses1 =
        new ArrayList<>(result.get(resultPrograms.getFirst()));

    // Then
    assertEquals(0, studentProgresses1.size());
  }

  @Test
  void shouldReturnAllProgramProgressForStudent() {
    // Given
    Program program1 = ProgramFixture.create().withName("Beta").toModel();
    Program program2 = ProgramFixture.create().withName("Alpha").toModel();
    TrainingPath trainingPath1 = TrainingPathFixture.create().withProgram(program1).toModel();
    TrainingPath trainingPath2 = TrainingPathFixture.create().withProgram(program2).toModel();
    List<TrainingPath> allTrainingPaths = new ArrayList<>();
    allTrainingPaths.add(trainingPath1);
    allTrainingPaths.add(trainingPath2);

    when(studentProgressRepository.findAllWithoutSkillsByStudent(student))
        .thenReturn(allTrainingPaths);

    // When
    List<TrainingPath> result = programProgressService.getAllStudentProgress(student);

    // Then
    assertEquals(2, result.size());
    assertEquals("Alpha", result.get(0).getProgram().getName());
    assertEquals("Beta", result.get(1).getProgram().getName());
  }
}
