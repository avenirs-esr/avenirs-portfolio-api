package fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.api.domain.model.*;
import fr.avenirsesr.portfolio.api.domain.model.enums.EAmsStatus;
import fr.avenirsesr.portfolio.api.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper.AMSMapper;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.AMSEntity;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.AMSTranslationEntity;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.repository.AMSDatabaseRepository;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder.fake.FakeAMS;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder.fake.FakerProvider;
import java.util.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Getter
@Setter
public class AMSSeeder {

  private static final FakerProvider faker = new FakerProvider();
  private static final int DEF_NB_AMS = 20;
  private static final int NB_COHORTS_MIN_PER_AMS = 0;
  private static final int NB_COHORTS_MAX_PER_AMS = 8;

  private static final int NB_SKILL_LEVEL_MIN_PER_AMS = 1;
  private static final int NB_SKILL_LEVEL_MAX_PER_AMS = 4;

  private static final int NB_TRACES_MIN_PER_AMS = 0;
  private static final int NB_TRACES_MAX_PER_AMS = 8;

  private int nbAms = DEF_NB_AMS;
  private List<Cohort> cohorts = List.of();
  private List<SkillLevel> skillLevels = List.of();
  private List<Trace> traces = List.of();
  // TODO: to remove when model is updated.
  private List<User> users = List.of();

  // TODO: abstraction when translations are fixed.
  private final AMSDatabaseRepository amsRepository;

  public AMSSeeder withCohorts(List<Cohort> cohorts) {
    this.cohorts = cohorts;
    return this;
  }

  public AMSSeeder withSkillLevels(List<SkillLevel> skillLevels) {
    this.skillLevels = skillLevels;
    return this;
  }

  public AMSSeeder withTraces(List<Trace> traces) {
    this.traces = traces;
    return this;
  }

  public AMSSeeder withUsers(List<User> users) {
    this.users = users;
    return this;
  }

  private void checkIfInitialized() throws IllegalStateException {
    if (cohorts.isEmpty() || skillLevels.isEmpty() || traces.isEmpty() || users.isEmpty()) {
      log.error(
          "AMSSeeder is not initialized: withCohorts, withSkillLevels, withTraces and withUsers must be called before seeding");
      throw new IllegalStateException("AMSSeeder is not initialized");
    }
  }

  private Set<Cohort> getRandomCohorts() {
    int cohortCount =
        faker.call().number().numberBetween(NB_COHORTS_MIN_PER_AMS, NB_COHORTS_MAX_PER_AMS + 1);

    List<Cohort> cohortList = new ArrayList<>(cohorts);

    Collections.shuffle(cohortList);

    return new HashSet<>(cohortList.subList(0, cohortCount));
  }

  private List<Trace> getRandomTraces() {
    int tracesCount =
        faker.call().number().numberBetween(NB_TRACES_MIN_PER_AMS, NB_TRACES_MAX_PER_AMS + 1);

    List<Trace> traceList = new ArrayList<>(traces);

    Collections.shuffle(traceList);

    return new ArrayList<>(traceList.subList(0, tracesCount));
  }

  private List<SkillLevel> getRandomSkillLevels() {
    int skillLevelCount =
        faker
            .call()
            .number()
            .numberBetween(NB_SKILL_LEVEL_MIN_PER_AMS, NB_SKILL_LEVEL_MAX_PER_AMS + 1);

    List<SkillLevel> skillLevelList = new ArrayList<>(skillLevels);

    Collections.shuffle(skillLevelList);

    return new ArrayList<>(skillLevelList.subList(0, skillLevelCount));
  }

  private User getRandomUser() {
    int userIndex = faker.call().number().numberBetween(0, users.size());
    return users.get(userIndex);
  }

  private EAmsStatus getRandomStatus() {
    EAmsStatus[] statuses = EAmsStatus.values();
    int statusIndex = faker.call().number().numberBetween(0, statuses.length);
    return statuses[statusIndex];
  }

  private Set<AMSTranslationEntity> generateTranslations(AMS ams) {
    AMSEntity amsEntity = AMSMapper.fromDomain(ams);
    return Set.of(
        new AMSTranslationEntity(UUID.randomUUID(), ELanguage.FRENCH, ams.getTitle(), amsEntity),
        new AMSTranslationEntity(UUID.randomUUID(), ELanguage.ENGLISH, ams.getTitle(), amsEntity),
        new AMSTranslationEntity(UUID.randomUUID(), ELanguage.SPANISH, ams.getTitle(), amsEntity));
  }

  public void seed() {
    log.info("Seeding AMS...");
    checkIfInitialized();

    List<AMSEntity> amsList = new ArrayList<>();

    for (int i = 0; i < nbAms; i++) {
      Set<Cohort> amsCohorts = getRandomCohorts();
      List<SkillLevel> amsSkillLevels = getRandomSkillLevels();
      AMS ams = FakeAMS.of(getRandomUser()).toModel();
      Set<AMSTranslationEntity> amsTranslations = generateTranslations(ams);
      List<Trace> amsTraces = getRandomTraces();
      EAmsStatus status = getRandomStatus();
      ams.setCohorts(amsCohorts);
      ams.setSkillLevels(amsSkillLevels);
      ams.setTraces(amsTraces);
      ams.setStatus(status);

      AMSEntity amsEntity = AMSMapper.fromDomain(ams);
      amsEntity.setTranslations(amsTranslations);
      amsList.add(amsEntity);
    }

    amsRepository.saveAllEntities(amsList);
    log.info("✓ {} ams created", amsList.size());
  }
}
