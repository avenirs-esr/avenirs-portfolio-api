package fr.avenirsesr.portfolio.student.progress.imported.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.SkillLevelProgressFixture;
import fr.avenirsesr.portfolio.student.progress.imported.application.adapter.dto.SkillLevelProgressOverviewDTO;
import fr.avenirsesr.portfolio.student.progress.imported.application.adapter.dto.StudentProgressOverviewDTO;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.StudentProgress;
import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.fixture.StudentProgressFixture;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StudentProgressOverviewMapperTest {

  @Mock private SkillOverviewMapper skillOverviewMapper;

  @InjectMocks private StudentProgressOverviewMapperImpl mapper;

  @Test
  void shouldMapStudentProgressToOverviewDTO() {
    BddLogger.given("a student progress with skill level progress list");
    var student = StudentFixture.create().toModel();
    StudentProgress studentProgress =
        StudentProgressFixture.create().withStudent(student).toModel();
    SkillLevelProgress skillLevel = studentProgress.getAllSkillLevels().get(0);

    SkillLevelProgressOverviewDTO overviewDTO =
        new SkillLevelProgressOverviewDTO(UUID.randomUUID(), "Level 1", skillLevel.getStatus());
    when(skillOverviewMapper.toSkillLevelProgressOverview(any())).thenReturn(overviewDTO);

    BddLogger.when("mapping via fromDomainToDto");
    StudentProgressOverviewDTO dto = mapper.fromDomainToDto(studentProgress, List.of(skillLevel));

    BddLogger.then("it should map id, program name, and skill overviews");
    assertNotNull(dto);
    assertEquals(studentProgress.getId(), dto.id());
    assertEquals(studentProgress.getTrainingPath().getProgram().getName(), dto.programTitle());
    assertNotNull(dto.skills());
    assertEquals(1, dto.skills().size());
  }

  @Test
  void shouldMapSkillLevelProgressToSkillOverviewDTO() {
    BddLogger.given("a skill level progress");
    var student = StudentFixture.create().toModel();
    SkillLevelProgress progress = SkillLevelProgressFixture.create(student).toModel();

    SkillLevelProgressOverviewDTO overviewDTO =
        new SkillLevelProgressOverviewDTO(
            progress.getSkillLevel().getId(),
            progress.getSkillLevel().getName(),
            progress.getStatus());
    when(skillOverviewMapper.toSkillLevelProgressOverview(progress)).thenReturn(overviewDTO);

    BddLogger.when("mapping via toSkillOverviewDTO");
    var dto = mapper.toSkillOverviewDTO(progress);

    BddLogger.then("it should map skillLevel.skill.id and skillLevel.skill.name");
    assertNotNull(dto);
    assertEquals(progress.getSkillLevel().getSkill().getId(), dto.id());
    assertEquals(progress.getSkillLevel().getSkill().getName(), dto.name());
    assertNotNull(dto.currentSkillLevel());
  }
}
