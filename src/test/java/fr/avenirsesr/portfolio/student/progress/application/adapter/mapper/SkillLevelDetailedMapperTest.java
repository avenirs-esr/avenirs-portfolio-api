package fr.avenirsesr.portfolio.student.progress.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.program.domain.model.Skill;
import fr.avenirsesr.portfolio.program.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.SkillFixture;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.SkillLevelFixture;
import fr.avenirsesr.portfolio.student.progress.application.adapter.dto.SkillLevelDetailedDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class SkillLevelDetailedMapperTest {

  @Nested
  class GivenSkillLevelDetailedMapper {
    @BeforeEach
    void setupGiven() {
      BddLogger.given("a skill level detailed mapper");
    }

    @Nested
    class WhenMappingADomainSkillLevelDetailedToSkillLevelDetailedDTO {
      private Skill skill;
      private SkillLevel skillLevel;

      @BeforeEach
      void setupWhen() {
        BddLogger.when("mapping a domain SkillLevel to SkillDetailedDTO");
      }

      @Nested
      class AndACorrectSkillDetailedIsPassed {

        @BeforeEach
        void setupAnd() {
          BddLogger.and("a correct SkillLevel is passed");

          skill = SkillFixture.create().toModel();
          skillLevel = SkillLevelFixture.create().withSkill(skill).toModel();
        }

        @Test
        void thenItShouldReturnACorrectSkillLevelDetailedDTO() {
          BddLogger.then("it should return a correct SkillLevelDetailedDTO");

          SkillLevelDetailedDTO dto = SkillLevelDetailedMapper.fromDomainToDto(skillLevel);

          assertNotNull(dto);
          assertEquals(skillLevel.getId(), dto.id());
          assertEquals(skillLevel.getName(), dto.name());
        }
      }
    }
  }
}
