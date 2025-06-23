package fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.api.domain.model.*;
import fr.avenirsesr.portfolio.api.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.api.domain.model.enums.EPortfolioType;
import fr.avenirsesr.portfolio.api.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.*;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper.*;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.*;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.repository.*;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder.fake.*;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SeederRunner implements CommandLineRunner {

  private static final int NB_USERS = 100;
  private static final int NB_COHORTS = 50;
  private static final int NB_AMS = 50;
  private static final int NB_TRACES = 200;

  private final UserRepository userRepository;
  private final InstitutionDatabaseRepository institutionRepository;
  private final ProgramDatabaseRepository programRepository;
  private final ProgramProgressRepository programProgressRepository;
  private final SkillDatabaseRepository skillRepository;
  private final UserSeeder userSeeder;
  private final CohortSeeder cohortSeeder;
  private final AMSSeeder amsSeeder;
  private final TraceSeeder traceSeeder;

  @Value("${seeder.enabled:false}")
  private boolean seedEnabled;

  public SeederRunner(
      UserRepository userRepository,
      ExternalUserRepository externalUserRepository,
      InstitutionDatabaseRepository institutionRepository,
      ProgramDatabaseRepository programRepository,
      ProgramProgressRepository programProgressRepository,
      SkillLevelRepository skillLevelRepository,
      SkillDatabaseRepository skillRepository,
      UserSeeder userSeeder,
      CohortSeeder cohortSeeder,
      AMSSeeder amsSeeder,
      TraceSeeder traceSeeder) {
    this.userRepository = userRepository;
    this.institutionRepository = institutionRepository;
    this.programRepository = programRepository;
    this.programProgressRepository = programProgressRepository;
    this.skillRepository = skillRepository;
    this.cohortSeeder = cohortSeeder;
    this.amsSeeder = amsSeeder;
    this.traceSeeder = traceSeeder;
    this.userSeeder = userSeeder;
    this.userSeeder.setNbUsers(NB_USERS);
    this.cohortSeeder.setNbCohorts(NB_COHORTS);
    this.amsSeeder.setNbAms(NB_AMS);
    this.traceSeeder.setNbTraces(NB_TRACES);
  }

  @Override
  public void run(String... args) {
    long userCont = userRepository.countAll();

    if (seedEnabled && userCont == 0) {

      var users = userSeeder.seed();

      Institution institutionBase = FakeInstitution.create().toModel();
      Institution institutionApc =
          FakeInstitution.create().withEnabledFiled(Set.of(EPortfolioType.APC)).toModel();
      Institution institutionLifeProject =
          FakeInstitution.create().withEnabledFiled(Set.of(EPortfolioType.LIFE_PROJECT)).toModel();
      List<Institution> institutions =
          List.of(
              institutionBase,
              FakeInstitution.create(institutionBase, ELanguage.ENGLISH).toModel(),
              FakeInstitution.create(institutionBase, ELanguage.SPANISH).toModel(),
              institutionApc,
              FakeInstitution.create(institutionApc, ELanguage.ENGLISH).toModel(),
              FakeInstitution.create(institutionApc, ELanguage.SPANISH).toModel(),
              institutionLifeProject,
              FakeInstitution.create(institutionLifeProject, ELanguage.ENGLISH).toModel(),
              FakeInstitution.create(institutionLifeProject, ELanguage.SPANISH).toModel());

      List<Institution> cleanedInstitutions =
          List.of(institutionBase, institutionApc, institutionLifeProject);

      List<InstitutionEntity> institutionEntities =
          cleanedInstitutions.stream()
              .map(
                  entity -> {
                    Set<InstitutionTranslationEntity> translations =
                        institutions.stream()
                            .filter(p -> p.getId().equals(entity.getId()))
                            .map(InstitutionTranslationMapper::fromDomain)
                            .collect(Collectors.toSet());
                    InstitutionEntity institutionEntity =
                        new InstitutionEntity(entity.getId(), entity.getEnabledFields());
                    institutionEntity.setTranslations(translations);
                    return institutionEntity;
                  })
              .toList();

      List<ProgramEntity> programEntities =
          institutionEntities.stream()
              .map(
                  institutionEntity -> {
                    ProgramEntity programEntity =
                        new ProgramEntity(UUID.randomUUID(), true, institutionEntity);

                    Set<ProgramTranslationEntity> programTranslations =
                        Set.of(
                            new ProgramTranslationEntity(
                                UUID.randomUUID(),
                                ELanguage.FRENCH,
                                FakeProgram.createName(),
                                programEntity),
                            new ProgramTranslationEntity(
                                UUID.randomUUID(),
                                ELanguage.ENGLISH,
                                String.format(
                                    "%s %s", FakeProgram.createName(), ELanguage.ENGLISH.getCode()),
                                programEntity),
                            new ProgramTranslationEntity(
                                UUID.randomUUID(),
                                ELanguage.SPANISH,
                                String.format(
                                    "%s %s", FakeProgram.createName(), ELanguage.SPANISH.getCode()),
                                programEntity));

                    programEntity.setTranslations(programTranslations);

                    return programEntity;
                  })
              .toList();

      List<Trace> traces = traceSeeder.withUsers(users).seed();
      // TODO: to remove when all seeders are set
      TraceEntity traceEntity = TraceMapper.fromDomain(traces.getFirst());

      SkillLevel skillLevel1 =
          FakeSkillLevel.create().withStatus(ESkillLevelStatus.VALIDATED).toModel();
      SkillLevel skillLevel2 =
          FakeSkillLevel.create().withStatus(ESkillLevelStatus.FAILED).toModel();
      SkillLevel skillLevel3 =
          FakeSkillLevel.create().withStatus(ESkillLevelStatus.UNDER_REVIEW).toModel();
      SkillLevel skillLevel4 =
          FakeSkillLevel.create().withStatus(ESkillLevelStatus.VALIDATED).toModel();
      SkillLevel skillLevel5 =
          FakeSkillLevel.create().withStatus(ESkillLevelStatus.VALIDATED).toModel();
      SkillLevel skillLevel6 =
          FakeSkillLevel.create().withStatus(ESkillLevelStatus.TO_BE_EVALUATED).toModel();
      SkillLevel skillLevel7 =
          FakeSkillLevel.create().withStatus(ESkillLevelStatus.VALIDATED).toModel();
      SkillLevel skillLevel8 = FakeSkillLevel.create().toModel();

      Skill skill1 = FakeSkill.of(Set.of(skillLevel1, skillLevel2, skillLevel3)).toModel();

      Skill skill2 = FakeSkill.of(Set.of(skillLevel4, skillLevel5, skillLevel6)).toModel();

      Skill skill3 = FakeSkill.of(Set.of(skillLevel7, skillLevel8)).toModel();

      var programProgresses =
          users.stream()
              .map(User::toStudent)
              .filter(Objects::nonNull)
              .map(
                  student -> {
                    var skills = Set.of(skill1, skill2, skill3);

                    Program program = ProgramMapper.toDomain(programEntities.getFirst());
                    var programProgress =
                        FakeProgramProgress.of(program, student, skills).toModel();

                    skills.forEach(
                        skill -> {
                          skill.setProgramProgress(programProgress);
                        });

                    skills.forEach(
                        skill -> {
                          skill
                              .getSkillLevels()
                              .forEach(
                                  skillLevel -> {
                                    skillLevel.setSkill(skill);
                                  });
                        });

                    return programProgress;
                  })
              .toList();

      ProgramProgressEntity programProgressEntity =
          ProgramProgressMapper.fromDomain(programProgresses.getFirst());

      SkillEntity skillEntity1 =
          skillEntitySetter(
              skill1,
              programProgressEntity,
              List.of(skillLevel1, skillLevel2, skillLevel3),
              traceEntity);
      SkillEntity skillEntity2 =
          skillEntitySetter(
              skill2,
              programProgressEntity,
              List.of(skillLevel4, skillLevel5, skillLevel6),
              traceEntity);
      SkillEntity skillEntity3 =
          skillEntitySetter(
              skill3, programProgressEntity, List.of(skillLevel7, skillLevel8), traceEntity);

      List<SkillEntity> skillEntities = List.of(skillEntity1, skillEntity2, skillEntity3);

      institutionRepository.saveAllEntities(institutionEntities);
      log.info("✓ {} institutions created", institutionEntities.size());

      programRepository.saveAllEntities(programEntities);
      log.info("✓ {} programs created", programEntities.size());

      programProgressRepository.saveAll(programProgresses);
      log.info("✓ {} programProgresses created", programProgresses.size());

      List<Cohort> cohorts =
          cohortSeeder.withUsers(users).withProgramProgressSet(programProgresses).seed();
      List<SkillLevel> skillLevels =
          List.of(
              skillLevel1,
              skillLevel2,
              skillLevel3,
              skillLevel4,
              skillLevel5,
              skillLevel6,
              skillLevel7,
              skillLevel8);

      skillRepository.saveAllEntities(skillEntities);

      log.info("✓ {} skills created", skillEntities.size());

      amsSeeder
          .withUsers(users)
          .withCohorts(cohorts)
          .withSkillLevels(skillLevels)
          .withTraces(traces)
          .seed();

      log.info("Seeding successfully finished");

    } else log.info("{} users found. Seeder is disabled: seeding skipped", userCont);
  }

  private SkillLevelEntity skillLevelTranslationSetter(
      SkillLevel skillLevel, SkillEntity skillEntity, TraceEntity trace) {
    SkillLevelEntity skillLevelEntity =
        SkillLevelMapper.fromDomain(skillLevel, skillEntity, List.of(trace));
    Set<SkillLevelTranslationEntity> skillLevelTranslationEntities1 =
        Set.of(
            new SkillLevelTranslationEntity(
                UUID.randomUUID(),
                ELanguage.FRENCH,
                skillLevel.getName(),
                skillLevel.getDescription(),
                skillLevelEntity),
            new SkillLevelTranslationEntity(
                UUID.randomUUID(),
                ELanguage.ENGLISH,
                String.format("%s %s", skillLevel.getName(), ELanguage.ENGLISH.getCode()),
                String.format("%s %s", skillLevel.getDescription(), ELanguage.ENGLISH.getCode()),
                skillLevelEntity),
            new SkillLevelTranslationEntity(
                UUID.randomUUID(),
                ELanguage.SPANISH,
                String.format("%s %s", skillLevel.getName(), ELanguage.SPANISH.getCode()),
                String.format("%s %s", skillLevel.getDescription(), ELanguage.SPANISH.getCode()),
                skillLevelEntity));
    skillLevelEntity.setTranslations(skillLevelTranslationEntities1);
    return skillLevelEntity;
  }

  private SkillEntity skillEntitySetter(
      Skill skill,
      ProgramProgressEntity programProgressEntity,
      List<SkillLevel> skillLevels,
      TraceEntity trace) {
    SkillEntity skillEntity = SkillMapper.fromDomain(skill, programProgressEntity);
    skillEntity.setSkillLevels(new HashSet<>());
    Set<SkillTranslationEntity> skillTranslationEntities1 =
        Set.of(
            new SkillTranslationEntity(
                UUID.randomUUID(), ELanguage.FRENCH, skill.getName(), skillEntity),
            new SkillTranslationEntity(
                UUID.randomUUID(),
                ELanguage.ENGLISH,
                String.format("%s %s", skill.getName(), ELanguage.ENGLISH.getCode()),
                skillEntity),
            new SkillTranslationEntity(
                UUID.randomUUID(),
                ELanguage.SPANISH,
                String.format("%s %s", skill.getName(), ELanguage.SPANISH.getCode()),
                skillEntity));
    skillEntity.setTranslations(skillTranslationEntities1);

    skillLevels.forEach(
        level -> {
          SkillLevelEntity skillLevelEntity =
              skillLevelTranslationSetter(level, skillEntity, trace);
          skillEntity.getSkillLevels().add(skillLevelEntity);
        });
    return skillEntity;
  }
}
