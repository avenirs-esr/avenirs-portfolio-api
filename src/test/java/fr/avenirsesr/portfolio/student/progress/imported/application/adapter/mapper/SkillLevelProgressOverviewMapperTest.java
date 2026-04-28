package fr.avenirsesr.portfolio.student.progress.imported.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.SkillLevelProgressFixture;
import fr.avenirsesr.portfolio.student.progress.imported.application.adapter.dto.SkillLevelProgressOverviewDTO;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class SkillLevelProgressOverviewMapperTest {

  private final SkillLevelProgressOverviewMapper mapper =
      Mappers.getMapper(SkillLevelProgressOverviewMapper.class);

  @Test
  void shouldMapSkillLevelProgressToOverviewDTO() {
    BddLogger.given("a skill level progress");
    var student = StudentFixture.create().toModel();
    SkillLevelProgress progress = SkillLevelProgressFixture.create(student).toModel();

    BddLogger.when("mapping to SkillLevelProgressOverviewDTO");
    SkillLevelProgressOverviewDTO dto = mapper.fromDomainToDto(progress);

    BddLogger.then("it should map skillLevel.id and skillLevel.name correctly");
    assertNotNull(dto);
    assertEquals(progress.getSkillLevel().getId(), dto.id());
    assertEquals(progress.getSkillLevel().getName(), dto.name());
    assertEquals(progress.getStatus(), dto.status());
  }
}
