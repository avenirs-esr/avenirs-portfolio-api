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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

  private Set<SkillLevelEntity> getRandomSkills(List<SkillLevelEntity> savedSkillLevel) {
    var savedSkills = savedSkillLevel.stream().map(SkillLevelEntity::getSkill).distinct().toList();
    List<SkillEntity> skills = new ArrayList<>(savedSkills);
    Collections.shuffle(skills);

    var selectedSkills = new HashSet<>(skills.subList(0, SeederConfig.SKILL_BY_PROGRAM));

    return savedSkillLevel.stream()
        .filter(skillLevelEntity -> selectedSkills.contains(skillLevelEntity.getSkill()))
        .collect(Collectors.toSet());
  }

  private List<TrainingPathEntity> generateFakeTrainingPathEntities(
      List<ProgramEntity> savedPrograms, List<SkillLevelEntity> savedSkillLevels) {
    List<TrainingPathEntity> trainingPathEntities = new ArrayList<>();

    for (ProgramEntity programEntity : savedPrograms) {
      for (int i = 0; i < SeederConfig.TRAINING_PATH_BY_PROGRAM; i++) {
        trainingPathEntities.add(
            createFakeTrainingPath(programEntity, getRandomSkills(savedSkillLevels)).toEntity());
      }
    }

    return trainingPathEntities;
  }

  public List<TrainingPathEntity> seed(
      List<ProgramEntity> savedPrograms, List<SkillLevelEntity> savedSkillLevels) {
    ValidationUtils.requireNonEmpty(savedPrograms, "programs cannot be empty");
    ValidationUtils.requireNonEmpty(savedSkillLevels, "skills cannot be empty");
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
