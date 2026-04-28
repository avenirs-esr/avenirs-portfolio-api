package fr.avenirsesr.portfolio.student.progress.imported.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.SkillLevelProgressFixture;
import fr.avenirsesr.portfolio.student.progress.imported.application.adapter.dto.SkillOverviewDTO;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SkillOverviewMapperTest {

  @Spy
  private SkillLevelProgressOverviewMapper skillLevelProgressOverviewMapper =
      Mappers.getMapper(SkillLevelProgressOverviewMapper.class);

  @InjectMocks private SkillOverviewMapperImpl mapper;

  @Test
  void shouldMapSkillLevelProgressToSkillOverviewDTO() {
    BddLogger.given("a skill level progress");
    var student = StudentFixture.create().toModel();
    SkillLevelProgress progress = SkillLevelProgressFixture.create(student).toModel();

    BddLogger.when("mapping to SkillOverviewDTO via default method");
    SkillOverviewDTO dto = mapper.fromDomainToDto(progress);

    BddLogger.then("it should map skill id, name and current skill level");
    assertNotNull(dto);
    assertEquals(progress.getSkillLevel().getSkill().getId(), dto.id());
    assertEquals(progress.getSkillLevel().getSkill().getName(), dto.name());
    assertNotNull(dto.currentSkillLevel());
    assertEquals(progress.getSkillLevel().getId(), dto.currentSkillLevel().id());
  }
}
