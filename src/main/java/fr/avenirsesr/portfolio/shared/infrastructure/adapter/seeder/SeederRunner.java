package fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.ams.domain.model.Cohort;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.seeder.AMSSeeder;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.seeder.CohortSeeder;
import fr.avenirsesr.portfolio.programprogress.domain.model.Program;
import fr.avenirsesr.portfolio.programprogress.domain.model.Skill;
import fr.avenirsesr.portfolio.programprogress.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.programprogress.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.programprogress.domain.port.output.repository.ProgramProgressRepository;
import fr.avenirsesr.portfolio.programprogress.domain.port.output.repository.SkillLevelRepository;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.mapper.ProgramMapper;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.mapper.ProgramProgressMapper;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.mapper.SkillLevelMapper;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.mapper.SkillMapper;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.model.InstitutionEntity;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.model.InstitutionTranslationEntity;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.model.ProgramEntity;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.model.ProgramProgressEntity;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.model.ProgramTranslationEntity;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.model.SkillEntity;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.model.SkillLevelEntity;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.model.SkillLevelTranslationEntity;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.model.SkillTranslationEntity;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.repository.InstitutionDatabaseRepository;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.repository.ProgramDatabaseRepository;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.repository.SkillDatabaseRepository;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.seeder.fake.FakeInstitution;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.seeder.fake.FakeProgram;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.seeder.fake.FakeProgramProgress;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.seeder.fake.FakeSkill;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.seeder.fake.FakeSkillLevel;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EDurationUnit;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EPortfolioType;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.mapper.TraceMapper;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.seeder.TraceSeeder;
import fr.avenirsesr.portfolio.user.domain.model.User;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.ExternalUserRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserRepository;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.UserSeeder;
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

      var institutionBase =
          FakeInstitution.create()
              .addTranslation(ELanguage.ENGLISH)
              .addTranslation(ELanguage.SPANISH);
      var institutionApc =
          FakeInstitution.create()
              .withEnabledFiled(Set.of(EPortfolioType.APC))
              .addTranslation(ELanguage.ENGLISH)
              .addTranslation(ELanguage.SPANISH);
      var institutionLifeProject =
          FakeInstitution.create()
              .withEnabledFiled(Set.of(EPortfolioType.LIFE_PROJECT))
              .addTranslation(ELanguage.ENGLISH)
              .addTranslation(ELanguage.SPANISH);

      var fakeInstitutions = List.of(institutionBase, institutionApc, institutionLifeProject);

      List<InstitutionEntity> institutionEntities =
          fakeInstitutions.stream()
              .map(
                  fakeInstitution -> {
                    InstitutionEntity institutionEntity =
                        new InstitutionEntity(
                            fakeInstitution.toModel().getId(),
                            fakeInstitution.toModel().getEnabledFields());

                    var translations =
                        fakeInstitution.getTranslations().stream()
                            .map(
                                t ->
                                    new InstitutionTranslationEntity(
                                        UUID.randomUUID(),
                                        t.language(),
                                        t.name(),
                                        institutionEntity))
                            .collect(Collectors.toSet());

                    institutionEntity.setTranslations(translations);
                    return institutionEntity;
                  })
              .toList();

      List<ProgramEntity> programEntities =
          institutionEntities.stream()
              .map(
                  institutionEntity -> {
                    ProgramEntity programEntity =
                        new ProgramEntity(
                            UUID.randomUUID(), true, institutionEntity, EDurationUnit.YEAR, 2);

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
