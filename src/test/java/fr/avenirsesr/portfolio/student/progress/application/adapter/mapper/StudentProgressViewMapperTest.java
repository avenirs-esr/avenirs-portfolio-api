package fr.avenirsesr.portfolio.student.progress.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.program.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.SkillFixture;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.SkillLevelFixture;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.SkillLevelProgressFixture;
import fr.avenirsesr.portfolio.student.progress.application.adapter.dto.StudentProgressViewDTO;
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
    // GIVEN
    var student = UserFixture.createStudent().toModel().toStudent();
    var javaSkill = SkillFixture.create().toModel();
    var pythonSkill = SkillFixture.create().toModel();

    var javaSkillLevel_1 = SkillLevelFixture.create().withSkill(javaSkill).toModel();
    var javaSkillLevel_2 = SkillLevelFixture.create().withSkill(javaSkill).toModel();
    var javaSkillLevel_3 = SkillLevelFixture.create().withSkill(javaSkill).toModel();
    var pythonSkillLevel_1 = SkillLevelFixture.create().withSkill(pythonSkill).toModel();
    var pythonSkillLevel_2 = SkillLevelFixture.create().withSkill(pythonSkill).toModel();

    var javaProgress_1 =
        SkillLevelProgressFixture.create(student, javaSkillLevel_1)
            .withStatus(ESkillLevelStatus.TO_BE_EVALUATED)
            .withEndDate(LocalDate.now().minusMonths(2))
            .toModel();
    var javaProgress_2 =
        SkillLevelProgressFixture.create(student, javaSkillLevel_2)
            .withStatus(ESkillLevelStatus.TO_BE_EVALUATED)
            .withEndDate(LocalDate.now().minusMonths(1))
            .toModel();
    var javaProgress_3 =
        SkillLevelProgressFixture.create(student, javaSkillLevel_3)
            .withStatus(ESkillLevelStatus.TO_BE_EVALUATED)
            .withEndDate(LocalDate.now().plusMonths(1))
            .toModel();
    var pythonProgress_1 =
        SkillLevelProgressFixture.create(student, pythonSkillLevel_1)
            .withStatus(ESkillLevelStatus.VALIDATED)
            .withEndDate(LocalDate.now().minusMonths(4))
            .toModel();
    var pythonProgress_2 =
        SkillLevelProgressFixture.create(student, pythonSkillLevel_2)
            .withStatus(ESkillLevelStatus.NOT_STARTED)
            .withEndDate(LocalDate.now().plusMonths(4))
            .toModel();

    StudentProgress studentProgress =
        StudentProgressFixture.create()
            .withUser(student.getUser())
            .withSkillLevels(
                List.of(
                    javaProgress_1,
                    javaProgress_2,
                    javaProgress_3,
                    pythonProgress_1,
                    pythonProgress_2))
            .toModel();

    try (MockedStatic<SkillViewMapper> mockedSkillViewMapper = mockStatic(SkillViewMapper.class)) {

      // WHEN
      StudentProgressViewDTO dto = StudentProgressViewMapper.fromDomainToDto(studentProgress);

      // THEN
      assertNotNull(dto);
      assertEquals(studentProgress.getId(), dto.id());
      assertEquals(studentProgress.getTrainingPath().getProgram().getName(), dto.name());
      assertEquals(2, dto.skills().size());

      mockedSkillViewMapper.verify(
          () -> SkillViewMapper.fromDomainToDto(eq(javaProgress_3), studentProgress));
      mockedSkillViewMapper.verify(
          () -> SkillViewMapper.fromDomainToDto(eq(pythonProgress_1), studentProgress));
    }
  }

  @Test
  void shouldHandleEmptyCurrentSkillLevels() {
    StudentProgress studentProgress =
        StudentProgressFixture.create().withSkillLevels(List.of()).toModel();

    StudentProgressViewDTO dto = StudentProgressViewMapper.fromDomainToDto(studentProgress);

    assertNotNull(dto);
    assertTrue(dto.skills().isEmpty(), "DTO should have empty skills list");
  }
}
