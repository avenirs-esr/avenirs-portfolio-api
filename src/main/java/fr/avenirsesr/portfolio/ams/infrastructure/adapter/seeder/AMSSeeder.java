package fr.avenirsesr.portfolio.ams.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.ams.domain.model.enums.EAmsStatus;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.AMSEntity;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.CohortEntity;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.repository.AMSDatabaseRepository;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.repository.CohortDatabaseRepository;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.seeder.domain.port.output.SharedDataGenerator;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.DataGeneratorProvider;
import fr.avenirsesr.portfolio.common.validation.infrastructure.adapter.utils.ValidationUtils;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederConfig;
import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.model.SkillLevelProgressEntity;
import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.repository.SkillLevelProgressDatabaseRepository;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.repository.TraceDatabaseRepository;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@Getter
@Setter
public class AMSSeeder {
  private static final DataGeneratorProvider<SharedDataGenerator> dataGenerator =
      new DataGeneratorProvider<SharedDataGenerator>()
          .init(AMSSeeder.class, SharedDataGenerator.class);

  private final AMSDatabaseRepository amsRepository;
  private final CohortDatabaseRepository cohortRepository;
  private final SkillLevelProgressDatabaseRepository skillLevelProgressRepository;
  private final TraceDatabaseRepository traceRepository;

  private Set<CohortEntity> getRandomCohorts(List<CohortEntity> savedCohorts) {
    int cohortCount =
        dataGenerator
            .with("cohortCount")
            .number(SeederConfig.NB_COHORTS_MIN_PER_AMS, SeederConfig.NB_COHORTS_MAX_PER_AMS + 1);

    List<CohortEntity> cohorts = new ArrayList<>(savedCohorts);

    Collections.shuffle(cohorts);

    return new HashSet<>(cohorts.subList(0, cohortCount));
  }

  private List<TraceEntity> getRandomTraces(List<TraceEntity> savedTraces) {
    int tracesCount =
        dataGenerator
            .with("traceCount")
            .number(SeederConfig.NB_TRACES_MIN_PER_AMS, SeederConfig.NB_TRACES_MAX_PER_AMS + 1);

    List<TraceEntity> traceList = new ArrayList<>(savedTraces);

    Collections.shuffle(traceList);

    return new ArrayList<>(traceList.subList(0, tracesCount));
  }

  private List<SkillLevelProgressEntity> getRandomSkillLevels(
      List<SkillLevelProgressEntity> savedSkillLevels) {
    int skillLevelCount =
        dataGenerator
            .with("skillLevelCount")
            .number(
                SeederConfig.NB_SKILL_LEVEL_MIN_PER_AMS, SeederConfig.NB_SKILL_LEVEL_MAX_PER_AMS);

    List<SkillLevelProgressEntity> skillLevelList = new ArrayList<>(savedSkillLevels);

    Collections.shuffle(skillLevelList);

    return new ArrayList<>(skillLevelList.subList(0, skillLevelCount));
  }

  private StudentEntity getRandomStudent(List<StudentEntity> savedStudents) {
    int userIndex = dataGenerator.with("userIdx").number(0, savedStudents.size() - 1);
    return savedStudents.get(userIndex);
  }

  private EAmsStatus getRandomStatus() {
    EAmsStatus[] statuses = EAmsStatus.values();
    int statusIndex = dataGenerator.with("statusIdx").number(0, statuses.length - 1);
    return statuses[statusIndex];
  }

  @Transactional
  public List<AMSEntity> seed(
      List<StudentEntity> savedStudents,
      List<SkillLevelProgressEntity> savedSkillLevels,
      List<TraceEntity> savedTraces,
      List<CohortEntity> savedCohorts) {
    ValidationUtils.requireNonEmpty(savedStudents, "students cannot be empty");
    ValidationUtils.requireNonEmpty(savedSkillLevels, "skillLevels cannot be empty");
    ValidationUtils.requireNonEmpty(savedTraces, "traces cannot be empty");
    ValidationUtils.requireNonEmpty(savedCohorts, "cohorts cannot be empty");

    log.info("Seeding AMS...");

    List<AMSEntity> amsList = new ArrayList<>();
    List<CohortEntity> cohortList = new ArrayList<>();
    List<SkillLevelProgressEntity> skillLevelProgressList = new ArrayList<>();
    List<TraceEntity> traceList = new ArrayList<>();

    for (int i = 0; i < SeederConfig.AMS_NB; i++) {
      AMSEntity ams =
          FakeAMS.of(getRandomStudent(savedStudents))
              .withStatus(getRandomStatus())
              .addTranslation(ELanguage.ENGLISH)
              .addTranslation(ELanguage.SPANISH)
              .toEntity();

      var cohorts = getRandomCohorts(savedCohorts);
      cohortList.addAll(cohorts);
      cohortList.forEach(
          cohort ->
              cohort.setAmsEntities(
                  Stream.concat(cohort.getAmsEntities().stream(), Stream.of(ams))
                      .collect(Collectors.toSet())));

      var skillLevels = getRandomSkillLevels(savedSkillLevels);
      skillLevels.forEach(
          level ->
              level.setAmses(Stream.concat(level.getAmses().stream(), Stream.of(ams)).toList()));
      skillLevelProgressList.addAll(skillLevels);

      var traces = getRandomTraces(savedTraces);
      traces.forEach(
          trace ->
              trace.setAmses(Stream.concat(trace.getAmses().stream(), Stream.of(ams)).toList()));
      traceList.addAll(traces);

      amsList.add(ams);
    }

    amsRepository.saveAllEntities(amsList);
    cohortRepository.saveAllEntities(cohortList);
    skillLevelProgressRepository.saveAllEntities(skillLevelProgressList);
    traceRepository.saveAllEntities(traceList);
    log.info("✔ {} ams created", amsList.size());

    return amsList;
  }
}
