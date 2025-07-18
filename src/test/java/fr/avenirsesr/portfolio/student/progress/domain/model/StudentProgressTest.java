package fr.avenirsesr.portfolio.student.progress.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.program.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.SkillFixture;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.SkillLevelFixture;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.SkillLevelProgressFixture;
import fr.avenirsesr.portfolio.student.progress.infrastructure.fixture.StudentProgressFixture;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

public class StudentProgressTest {

  @Test
  void testEmptySkillLevels() {
    // Given
    StudentProgress studentProgress =
        StudentProgressFixture.create().withSkillLevels(List.of()).toModel();

    // When
    List<SkillLevelProgress> result = studentProgress.getCurrentSkillLevels();

    // Then
    assertTrue(result.isEmpty(), "SkillLevelProgress should be empty");
  }

  @Test
  void testAllNotStarted() {
    // Given
    StudentProgress studentProgress = StudentProgressFixture.create().toModel();
    studentProgress
        .getAllSkillLevels()
        .forEach(skillLevel -> skillLevel.setStatus(ESkillLevelStatus.NOT_STARTED));

    // When
    List<SkillLevelProgress> result = studentProgress.getCurrentSkillLevels();

    // Then
    assertTrue(result.isEmpty(), "NOT_STARTED should be ignored");
  }

  @Test
  void testSelectLatestStartDateForSkill() {
    // Given
    var student = UserFixture.createStudent().toModel().toStudent();
    var skill = SkillFixture.create().toModel();
    var skillLevels =
        List.of(
            SkillLevelFixture.create().withSkill(skill).toModel(),
            SkillLevelFixture.create().withSkill(skill).toModel());
    var skillLevelProgress =
        skillLevels.stream()
            .map(
                sLvl ->
                    SkillLevelProgressFixture.create(student, sLvl)
                        .withStatus(ESkillLevelStatus.TO_BE_EVALUATED)
                        .toModel())
            .toList();

    var oldestProgress = skillLevelProgress.getFirst();
    var newestProgress = skillLevelProgress.getLast();
    oldestProgress.setStartDate(LocalDate.of(2023, 1, 1));
    newestProgress.setStartDate(LocalDate.of(2024, 1, 1));

    StudentProgress studentProgress =
        StudentProgressFixture.create()
            .withUser(student.getUser())
            .withSkillLevels(skillLevelProgress)
            .toModel();

    // When
    List<SkillLevelProgress> result = studentProgress.getCurrentSkillLevels();

    // Then
    assertEquals(1, result.size());
    assertEquals(newestProgress, result.getFirst(), "should return latest start date");
  }

  @Test
  void testMultipleSkills() {
    // Given
    var student = UserFixture.createStudent().toModel().toStudent();
    var javaSkill = SkillFixture.create().toModel();
    var pythonSkill = SkillFixture.create().toModel();
    var skillLevels =
        List.of(
            SkillLevelFixture.create().withSkill(javaSkill).toModel(),
            SkillLevelFixture.create().withSkill(javaSkill).toModel(),
            SkillLevelFixture.create().withSkill(pythonSkill).toModel(),
            SkillLevelFixture.create().withSkill(pythonSkill).toModel());
    var skillLevelProgress =
        skillLevels.stream()
            .map(
                sLvl ->
                    SkillLevelProgressFixture.create(student, sLvl)
                        .withStatus(ESkillLevelStatus.TO_BE_EVALUATED)
                        .toModel())
            .toList();

    var javaNewest = skillLevelProgress.getFirst();
    var pythonNewest = skillLevelProgress.get(2);
    skillLevelProgress.get(1).setStartDate(LocalDate.of(2023, 1, 1));
    javaNewest.setStartDate(LocalDate.of(2024, 1, 1));
    pythonNewest.setStartDate(LocalDate.of(2024, 1, 1));
    skillLevelProgress.get(3).setStartDate(LocalDate.of(2023, 1, 1));

    // When
    StudentProgress studentProgress =
        StudentProgressFixture.create()
            .withUser(student.getUser())
            .withSkillLevels(skillLevelProgress)
            .toModel();
    List<SkillLevelProgress> result = studentProgress.getCurrentSkillLevels();

    // Then
    assertEquals(2, result.size());
    assertTrue(result.contains(javaNewest));
    assertTrue(result.contains(pythonNewest));
  }

  @Test
  void testMixedStatuses() {
    // Given
    var student = UserFixture.createStudent().toModel().toStudent();
    var javaSkill = SkillFixture.create().toModel();
    var skillLevels =
        List.of(
            SkillLevelFixture.create().withSkill(javaSkill).toModel(),
            SkillLevelFixture.create().withSkill(javaSkill).toModel());
    var skillLevelProgress =
        skillLevels.stream()
            .map(
                sLvl ->
                    SkillLevelProgressFixture.create(student, sLvl)
                        .withStatus(ESkillLevelStatus.TO_BE_EVALUATED)
                        .toModel())
            .toList();

    skillLevelProgress.get(0).setStartDate(LocalDate.of(2024, 1, 1));
    skillLevelProgress.get(0).setStatus(ESkillLevelStatus.NOT_STARTED);
    skillLevelProgress.get(1).setStartDate(LocalDate.of(2023, 1, 1));
    StudentProgress studentProgress =
        StudentProgressFixture.create()
            .withUser(student.getUser())
            .withSkillLevels(skillLevelProgress)
            .toModel();

    // When
    List<SkillLevelProgress> result = studentProgress.getCurrentSkillLevels();

    assertEquals(1, result.size());
    assertEquals(skillLevelProgress.get(1), result.getFirst(), "NOT_STARTED should be ignored");
  }
}
