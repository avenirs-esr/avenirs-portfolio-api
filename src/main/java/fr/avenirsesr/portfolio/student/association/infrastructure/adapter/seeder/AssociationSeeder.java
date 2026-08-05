package fr.avenirsesr.portfolio.student.association.infrastructure.adapter.seeder;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociationData;
import fr.avenirsesr.portfolio.student.association.domain.model.Association;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.student.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.student.association.infrastructure.adapter.mapper.AssociationMapper;
import fr.avenirsesr.portfolio.student.association.infrastructure.adapter.model.AssociationEntity;
import fr.avenirsesr.portfolio.student.association.infrastructure.adapter.seeder.data.AssociationCreationData;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.ESeederSource;
import fr.avenirsesr.portfolio.common.utils.FileReader;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederConfig;
import fr.avenirsesr.portfolio.student.progress.declared.activity.infrastructure.adapter.model.DeclaredActivityEntity;
import fr.avenirsesr.portfolio.student.progress.declared.experience.infrastructure.adapter.model.DeclaredExperienceEntity;
import fr.avenirsesr.portfolio.student.progress.declared.skill.infrastructure.adapter.model.DeclaredSkillProgressEntity;
import fr.avenirsesr.portfolio.student.trace.infrastructure.adapter.model.TraceEntity;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AssociationSeeder {
  private static final String PATH_FILE = "seeder/associations.json";

  private final FileReader fileReader;
  private final AssociationService associationService;

  @Value("${seeder.source}")
  private ESeederSource seederSource;

  @Transactional
  public List<AssociationEntity> seed(
      List<DeclaredActivityEntity> savedActivities,
      List<TraceEntity> savedTraces,
      List<DeclaredSkillProgressEntity> savedDeclaredSkillProgresses,
      List<DeclaredExperienceEntity> savedDeclaredExperiences) {
    log.info("Seeding associations...");

    List<AssociationCreationData> creationData =
        switch (seederSource) {
          case CSV ->
              fileReader.readJSON(PATH_FILE, new TypeReference<List<AssociationCreationData>>() {});
          case FAKER ->
              savedActivities.stream()
                  .flatMap(
                      a ->
                          savedTraces.stream()
                              .map(
                                  t ->
                                      new AssociationCreationData(
                                          a.getId().toString(),
                                          t.getId().toString(),
                                          EAssociationType.DECLARED_ACTIVITY_TRACE)))
                  .distinct()
                  .collect(
                      Collectors.collectingAndThen(
                          Collectors.toList(),
                          list -> {
                            Collections.shuffle(list);
                            return list.stream()
                                .limit(SeederConfig.NB_DECLARED_ACTIVITIES_TRACE_ASSOCIATION)
                                .toList();
                          }));
        };

    List<Association> associations =
        associationService.createAll(
            creationData.stream()
                .map(
                    mapToAssociationCreationData(
                        savedDeclaredSkillProgresses, savedDeclaredExperiences))
                .toList());

    log.info("✔ {} associations created", associations.size());
    return associations.stream().map(AssociationMapper.INSTANCE::fromDomain).toList();
  }

  private Function<AssociationCreationData, AssociationData> mapToAssociationCreationData(
      List<DeclaredSkillProgressEntity> savedDeclaredSkillProgresses,
      List<DeclaredExperienceEntity> savedDeclaredExperiences) {

    return data -> {
      Function<String, UUID> mapperId1 =
          switch (data.associationType()) {
            case DECLARED_ACTIVITY_TRACE,
                TRACE_DECLARED_SKILL,
                DECLARED_ACTIVITY_DECLARED_SKILL,
                TRACE_DECLARED_EXPERIENCE,
                DECLARED_EXPERIENCE_DECLARED_SKILL ->
                UUID::fromString;
          };

      Function<String, UUID> mapperId2 =
          switch (data.associationType()) {
            case DECLARED_ACTIVITY_TRACE -> UUID::fromString;

            case DECLARED_ACTIVITY_DECLARED_SKILL,
                TRACE_DECLARED_SKILL,
                DECLARED_EXPERIENCE_DECLARED_SKILL ->
                id ->
                    resolveDynamicIdWithStudentParam(
                        id,
                        savedDeclaredSkillProgresses,
                        progress ->
                            progress.getStudent() != null ? progress.getStudent().getId() : null,
                        DeclaredSkillProgressEntity::getId,
                        Comparator.comparing(progress -> progress.getDeclaredSkill().getLibelle()));
            case TRACE_DECLARED_EXPERIENCE ->
                id ->
                    resolveDynamicIdWithStudentParam(
                        id,
                        savedDeclaredExperiences,
                        experience ->
                            experience.getStudent() != null
                                ? experience.getStudent().getId()
                                : null,
                        DeclaredExperienceEntity::getId,
                        declaredExperienceComparator());
          };

      return new AssociationData(
          mapperId1.apply(data.id1()), mapperId2.apply(data.id2()), data.associationType());
    };
  }

  private <T> UUID resolveDynamicIdWithStudentParam(
      String dynamicId,
      List<T> savedEntities,
      Function<T, UUID> studentIdExtractor,
      Function<T, UUID> entityIdExtractor,
      Comparator<T> sortComparator) {

    int lastSeparatorIndex = dynamicId.lastIndexOf('_');
    int secondLastSeparatorIndex = dynamicId.lastIndexOf('_', lastSeparatorIndex - 1);

    if (lastSeparatorIndex < 0 || secondLastSeparatorIndex < 0) {
      throw new IllegalArgumentException("Invalid dynamic student scoped id format: " + dynamicId);
    }

    String studentIdRaw = dynamicId.substring(secondLastSeparatorIndex + 1, lastSeparatorIndex);
    String indexRaw = dynamicId.substring(lastSeparatorIndex + 1);

    UUID studentId = UUID.fromString(studentIdRaw);
    int index = Integer.parseInt(indexRaw);

    Stream<T> filteredStream =
        savedEntities.stream().filter(entity -> studentId.equals(studentIdExtractor.apply(entity)));

    if (sortComparator != null) {
      filteredStream = filteredStream.sorted(sortComparator);
    }

    List<T> filteredEntities = filteredStream.toList();

    if (index < 0 || index >= filteredEntities.size()) {
      throw new IllegalArgumentException(
          "Invalid dynamic student scoped index: "
              + dynamicId
              + ", available size="
              + filteredEntities.size());
    }

    return entityIdExtractor.apply(filteredEntities.get(index));
  }

  private Comparator<DeclaredExperienceEntity> declaredExperienceComparator() {
    return Comparator.comparing(
            (DeclaredExperienceEntity experience) -> experience.getEndDate() == null ? 1 : 0,
            Comparator.reverseOrder())
        .thenComparing(DeclaredExperienceEntity::getStartDate, Comparator.reverseOrder())
        .thenComparing(
            experience -> experience.getTitle() == null ? "" : experience.getTitle().toLowerCase());
  }
}
