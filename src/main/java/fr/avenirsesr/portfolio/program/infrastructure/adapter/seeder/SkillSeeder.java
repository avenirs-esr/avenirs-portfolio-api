package fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.program.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.ProgramEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillLevelEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.repository.SkillDatabaseRepository;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.fake.FakeSkill;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.fake.FakeSkillLevel;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederConfig;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake.FakerProvider;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.utils.ValidationUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class SkillSeeder {
  private static final Faker faker = new FakerProvider().call();

  private final SkillDatabaseRepository skillRepository;

  private Set<SkillLevelEntity> createFakeSkillLevelsOfOneSkill() {
    var skillLevels = new HashSet<SkillLevelEntity>();
    for (int j = 0; j < SeederConfig.SKILL_LEVEL_BY_SKILL; j++) {
      int StatusIndex = faker.random().nextInt(ESkillLevelStatus.values().length);
      skillLevels.add(
          FakeSkillLevel.create()
              .addTranslation(ELanguage.ENGLISH)
              .withStatus(ESkillLevelStatus.values()[StatusIndex])
              .toEntity());
    }

    return skillLevels;
  }

  private FakeSkill createFakeSkill(Set<SkillLevelEntity> skillLevels) {
    return FakeSkill.of(skillLevels).addTranslation(ELanguage.ENGLISH);
  }

  public List<SkillEntity> seed(List<ProgramEntity> programEntities) {
    ValidationUtils.requireNonEmpty(programEntities, "programs cannot be empty");

    log.info("Seeding skills...");

    List<FakeSkill> fakeSkills = new ArrayList<>();

    for (int i = 0; i < programEntities.size() * SeederConfig.SKILL_BY_PROGRAM; i++) {
      fakeSkills.add(createFakeSkill(createFakeSkillLevelsOfOneSkill()));
    }

    var skillEntities = fakeSkills.stream().map(FakeSkill::toEntity).toList();
    skillRepository.saveAllEntities(skillEntities);

    var skillLevelEntities =
        skillEntities.stream().flatMap(s -> s.getSkillLevels().stream()).toList();

    log.info(
        "✔ {} skill created with : {} skill levels by skill \n"
            + "-> Total {} skills and {} skill levels",
        skillEntities.size(),
        SeederConfig.SKILL_LEVEL_BY_SKILL,
        skillEntities.size(),
        skillLevelEntities.size());

    return skillEntities;
  }
}
