package fr.avenirsesr.portfolio.student.progress.imported.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.program.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.SkillFixture;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.SkillLevelFixture;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.SkillLevelProgressFixture;
import fr.avenirsesr.portfolio.student.progress.imported.application.adapter.dto.StudentProgressViewDTO;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.StudentProgress;
import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.fixture.StudentProgressFixture;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StudentProgressViewMapperTest {

  @Mock private SkillMapper skillMapper;

  @InjectMocks private StudentProgressViewMapper mapper;

  @Test
  void shouldMapStudentProgressToDTO() {
    BddLogger.given("a student progress view mapper");
    var student = StudentFixture.create().toModel();
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

    var skillLevelProgresses =
        List.of(javaProgress_1, javaProgress_2, javaProgress_3, pythonProgress_1, pythonProgress_2);

    StudentProgress studentProgress =
        StudentProgressFixture.create()
            .withStudent(student)
            .withSkillLevels(skillLevelProgresses)
            .toModel();

    BddLogger.when("mapping a domain StudentProgress to StudentProgressViewDTO");
    StudentProgressViewDTO dto =
        mapper.fromDomainToDto(studentProgress, List.of(javaProgress_3, pythonProgress_2));

    BddLogger.then("it should return a correct StudentProgressViewDTO");
    assertNotNull(dto);
    assertEquals(studentProgress.getId(), dto.id());
    assertEquals(studentProgress.getTrainingPath().getProgram().getName(), dto.name());
    assertEquals(2, dto.skills().size());

    verify(skillMapper).fromDomainToDto(eq(javaProgress_3), eq(studentProgress));
    verify(skillMapper).fromDomainToDto(eq(pythonProgress_2), eq(studentProgress));
  }

  @Test
  void shouldHandleEmptyCurrentSkillLevels() {
    BddLogger.given("a student progress view mapper");
    StudentProgress studentProgress =
        StudentProgressFixture.create().withSkillLevels(List.of()).toModel();

    BddLogger.when(
        "mapping a domain StudentProgress without current skill levels to StudentProgressViewDTO");
    StudentProgressViewDTO dto = mapper.fromDomainToDto(studentProgress, List.of());

    BddLogger.then("it should handle empty current skill levels");
    assertNotNull(dto);
    assertTrue(dto.skills().isEmpty(), "DTO should have empty skills list");
  }
}
