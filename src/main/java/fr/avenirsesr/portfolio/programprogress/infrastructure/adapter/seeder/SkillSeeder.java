package fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.programprogress.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.model.ProgramEntity;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.model.SkillEntity;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.model.SkillLevelEntity;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.repository.SkillDatabaseRepository;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.seeder.fake.FakeSkill;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.seeder.fake.FakeSkillLevel;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake.FakerProvider;
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
  private static final int NB_SKILL_BY_PROGRAM = 6;
  private static final int NB_SKILL_LEVEL_BY_SKILL = 3;

  private final SkillDatabaseRepository skillRepository;

  private Set<SkillLevelEntity> createFakeSkillLevelsOfOneSkill() {
    var skillLevels = new HashSet<SkillLevelEntity>();
    for (int j = 0; j < NB_SKILL_LEVEL_BY_SKILL; j++) {
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
    log.info("Seeding skills...");

    List<FakeSkill> fakeSkills = new ArrayList<>();

    for (int i = 0; i < programEntities.size() * NB_SKILL_BY_PROGRAM; i++) {
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
        NB_SKILL_LEVEL_BY_SKILL,
        skillEntities.size(),
        skillLevelEntities.size());

    return skillEntities;
  }
}
