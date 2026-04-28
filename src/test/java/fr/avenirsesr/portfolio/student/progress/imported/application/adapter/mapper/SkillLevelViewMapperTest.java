package fr.avenirsesr.portfolio.student.progress.imported.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.SkillLevelFixture;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.SkillLevelProgressFixture;
import fr.avenirsesr.portfolio.student.progress.imported.application.adapter.dto.SkillLevelViewDTO;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class SkillLevelViewMapperTest {

  private final SkillLevelViewMapper mapper = Mappers.getMapper(SkillLevelViewMapper.class);

  @Test
  void shouldMapSkillLevelProgressToViewDTO() {
    BddLogger.given("a skill level progress");
    var student = StudentFixture.create().toModel();
    var skillLevel = SkillLevelFixture.create().withDescription("Beginner level").toModel();
    SkillLevelProgress progress = SkillLevelProgressFixture.create(student, skillLevel).toModel();

    BddLogger.when("mapping to SkillLevelViewDTO");
    SkillLevelViewDTO dto = mapper.fromDomainToDto(progress);

    BddLogger.then("it should map skillLevel.id, name, and description to shortDescription");
    assertNotNull(dto);
    assertEquals(skillLevel.getId(), dto.id());
    assertEquals(skillLevel.getName(), dto.name());
    assertEquals("Beginner level", dto.shortDescription());
    assertEquals(progress.getStatus(), dto.status());
  }
}
