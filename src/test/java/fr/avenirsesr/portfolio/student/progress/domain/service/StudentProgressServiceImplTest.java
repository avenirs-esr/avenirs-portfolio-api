package fr.avenirsesr.portfolio.student.progress.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.program.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.*;
import fr.avenirsesr.portfolio.shared.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.shared.domain.model.PagedResult;
import fr.avenirsesr.portfolio.shared.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ESortField;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ESortOrder;
import fr.avenirsesr.portfolio.student.progress.domain.model.*;
import fr.avenirsesr.portfolio.student.progress.domain.port.output.repository.StudentProgressRepository;
import fr.avenirsesr.portfolio.student.progress.infrastructure.fixture.*;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StudentProgressServiceImplTest {
  @Mock private StudentProgressRepository studentProgressRepository;
  @InjectMocks private StudentProgressServiceImpl studentProgressService;
  private Student student;

  @BeforeEach
  void setUp() {
    student = UserFixture.createStudent().toModel().toStudent();
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
    boolean result = studentProgressService.isStudentFollowingAPCProgram(student);

    // Then
    assertTrue(result);
    verify(studentProgressRepository).findAllAPCByStudent(student);
  }

  @Test
  void shouldReturnFalseWhenStudentIsNotFollowingAnyProgramWithLearningMethod() {
    // Given
    when(studentProgressRepository.findAllAPCByStudent(student)).thenReturn(List.of());

    // When
    boolean result = studentProgressService.isStudentFollowingAPCProgram(student);

    // Then
    assertFalse(result);
    verify(studentProgressRepository).findAllAPCByStudent(student);
  }

  @Test
  void shouldReturnSkillsOverviewWithLimitedSkills() {
    var skillLevelsProgress = new ArrayList<SkillLevelProgress>();
    for (int i = 0; i < 8; i++) {
      skillLevelsProgress.add(
          SkillLevelProgressFixture.create(student)
              .withStatus(ESkillLevelStatus.TO_BE_EVALUATED)
              .toModel());
    }

    // Given
    StudentProgress progress1 =
        StudentProgressFixture.create()
            .withUser(student.getUser())
            .withSkillLevels(
                List.of(
                    skillLevelsProgress.get(0),
                    skillLevelsProgress.get(1),
                    skillLevelsProgress.get(2),
                    skillLevelsProgress.get(3)))
            .toModel();
    StudentProgress progress2 =
        StudentProgressFixture.create()
            .withUser(student.getUser())
            .withSkillLevels(
                List.of(
                    skillLevelsProgress.get(4),
                    skillLevelsProgress.get(5),
                    skillLevelsProgress.get(6),
                    skillLevelsProgress.get(7)))
            .toModel();

    when(studentProgressRepository.findAllByStudent(eq(student)))
        .thenReturn(List.of(progress1, progress2));

    // When
    Map<StudentProgress, List<SkillLevelProgress>> result =
        studentProgressService.getStudentProgressOverview(student);

    // Then
    assertEquals(2, result.size(), "Should contain 2 StudentProgress");
    assertTrue(result.containsKey(progress1));
    assertTrue(result.containsKey(progress2));

    int maxPerProgress = 3;
    assertTrue(result.get(progress1).size() <= maxPerProgress);
    assertTrue(result.get(progress2).size() <= maxPerProgress);

    verify(studentProgressRepository).findAllByStudent(eq(student));
  }

  @Test
  void shouldReturnEmptySkillsOverviewWhenNoProgress() {
    // Given
    when(studentProgressRepository.findAllByStudent(eq(student))).thenReturn(List.of());

    // When
    Map<StudentProgress, List<SkillLevelProgress>> result =
        studentProgressService.getStudentProgressOverview(student);

    // Then
    assertTrue(result.isEmpty(), "StudentProgress should be empty");
    verify(studentProgressRepository).findAllByStudent(eq(student));
  }

  @Test
  void shouldReturnOnlyCurrentStudentProgressOnOverview() {
    var skillLevelsProgress = new ArrayList<SkillLevelProgress>();
    for (int i = 0; i < 6; i++) {
      skillLevelsProgress.add(
          SkillLevelProgressFixture.create(student)
              .withStatus(ESkillLevelStatus.TO_BE_EVALUATED)
              .toModel());
    }

    LocalDate now = LocalDate.now();

    // Progress "current" : now between startDate and endDate
    StudentProgress currentProgress =
        StudentProgressFixture.create()
            .withUser(student.getUser())
            .withSkillLevels(List.of(skillLevelsProgress.get(0), skillLevelsProgress.get(1)))
            .withStartDate(now.minusDays(5))
            .toModel();

    // Progress "past" : endDate before now
    StudentProgress pastProgress =
        StudentProgressFixture.create()
            .withUser(student.getUser())
            .withSkillLevels(List.of(skillLevelsProgress.get(2), skillLevelsProgress.get(3)))
            .withStartDate(now.minusYears(10))
            .toModel();

    // Progress "future" : startDate after now
    StudentProgress futureProgress =
        StudentProgressFixture.create()
            .withUser(student.getUser())
            .withSkillLevels(List.of(skillLevelsProgress.get(4), skillLevelsProgress.get(5)))
            .withStartDate(now.plusYears(1))
            .toModel();

    // Mock repository to return all progress
    when(studentProgressRepository.findAllByStudent(eq(student)))
        .thenReturn(List.of(currentProgress, pastProgress, futureProgress));

    // WHEN
    Map<StudentProgress, List<SkillLevelProgress>> result =
        studentProgressService.getStudentProgressOverview(student);

    // THEN
    assertEquals(1, result.size(), "Only current progress should be returned");
    assertTrue(result.containsKey(currentProgress), "Current progress should be present");
    assertFalse(result.containsKey(pastProgress), "Past progress should be filtered out");
    assertFalse(result.containsKey(futureProgress), "Future progress should be filtered out");

    int maxPerProgress = 3;
    assertTrue(result.get(currentProgress).size() <= maxPerProgress);

    verify(studentProgressRepository).findAllByStudent(eq(student));
  }

  @Test
  void shouldReturnSkillsViewWithCustomSortCriteria() {
    // Given
    SortCriteria customSort = new SortCriteria(ESortField.DATE, ESortOrder.DESC);
    StudentProgress progress =
        StudentProgressFixture.create().withUser(student.getUser()).toModel();

    when(studentProgressRepository.findAllByStudent(eq(student))).thenReturn(List.of(progress));

    // When
    Map<StudentProgress, List<SkillLevelProgress>> result =
        studentProgressService.getStudentProgressView(student, customSort);

    // Then
    assertEquals(1, result.size());
    assertEquals(progress, result.keySet().stream().toList().getFirst());
    verify(studentProgressRepository).findAllByStudent(student);
  }

  @Test
  void shouldReturnOnlyCurrentStudentProgressOnView() {
    var skillLevelsProgress = new ArrayList<SkillLevelProgress>();
    for (int i = 0; i < 6; i++) {
      skillLevelsProgress.add(
          SkillLevelProgressFixture.create(student)
              .withStatus(ESkillLevelStatus.TO_BE_EVALUATED)
              .toModel());
    }

    LocalDate now = LocalDate.now();

    // Progress "current" : now between startDate and endDate
    StudentProgress currentProgress =
        StudentProgressFixture.create()
            .withUser(student.getUser())
            .withSkillLevels(List.of(skillLevelsProgress.get(0), skillLevelsProgress.get(1)))
            .withStartDate(now.minusDays(5))
            .toModel();

    // Progress "past" : endDate before now
    StudentProgress pastProgress =
        StudentProgressFixture.create()
            .withUser(student.getUser())
            .withSkillLevels(List.of(skillLevelsProgress.get(2), skillLevelsProgress.get(3)))
            .withStartDate(now.minusYears(10))
            .toModel();

    // Progress "future" : startDate after now
    StudentProgress futureProgress =
        StudentProgressFixture.create()
            .withUser(student.getUser())
            .withSkillLevels(List.of(skillLevelsProgress.get(4), skillLevelsProgress.get(5)))
            .withStartDate(now.plusYears(1))
            .toModel();

    // Mock repository to return all progress
    when(studentProgressRepository.findAllByStudent(eq(student)))
        .thenReturn(List.of(currentProgress, pastProgress, futureProgress));

    // WHEN
    Map<StudentProgress, List<SkillLevelProgress>> result =
        studentProgressService.getStudentProgressView(
            student, new SortCriteria(ESortField.DATE, ESortOrder.ASC));

    // THEN
    assertEquals(1, result.size(), "Only current progress should be returned");
    assertTrue(result.containsKey(currentProgress), "Current progress should be present");
    assertFalse(result.containsKey(pastProgress), "Past progress should be filtered out");
    assertFalse(result.containsKey(futureProgress), "Future progress should be filtered out");

    verify(studentProgressRepository).findAllByStudent(eq(student));
  }

  @Test
  void shouldReturnSkillsViewWithoutLimitedSkills() {
    var skillLevelsProgress = new ArrayList<SkillLevelProgress>();
    for (int i = 0; i < 8; i++) {
      skillLevelsProgress.add(
          SkillLevelProgressFixture.create(student)
              .withStatus(ESkillLevelStatus.TO_BE_EVALUATED)
              .toModel());
    }

    // Given
    StudentProgress progress1 =
        StudentProgressFixture.create()
            .withUser(student.getUser())
            .withSkillLevels(
                List.of(
                    skillLevelsProgress.get(0),
                    skillLevelsProgress.get(1),
                    skillLevelsProgress.get(2),
                    skillLevelsProgress.get(3)))
            .toModel();
    StudentProgress progress2 =
        StudentProgressFixture.create()
            .withUser(student.getUser())
            .withSkillLevels(
                List.of(
                    skillLevelsProgress.get(4),
                    skillLevelsProgress.get(5),
                    skillLevelsProgress.get(6),
                    skillLevelsProgress.get(7)))
            .toModel();

    when(studentProgressRepository.findAllByStudent(eq(student)))
        .thenReturn(List.of(progress1, progress2));

    // When
    Map<StudentProgress, List<SkillLevelProgress>> result =
        studentProgressService.getStudentProgressView(
            student, new SortCriteria(ESortField.DATE, ESortOrder.ASC));

    // Then
    assertEquals(2, result.size(), "Should contain 2 StudentProgress");
    assertEquals(
        8,
        result.keySet().stream()
            .flatMap(studentProgress -> studentProgress.getAllSkillLevels().stream())
            .toList()
            .size(),
        "Should contain 8 skillLevelsProgresses");
    verify(studentProgressRepository).findAllByStudent(eq(student));
  }

  @ParameterizedTest
  @EnumSource(ESortOrder.class)
  void shouldSortStudentProgressAndSkillsByName(ESortOrder order) {
    // Given
    var skillAA =
        SkillLevelProgressFixture.create(student)
            .withSkillLevel(
                SkillLevelFixture.create()
                    .withSkill(SkillFixture.create().withName("Skill A").toModel())
                    .toModel())
            .withStatus(ESkillLevelStatus.TO_BE_EVALUATED)
            .toModel();
    var skillAB =
        SkillLevelProgressFixture.create(student)
            .withSkillLevel(
                SkillLevelFixture.create()
                    .withSkill(SkillFixture.create().withName("Skill B").toModel())
                    .toModel())
            .withStatus(ESkillLevelStatus.TO_BE_EVALUATED)
            .toModel();
    var progressA =
        StudentProgressFixture.create()
            .withUser(student.getUser())
            .withTrainingPath(
                TrainingPathFixture.create()
                    .withProgram(ProgramFixture.create().withName("Program A").toModel())
                    .toModel())
            .withSkillLevels(List.of(skillAA, skillAB))
            .toModel();

    var skillBA =
        SkillLevelProgressFixture.create(student)
            .withSkillLevel(
                SkillLevelFixture.create()
                    .withSkill(SkillFixture.create().withName("Skill A").toModel())
                    .toModel())
            .withStatus(ESkillLevelStatus.TO_BE_EVALUATED)
            .toModel();
    var skillBB =
        SkillLevelProgressFixture.create(student)
            .withSkillLevel(
                SkillLevelFixture.create()
                    .withSkill(SkillFixture.create().withName("Skill B").toModel())
                    .toModel())
            .withStatus(ESkillLevelStatus.TO_BE_EVALUATED)
            .toModel();
    var progressB =
        StudentProgressFixture.create()
            .withUser(student.getUser())
            .withTrainingPath(
                TrainingPathFixture.create()
                    .withProgram(ProgramFixture.create().withName("Program B").toModel())
                    .toModel())
            .withSkillLevels(List.of(skillBB, skillBA))
            .toModel();

    when(studentProgressRepository.findAllByStudent(eq(student)))
        .thenReturn(List.of(progressB, progressA));

    // When
    var result =
        studentProgressService.getStudentProgressView(
            student, new SortCriteria(ESortField.NAME, order));

    // Then
    List<StudentProgress> orderedKeys = new ArrayList<>(result.keySet());
    if (order == ESortOrder.ASC) {
      assertEquals(progressA, orderedKeys.get(0));
      assertEquals(progressB, orderedKeys.get(1));
    } else {
      assertEquals(progressB, orderedKeys.get(0));
      assertEquals(progressA, orderedKeys.get(1));
    }

    // Vérifie que les skills de chaque progress sont triés par nom
    List<SkillLevelProgress> skillsOfFirst = result.get(orderedKeys.get(0));
    List<String> skillNamesOfFirst =
        skillsOfFirst.stream().map(slp -> slp.getSkillLevel().getSkill().getName()).toList();

    List<SkillLevelProgress> skillsOfSecond = result.get(orderedKeys.get(1));
    List<String> skillNamesOfSecond =
        skillsOfSecond.stream().map(slp -> slp.getSkillLevel().getSkill().getName()).toList();

    List<String> expectedOrder =
        (order == ESortOrder.ASC) ? List.of("Skill A", "Skill B") : List.of("Skill B", "Skill A");

    assertEquals(expectedOrder, skillNamesOfFirst);
    assertEquals(expectedOrder, skillNamesOfSecond);
  }

  @ParameterizedTest
  @EnumSource(ESortOrder.class)
  void shouldSortStudentProgressAndSkillsByDate(ESortOrder order) {
    // Given
    var skillOld1 =
        SkillLevelProgressFixture.create(student)
            .withStatus(ESkillLevelStatus.TO_BE_EVALUATED)
            .withStartDate(LocalDate.now().minusMonths(2))
            .toModel();
    var skillOld2 =
        SkillLevelProgressFixture.create(student)
            .withStatus(ESkillLevelStatus.TO_BE_EVALUATED)
            .withStartDate(LocalDate.now().minusMonths(1))
            .toModel();
    var skillNew1 =
        SkillLevelProgressFixture.create(student)
            .withStatus(ESkillLevelStatus.TO_BE_EVALUATED)
            .withStartDate(LocalDate.now().minusWeeks(2))
            .toModel();
    var skillNew2 =
        SkillLevelProgressFixture.create(student)
            .withStatus(ESkillLevelStatus.TO_BE_EVALUATED)
            .withStartDate(LocalDate.now().minusWeeks(1))
            .toModel();

    var progressOld =
        StudentProgressFixture.create()
            .withUser(student.getUser())
            .withStartDate(LocalDate.now().minusMonths(2))
            .withSkillLevels(List.of(skillOld2, skillOld1))
            .toModel();

    var progressNew =
        StudentProgressFixture.create()
            .withUser(student.getUser())
            .withStartDate(LocalDate.now().minusWeeks(2))
            .withSkillLevels(List.of(skillNew2, skillNew1))
            .toModel();

    when(studentProgressRepository.findAllByStudent(eq(student)))
        .thenReturn(List.of(progressOld, progressNew));

    // When
    var result =
        studentProgressService.getStudentProgressView(
            student, new SortCriteria(ESortField.DATE, order));

    // Then
    List<StudentProgress> orderedKeys = new ArrayList<>(result.keySet());
    if (order == ESortOrder.ASC) {
      assertEquals(progressOld, orderedKeys.get(0));
      assertEquals(progressNew, orderedKeys.get(1));
    } else {
      assertEquals(progressNew, orderedKeys.get(0));
      assertEquals(progressOld, orderedKeys.get(1));
    }

    List<SkillLevelProgress> skillsFirst = result.get(orderedKeys.get(0));
    List<LocalDate> datesFirst =
        skillsFirst.stream().map(SkillLevelProgress::getStartDate).toList();

    List<SkillLevelProgress> skillsSecond = result.get(orderedKeys.get(1));
    List<LocalDate> datesSecond =
        skillsSecond.stream().map(SkillLevelProgress::getStartDate).toList();

    List<LocalDate> expectedFirst =
        (order == ESortOrder.ASC)
            ? datesFirst.stream().sorted().toList()
            : datesFirst.stream().sorted(Comparator.reverseOrder()).toList();
    List<LocalDate> expectedSecond =
        (order == ESortOrder.ASC)
            ? datesSecond.stream().sorted().toList()
            : datesSecond.stream().sorted(Comparator.reverseOrder()).toList();

    assertEquals(expectedFirst, datesFirst);
    assertEquals(expectedSecond, datesSecond);
  }

  @Test
  void shouldReturnPagedSkillsLifeProjectView() {
    // Given
    LocalDate now = LocalDate.now();

    SkillLevelProgress skillLevel1 =
        SkillLevelProgressFixture.create(student)
            .withStatus(ESkillLevelStatus.TO_BE_EVALUATED)
            .toModel();
    SkillLevelProgress skillLevel2 =
        SkillLevelProgressFixture.create(student)
            .withStatus(ESkillLevelStatus.TO_BE_EVALUATED)
            .toModel();

    StudentProgress currentProgress =
        StudentProgressFixture.create()
            .withUser(student.getUser())
            .withSkillLevels(List.of(skillLevel1, skillLevel2))
            .withStartDate(now.minusDays(10))
            .toModel();

    when(studentProgressRepository.findAllByStudent(eq(student)))
        .thenReturn(List.of(currentProgress));

    SortCriteria sortCriteria = new SortCriteria(ESortField.DATE, ESortOrder.ASC);
    PageCriteria pageCriteria = new PageCriteria(0, 1); // 1 élément par page

    // When
    PagedResult<SkillProgress> result =
        studentProgressService.getAllTimeSkillsView(student, sortCriteria, pageCriteria);

    // Then
    assertEquals(1, result.content().size(), "Page should contain 1 element");
    assertEquals(2, result.pageInfo().totalElements(), "Total elements should match");
    verify(studentProgressRepository).findAllByStudent(eq(student));
  }

  @Test
  void shouldReturnEmptyResultWhenNoStudentProgress() {
    // Given
    when(studentProgressRepository.findAllByStudent(eq(student))).thenReturn(List.of());

    SortCriteria sortCriteria = new SortCriteria(ESortField.DATE, ESortOrder.ASC);
    PageCriteria pageCriteria = new PageCriteria(0, 5);

    // When
    PagedResult<SkillProgress> result =
        studentProgressService.getAllTimeSkillsView(student, sortCriteria, pageCriteria);

    // Then
    assertTrue(result.content().isEmpty(), "Results should be empty");
    assertEquals(0, result.pageInfo().totalElements(), "Total elements should be 0");
    verify(studentProgressRepository).findAllByStudent(eq(student));
  }

  @Test
  void shouldFilterOutFutureStudentProgress() {
    // Given
    LocalDate now = LocalDate.now();

    StudentProgress pastProgress =
        StudentProgressFixture.create()
            .withUser(student.getUser())
            .withSkillLevels(
                List.of(
                    SkillLevelProgressFixture.create(student)
                        .withStatus(ESkillLevelStatus.TO_BE_EVALUATED)
                        .toModel()))
            .withStartDate(now.minusDays(5))
            .toModel();

    StudentProgress futureProgress =
        StudentProgressFixture.create()
            .withUser(student.getUser())
            .withSkillLevels(
                List.of(
                    SkillLevelProgressFixture.create(student)
                        .withStatus(ESkillLevelStatus.TO_BE_EVALUATED)
                        .toModel()))
            .withStartDate(now.plusDays(5))
            .toModel();

    when(studentProgressRepository.findAllByStudent(eq(student)))
        .thenReturn(List.of(pastProgress, futureProgress));

    SortCriteria sortCriteria = new SortCriteria(ESortField.DATE, ESortOrder.ASC);
    PageCriteria pageCriteria = new PageCriteria(0, 10);

    // When
    PagedResult<SkillProgress> result =
        studentProgressService.getAllTimeSkillsView(student, sortCriteria, pageCriteria);

    // Then
    assertEquals(1, result.content().size(), "Only past progress should be returned");
    assertTrue(result.content().stream().allMatch(sp -> sp.studentProgress().equals(pastProgress)));
    verify(studentProgressRepository).findAllByStudent(eq(student));
  }

  @Test
  void shouldSortSkillProgressAccordingToSortCriteria() {
    // Given
    LocalDate now = LocalDate.now();

    SkillLevelProgress skillLevelOld =
        SkillLevelProgressFixture.create(student)
            .withStatus(ESkillLevelStatus.TO_BE_EVALUATED)
            .withStartDate(now.minusDays(10))
            .toModel();
    SkillLevelProgress skillLevelNew =
        SkillLevelProgressFixture.create(student)
            .withStatus(ESkillLevelStatus.TO_BE_EVALUATED)
            .withStartDate(now.minusDays(1))
            .toModel();

    StudentProgress progress =
        StudentProgressFixture.create()
            .withUser(student.getUser())
            .withSkillLevels(List.of(skillLevelOld, skillLevelNew))
            .withStartDate(now.minusDays(15))
            .toModel();

    when(studentProgressRepository.findAllByStudent(eq(student))).thenReturn(List.of(progress));

    SortCriteria sortCriteria = new SortCriteria(ESortField.DATE, ESortOrder.DESC);
    PageCriteria pageCriteria = new PageCriteria(0, 5);

    // When
    PagedResult<SkillProgress> result =
        studentProgressService.getAllTimeSkillsView(student, sortCriteria, pageCriteria);

    // Then
    assertEquals(skillLevelNew.getSkillLevel().getSkill(), result.content().get(0).skill());
    verify(studentProgressRepository).findAllByStudent(eq(student));
  }

  ///  --------

  @Test
  void shouldSkipFirstPageElementsWhenFetchingSecondPage() {
    // Given
    LocalDate now = LocalDate.now();
    SkillLevelProgress skill1 =
        SkillLevelProgressFixture.create(student)
            .withStatus(ESkillLevelStatus.TO_BE_EVALUATED)
            .withStartDate(now.minusDays(3))
            .toModel();
    SkillLevelProgress skill2 =
        SkillLevelProgressFixture.create(student)
            .withStatus(ESkillLevelStatus.TO_BE_EVALUATED)
            .withStartDate(now.minusDays(2))
            .toModel();
    SkillLevelProgress skill3 =
        SkillLevelProgressFixture.create(student)
            .withStatus(ESkillLevelStatus.TO_BE_EVALUATED)
            .withStartDate(now.minusDays(1))
            .toModel();

    StudentProgress progress =
        StudentProgressFixture.create()
            .withUser(student.getUser())
            .withSkillLevels(List.of(skill1, skill2, skill3))
            .withStartDate(now.minusDays(5))
            .toModel();

    when(studentProgressRepository.findAllByStudent(eq(student))).thenReturn(List.of(progress));

    SortCriteria sortCriteria = new SortCriteria(ESortField.DATE, ESortOrder.ASC);
    PageCriteria pageCriteria = new PageCriteria(1, 2); // second page, 2 per page

    // When
    PagedResult<SkillProgress> result =
        studentProgressService.getAllTimeSkillsView(student, sortCriteria, pageCriteria);

    // Then
    assertEquals(1, result.content().size(), "Second page should contain only the last element");
    assertEquals(3, result.pageInfo().totalElements(), "Total elements should be 3");
    assertEquals(1, result.pageInfo().page(), "Current page should be 1");
  }

  @Test
  void shouldRespectPaginationLimit() {
    // Given
    LocalDate now = LocalDate.now();
    SkillLevelProgress skill1 =
        SkillLevelProgressFixture.create(student)
            .withStatus(ESkillLevelStatus.TO_BE_EVALUATED)
            .withStartDate(now.minusDays(3))
            .toModel();
    SkillLevelProgress skill2 =
        SkillLevelProgressFixture.create(student)
            .withStatus(ESkillLevelStatus.TO_BE_EVALUATED)
            .withStartDate(now.minusDays(2))
            .toModel();
    SkillLevelProgress skill3 =
        SkillLevelProgressFixture.create(student)
            .withStatus(ESkillLevelStatus.TO_BE_EVALUATED)
            .withStartDate(now.minusDays(1))
            .toModel();

    StudentProgress progress =
        StudentProgressFixture.create()
            .withUser(student.getUser())
            .withSkillLevels(List.of(skill1, skill2, skill3))
            .withStartDate(now.minusDays(5))
            .toModel();

    when(studentProgressRepository.findAllByStudent(eq(student))).thenReturn(List.of(progress));

    SortCriteria sortCriteria = new SortCriteria(ESortField.DATE, ESortOrder.ASC);
    PageCriteria pageCriteria = new PageCriteria(0, 2); // limit to 2 elements

    // When
    PagedResult<SkillProgress> result =
        studentProgressService.getAllTimeSkillsView(student, sortCriteria, pageCriteria);

    // Then
    assertEquals(2, result.content().size(), "Only 2 elements should be returned");
    assertEquals(3, result.pageInfo().totalElements(), "Total elements should be 3");
  }

  @Test
  void shouldOrderCurrentProgressBeforeFinishedProgress() {
    // Given
    LocalDate now = LocalDate.now();
    SkillLevelProgress currentSkill =
        SkillLevelProgressFixture.create(student)
            .withStatus(ESkillLevelStatus.TO_BE_EVALUATED)
            .toModel();
    SkillLevelProgress pastSkill =
        SkillLevelProgressFixture.create(student)
            .withStatus(ESkillLevelStatus.TO_BE_EVALUATED)
            .toModel();

    StudentProgress currentProgress =
        StudentProgressFixture.create()
            .withUser(student.getUser())
            .withSkillLevels(List.of(currentSkill))
            .withStartDate(now.minusDays(5))
            .toModel();

    StudentProgress finishedProgress =
        StudentProgressFixture.create()
            .withUser(student.getUser())
            .withSkillLevels(List.of(pastSkill))
            .withStartDate(now.minusMonths(2), Period.ofMonths(1))
            .toModel();

    when(studentProgressRepository.findAllByStudent(eq(student)))
        .thenReturn(List.of(finishedProgress, currentProgress));

    SortCriteria sortCriteria = new SortCriteria(ESortField.DATE, ESortOrder.DESC);
    PageCriteria pageCriteria = new PageCriteria(0, 10);

    // When
    PagedResult<SkillProgress> result =
        studentProgressService.getAllTimeSkillsView(student, sortCriteria, pageCriteria);

    // Then
    assertFalse(result.content().isEmpty(), "Result should not be empty");
    StudentProgress firstProgress = result.content().getFirst().studentProgress();
    assertEquals(currentProgress, firstProgress, "Current progress should appear first");
  }
}
