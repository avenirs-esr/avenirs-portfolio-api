package fr.avenirsesr.portfolio.student.progress.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.program.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.SkillFixture;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.SkillLevelFixture;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.SkillLevelProgressFixture;
import fr.avenirsesr.portfolio.student.progress.application.adapter.dto.StudentProgressViewDTO;
import fr.avenirsesr.portfolio.student.progress.domain.dto.SkillLevelProgressWithTraceCountDTO;
import fr.avenirsesr.portfolio.student.progress.domain.model.StudentProgress;
import fr.avenirsesr.portfolio.student.progress.infrastructure.fixture.StudentProgressFixture;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

public class StudentProgressViewMapperTest {

  @Test
  void shouldMapStudentProgressToDTO() {
    BddLogger.given("a student progress view mapper");
    var student = UserFixture.createStudent().toModel().toStudent();
    var javaSkill = SkillFixture.create().toModel();
    var pythonSkill = SkillFixture.create().toModel();

    var javaSkillLevel_1 = SkillLevelFixture.create().withSkill(javaSkill).toModel();
    var javaSkillLevel_2 = SkillLevelFixture.create().withSkill(javaSkill).toModel();
    var javaSkillLevel_3 = SkillLevelFixture.create().withSkill(javaSkill).toModel();
    var pythonSkillLevel_1 = SkillLevelFixture.create().withSkill(pythonSkill).toModel();
    var pythonSkillLevel_2 = SkillLevelFixture.create().withSkill(pythonSkill).toModel();

    var javaProgress_1 =
        new SkillLevelProgressWithTraceCountDTO(
            SkillLevelProgressFixture.create(student, javaSkillLevel_1)
                .withStatus(ESkillLevelStatus.TO_BE_EVALUATED)
                .withEndDate(LocalDate.now().minusMonths(2))
                .toModel(),
            2);
    var javaProgress_2 =
        new SkillLevelProgressWithTraceCountDTO(
            SkillLevelProgressFixture.create(student, javaSkillLevel_2)
                .withStatus(ESkillLevelStatus.TO_BE_EVALUATED)
                .withEndDate(LocalDate.now().minusMonths(1))
                .toModel(),
            2);
    var javaProgress_3 =
        new SkillLevelProgressWithTraceCountDTO(
            SkillLevelProgressFixture.create(student, javaSkillLevel_3)
                .withStatus(ESkillLevelStatus.TO_BE_EVALUATED)
                .withEndDate(LocalDate.now().plusMonths(1))
                .toModel(),
            2);
    var pythonProgress_1 =
        new SkillLevelProgressWithTraceCountDTO(
            SkillLevelProgressFixture.create(student, pythonSkillLevel_1)
                .withStatus(ESkillLevelStatus.VALIDATED)
                .withEndDate(LocalDate.now().minusMonths(4))
                .toModel(),
            2);
    var pythonProgress_2 =
        new SkillLevelProgressWithTraceCountDTO(
            SkillLevelProgressFixture.create(student, pythonSkillLevel_2)
                .withStatus(ESkillLevelStatus.NOT_STARTED)
                .withEndDate(LocalDate.now().plusMonths(4))
                .toModel(),
            2);

    var skillLevelProgresses =
        List.of(javaProgress_1, javaProgress_2, javaProgress_3, pythonProgress_1, pythonProgress_2);

    StudentProgress studentProgress =
        StudentProgressFixture.create()
            .withUser(student.getUser())
            .withSkillLevels(
                skillLevelProgresses.stream()
                    .map(SkillLevelProgressWithTraceCountDTO::skillLevelProgress)
                    .toList())
            .toModel();

    try (MockedStatic<SkillMapper> mockedSkillViewMapper = mockStatic(SkillMapper.class)) {

      BddLogger.when("mapping a domain StudentProgress to StudentProgressViewDTO");
      StudentProgressViewDTO dto =
          StudentProgressViewMapper.fromDomainToDto(
              studentProgress, List.of(javaProgress_3, pythonProgress_2));

      BddLogger.then("it should return a correct StudentProgressViewDTO");
      assertNotNull(dto);
      assertEquals(studentProgress.getId(), dto.id());
      assertEquals(studentProgress.getTrainingPath().getProgram().getName(), dto.name());
      assertEquals(2, dto.skills().size());

      mockedSkillViewMapper.verify(
          () -> SkillMapper.fromDomainToDto(eq(javaProgress_3), eq(studentProgress)));
      mockedSkillViewMapper.verify(
          () -> SkillMapper.fromDomainToDto(eq(pythonProgress_2), eq(studentProgress)));
    }
  }

  @Test
  void shouldHandleEmptyCurrentSkillLevels() {
    BddLogger.given("a student progress view mapper");
    StudentProgress studentProgress =
        StudentProgressFixture.create().withSkillLevels(List.of()).toModel();

    BddLogger.when(
        "mapping a domain StudentProgress without current skill levels to StudentProgressViewDTO");
    StudentProgressViewDTO dto =
        StudentProgressViewMapper.fromDomainToDto(studentProgress, List.of());

    BddLogger.then("it should handle empty current skill levels");
    assertNotNull(dto);
    assertTrue(dto.skills().isEmpty(), "DTO should have empty skills list");
  }
}
