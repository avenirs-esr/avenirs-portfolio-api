package fr.avenirsesr.portfolio.student.progress.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.program.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.SkillLevelProgressFixture;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.TrainingPathFixture;
import fr.avenirsesr.portfolio.shared.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ESortField;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ESortOrder;
import fr.avenirsesr.portfolio.student.progress.domain.model.*;
import fr.avenirsesr.portfolio.student.progress.domain.port.output.repository.StudentProgressRepository;
import fr.avenirsesr.portfolio.student.progress.infrastructure.fixture.*;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
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

    when(studentProgressRepository.findAllByStudent(eq(student), any(SortCriteria.class)))
        .thenReturn(List.of(progress1, progress2));

    // When
    Map<StudentProgress, List<SkillLevelProgress>> result =
        studentProgressService.getSkillsOverview(student);

    // Then
    assertEquals(2, result.size(), "Should contain 2 StudentProgress");
    assertTrue(result.containsKey(progress1));
    assertTrue(result.containsKey(progress2));

    int maxPerProgress = 3;
    assertTrue(result.get(progress1).size() <= maxPerProgress);
    assertTrue(result.get(progress2).size() <= maxPerProgress);

    verify(studentProgressRepository).findAllByStudent(eq(student), any(SortCriteria.class));
  }

  @Test
  void shouldReturnEmptySkillsOverviewWhenNoProgress() {
    // Given
    when(studentProgressRepository.findAllByStudent(eq(student), any(SortCriteria.class)))
        .thenReturn(List.of());

    // When
    Map<StudentProgress, List<SkillLevelProgress>> result =
        studentProgressService.getSkillsOverview(student);

    // Then
    assertTrue(result.isEmpty(), "StudentProgress should be empty");
    verify(studentProgressRepository).findAllByStudent(eq(student), any(SortCriteria.class));
  }

  @Test
  void shouldReturnSkillsViewWithCustomSortCriteria() {
    // Given
    SortCriteria customSort = new SortCriteria(ESortField.DATE, ESortOrder.DESC);
    StudentProgress progress =
        StudentProgressFixture.create().withUser(student.getUser()).toModel();

    when(studentProgressRepository.findAllByStudent(student, customSort))
        .thenReturn(List.of(progress));

    // When
    List<StudentProgress> result = studentProgressService.getSkillsView(student, customSort);

    // Then
    assertEquals(1, result.size());
    assertEquals(progress, result.getFirst());
    verify(studentProgressRepository).findAllByStudent(student, customSort);
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

    when(studentProgressRepository.findAllByStudent(eq(student), any(SortCriteria.class)))
        .thenReturn(List.of(progress1, progress2));

    // When
    List<StudentProgress> result = studentProgressService.getSkillsView(student, null);

    // Then
    assertEquals(2, result.size(), "Should contain 2 StudentProgress");
    assertEquals(
        8,
        result.stream()
            .flatMap(studentProgress -> studentProgress.getAllSkillLevels().stream())
            .toList()
            .size(),
        "Should contain 2 StudentProgress");
    verify(studentProgressRepository).findAllByStudent(eq(student), any(SortCriteria.class));
  }
}
