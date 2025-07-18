package fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.ProgramEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillLevelEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.repository.SkillDatabaseRepository;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.repository.SkillLevelDatabaseRepository;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.fake.FakeSkill;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.fake.FakeSkillLevel;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederConfig;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.utils.ValidationUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class SkillSeeder {
  private final SkillDatabaseRepository skillRepository;
  private final SkillLevelDatabaseRepository skillLevelRepository;

  private List<SkillLevelEntity> createFakeSkillLevelsOfOneSkill() {
    var skillLevels = new ArrayList<SkillLevelEntity>();
    for (int j = 0; j < SeederConfig.SKILL_LEVEL_BY_SKILL; j++) {
      skillLevels.add(FakeSkillLevel.create().addTranslation(ELanguage.ENGLISH).toEntity());
    }

    return skillLevels;
  }

  private FakeSkill createFakeSkill(List<SkillLevelEntity> skillLevels) {
    return FakeSkill.of(skillLevels).addTranslation(ELanguage.ENGLISH);
  }

  public List<SkillLevelEntity> seed(List<ProgramEntity> programEntities) {
    ValidationUtils.requireNonEmpty(programEntities, "programs cannot be empty");

    log.info("Seeding skills...");

    List<FakeSkill> fakeSkills = new ArrayList<>();
    List<SkillLevelEntity> skillLevelEntities = new ArrayList<>();

    for (int i = 0; i < programEntities.size() * SeederConfig.SKILL_BY_PROGRAM; i++) {
      var skillLevelsOfSkill = createFakeSkillLevelsOfOneSkill();
      skillLevelEntities.addAll(skillLevelsOfSkill);
      fakeSkills.add(createFakeSkill(skillLevelsOfSkill));
    }

    var skillEntities = fakeSkills.stream().map(FakeSkill::toEntity).toList();
    skillRepository.saveAllEntities(skillEntities);
    skillLevelRepository.saveAllEntities(skillLevelEntities);

    log.info(
        "✔ {} skill created with : {} skill levels by skill \n"
            + "-> Total {} skills and {} skill levels",
        skillEntities.size(),
        SeederConfig.SKILL_LEVEL_BY_SKILL,
        skillEntities.size(),
        skillLevelEntities.size());

    return skillLevelEntities;
  }
}
