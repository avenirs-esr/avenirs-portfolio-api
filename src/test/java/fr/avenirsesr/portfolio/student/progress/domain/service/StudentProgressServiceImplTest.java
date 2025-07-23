package fr.avenirsesr.portfolio.student.progress.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.program.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.SkillLevelProgressFixture;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.TrainingPathFixture;
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
    List<StudentProgress> result =
        studentProgressService.getStudentProgressView(student, customSort);

    // Then
    assertEquals(1, result.size());
    assertEquals(progress, result.getFirst());
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
    List<StudentProgress> result = studentProgressService.getStudentProgressView(student, null);

    // THEN
    assertEquals(1, result.size(), "Only current progress should be returned");
    assertTrue(result.contains(currentProgress), "Current progress should be present");
    assertFalse(result.contains(pastProgress), "Past progress should be filtered out");
    assertFalse(result.contains(futureProgress), "Future progress should be filtered out");

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
    List<StudentProgress> result = studentProgressService.getStudentProgressView(student, null);

    // Then
    assertEquals(2, result.size(), "Should contain 2 StudentProgress");
    assertEquals(
        8,
        result.stream()
            .flatMap(studentProgress -> studentProgress.getAllSkillLevels().stream())
            .toList()
            .size(),
        "Should contain 2 StudentProgress");
    verify(studentProgressRepository).findAllByStudent(eq(student));
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
    PageCriteria pageCriteria = new PageCriteria(1, 1); // 1 élément par page

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
    PageCriteria pageCriteria = new PageCriteria(1, 5);

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
    PageCriteria pageCriteria = new PageCriteria(1, 10);

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
    PageCriteria pageCriteria = new PageCriteria(1, 5);

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
    PageCriteria pageCriteria = new PageCriteria(2, 2); // second page, 2 per page

    // When
    PagedResult<SkillProgress> result =
        studentProgressService.getAllTimeSkillsView(student, sortCriteria, pageCriteria);

    // Then
    assertEquals(1, result.content().size(), "Second page should contain only the last element");
    assertEquals(3, result.pageInfo().totalElements(), "Total elements should be 3");
    assertEquals(2, result.pageInfo().page(), "Current page should be 2");
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
    PageCriteria pageCriteria = new PageCriteria(1, 2); // limit to 2 elements

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
    PageCriteria pageCriteria = new PageCriteria(1, 10);

    // When
    PagedResult<SkillProgress> result =
        studentProgressService.getAllTimeSkillsView(student, sortCriteria, pageCriteria);

    // Then
    assertFalse(result.content().isEmpty(), "Result should not be empty");
    StudentProgress firstProgress = result.content().getFirst().studentProgress();
    assertEquals(currentProgress, firstProgress, "Current progress should appear first");
  }
}
