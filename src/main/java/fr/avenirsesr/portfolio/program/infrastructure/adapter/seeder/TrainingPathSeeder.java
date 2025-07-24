package fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.ProgramEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillLevelEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.TrainingPathEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.repository.SkillLevelDatabaseRepository;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.repository.TrainingPathDatabaseRepository;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.fake.FakeTrainingPath;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederConfig;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.utils.ValidationUtils;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrainingPathSeeder {
  private final TrainingPathDatabaseRepository trainingPathRepository;
  private final SkillLevelDatabaseRepository skillLevelRepository;

  private FakeTrainingPath createFakeTrainingPath(
      ProgramEntity program, Set<SkillLevelEntity> skillLevels) {
    return FakeTrainingPath.of(program, skillLevels);
  }

  private List<TrainingPathEntity> generateFakeTrainingPathEntities(
      List<ProgramEntity> savedPrograms, List<SkillLevelEntity> savedSkillLevels) {
    List<TrainingPathEntity> trainingPathEntities = new ArrayList<>();
    Map<ProgramEntity, Set<SkillLevelEntity>> skillLevelByProgram = new HashMap<>();
    var savedSkills =
        savedSkillLevels.stream()
            .filter(skillLevelEntity -> skillLevelEntity.getTrainingPath() == null)
            .map(SkillLevelEntity::getSkill)
            .distinct()
            .toList();

    for (int i = 0; i < savedPrograms.size(); i++) {
      List<SkillEntity> programSkills =
          savedSkills.subList(
              i * SeederConfig.SKILL_BY_PROGRAM, (i + 1) * SeederConfig.SKILL_BY_PROGRAM);

      skillLevelByProgram.put(
          savedPrograms.get(i),
          savedSkillLevels.stream()
              .filter(skillLevelEntity -> programSkills.contains(skillLevelEntity.getSkill()))
              .collect(Collectors.toSet()));
    }

    for (ProgramEntity programEntity : savedPrograms) {
      for (int i = 0; i < SeederConfig.TRAINING_PATH_BY_PROGRAM; i++) {
        trainingPathEntities.add(
            createFakeTrainingPath(programEntity, skillLevelByProgram.get(programEntity))
                .toEntity());
      }
    }

    return trainingPathEntities;
  }

  public List<TrainingPathEntity> seed(
      List<ProgramEntity> savedPrograms, List<SkillLevelEntity> savedSkillLevels) {
    ValidationUtils.requireNonEmpty(savedPrograms, "programs cannot be empty");
    ValidationUtils.requireNonEmpty(savedSkillLevels, "skills cannot be empty");
    if ((long) savedPrograms.size() * SeederConfig.SKILL_BY_PROGRAM
        != savedSkillLevels.stream().map(SkillLevelEntity::getSkill).distinct().count()) {
      throw new IllegalArgumentException(
          "should have saved %s skill by program so %s skills in total"
              .formatted(
                  SeederConfig.SKILL_BY_PROGRAM,
                  SeederConfig.SKILL_BY_PROGRAM * savedPrograms.size()));
    }
    log.info("Seeding training path...");

    List<TrainingPathEntity> trainingPathEntities =
        generateFakeTrainingPathEntities(savedPrograms, savedSkillLevels);

    trainingPathRepository.saveAllEntities(trainingPathEntities);
    trainingPathEntities.forEach(
        trainingPathEntity ->
            trainingPathEntity
                .getSkillLevels()
                .forEach(skillLevelEntity -> skillLevelEntity.setTrainingPath(trainingPathEntity)));
    skillLevelRepository.saveAllEntities(savedSkillLevels);

    log.info("✔ {} trainingPathes created", trainingPathEntities.size());
    return trainingPathEntities;
  }
}
