package fr.avenirsesr.portfolio.student.progress.imported.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.program.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.SkillFixture;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.SkillLevelFixture;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.SkillLevelProgressFixture;
import fr.avenirsesr.portfolio.student.progress.imported.application.adapter.dto.SkillDTO;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.StudentProgress;
import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.fixture.StudentProgressFixture;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class SkillMapperTest {

  @Test
  void shouldMapSkillLevelProgressToDTO() {
    BddLogger.given("a skill mapper");
    var student = StudentFixture.create().toModel();
    var javaSkill = SkillFixture.create().toModel();

    var javaSkillLevel1 = SkillLevelFixture.create().withSkill(javaSkill).toModel();
    var javaSkillLevel2 = SkillLevelFixture.create().withSkill(javaSkill).toModel();

    var progress1 =
        SkillLevelProgressFixture.create(student, javaSkillLevel1)
            .withStatus(ESkillLevelStatus.VALIDATED)
            .withEndDate(LocalDate.now().minusMonths(2))
            .toModel();
    var progress2 =
        SkillLevelProgressFixture.create(student, javaSkillLevel2)
            .withStatus(ESkillLevelStatus.TO_BE_EVALUATED)
            .withEndDate(LocalDate.now().plusMonths(1))
            .toModel();

    StudentProgress studentProgress =
        StudentProgressFixture.create()
            .withStudent(student)
            .withSkillLevels(List.of(progress1, progress2))
            .toModel();

    try (MockedStatic<SkillLevelViewMapper> skillLevelViewMapperMock =
        mockStatic(SkillLevelViewMapper.class)) {

      skillLevelViewMapperMock
          .when(() -> SkillLevelViewMapper.fromDomainToDto(progress2))
          .thenReturn(null);
      skillLevelViewMapperMock
          .when(() -> SkillLevelViewMapper.fromDomainToDto(progress1))
          .thenReturn(null);

      BddLogger.when("mapping a domain SkillLevelProgress to SkillDTO");
      SkillDTO dto = SkillMapper.fromDomainToDto(progress2, studentProgress);

      BddLogger.then("it should return a correct SkillDTO");
      assertNotNull(dto);
      assertEquals(javaSkill.getId(), dto.id());
      assertEquals(javaSkill.getName(), dto.name());
      assertEquals(2, dto.levelCount()); // Il y a 2 SkillLevelProgress pour ce skill

      skillLevelViewMapperMock.verify(() -> SkillLevelViewMapper.fromDomainToDto(progress2));
    }
  }

  @Test
  void shouldHandleNoLastAchievedSkillLevel() {
    BddLogger.given("a skill mapper");
    var student = StudentFixture.create().toModel();
    var pythonSkill = SkillFixture.create().toModel();

    var pythonSkillLevel = SkillLevelFixture.create().withSkill(pythonSkill).toModel();
    var pythonProgress =
        SkillLevelProgressFixture.create(student, pythonSkillLevel)
            .withStatus(ESkillLevelStatus.NOT_STARTED)
            .toModel();

    StudentProgress studentProgress =
        StudentProgressFixture.create()
            .withStudent(student)
            .withSkillLevels(List.of(pythonProgress))
            .toModel();

    BddLogger.when(
        "mapping a domain SkillLevelProgress without last achieved skill level to SkillDTO");
    SkillDTO dto = SkillMapper.fromDomainToDto(pythonProgress, studentProgress);

    BddLogger.then("it should handle no last achieved skill level");
    assertNotNull(dto);
    assertEquals(pythonSkill.getId(), dto.id());
    assertNull(dto.achievedSkillLevels(), "Last achieved skill level should be null");
  }

  @Test
  void shouldSetIsProgramFinishedBasedOnEndDate() {
    BddLogger.given("a skill mapper");
    var student = StudentFixture.create().toModel();
    var skill = SkillFixture.create().toModel();
    var skillLevel = SkillLevelFixture.create().withSkill(skill).toModel();
    var progress =
        SkillLevelProgressFixture.create(student, skillLevel)
            .withStatus(ESkillLevelStatus.VALIDATED)
            .toModel();

    StudentProgress finishedProgress =
        StudentProgressFixture.create()
            .withStudent(student)
            .withSkillLevels(List.of(progress))
            .withStartDate(LocalDate.now().minusMonths(2), Period.ofMonths(1))
            .toModel();
    StudentProgress ongoingProgress =
        StudentProgressFixture.create()
            .withStudent(student)
            .withSkillLevels(List.of(progress))
            .withStartDate(LocalDate.now().minusMonths(2), Period.ofMonths(3))
            .toModel();

    BddLogger.when("mapping a domain SkillLevelProgress with end dates to SkillDTO");
    SkillDTO finishedDto = SkillMapper.fromDomainToDto(progress, finishedProgress);
    SkillDTO ongoingDto = SkillMapper.fromDomainToDto(progress, ongoingProgress);

    BddLogger.then("it should set IsProgramFinished based on end date");
    assertTrue(finishedDto.isProgramFinished(), "Program should be marked as finished");
    assertFalse(ongoingDto.isProgramFinished(), "Program should not be marked as finished");
  }
}
