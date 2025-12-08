package fr.avenirsesr.portfolio.student.progress.imported.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.program.domain.model.Skill;
import fr.avenirsesr.portfolio.program.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.SkillFixture;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.SkillLevelFixture;
import fr.avenirsesr.portfolio.student.progress.imported.application.adapter.dto.SkillDetailedDTO;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class SkillDetailedMapperTest {

  @Nested
  class GivenSkillDetailedMapper {
    @BeforeEach
    void setupGiven() {
      BddLogger.given("a skill detailed mapper");
    }

    @Nested
    class WhenMappingADomainSkillToSkillDetailedDTO {
      private Skill skill;
      private List<SkillLevel> skillLevels;

      @BeforeEach
      void setupWhen() {
        BddLogger.when("mapping a domain Skill to SkillDetailedDTO");
      }

      @Nested
      class AndACorrectSkillDetailedIsPassed {

        @BeforeEach
        void setupAnd() {
          BddLogger.and("a correct Skill is passed");

          skill = SkillFixture.create().toModel();
          skillLevels = new ArrayList<SkillLevel>();

          skillLevels.add(SkillLevelFixture.create().withSkill(skill).toModel());
          skillLevels.add(SkillLevelFixture.create().withSkill(skill).toModel());
        }

        @Test
        void thenItShouldReturnACorrectSkillDetailedDTO() {
          BddLogger.then("it should return a correct SkillDetailedDTO");

          SkillDetailedDTO dto = SkillDetailedMapper.fromDomainToDto(skill, skillLevels);

          assertNotNull(dto);
          assertEquals(skill.getId(), dto.id());
          assertEquals(skill.getName(), dto.name());
          assertEquals(skillLevels.size(), dto.skillLevels().size());
          assertEquals(skillLevels.get(0).getId(), dto.skillLevels().get(0).id());
          assertEquals(skillLevels.get(0).getName(), dto.skillLevels().get(0).name());
        }
      }
    }
  }
}
