package fr.avenirsesr.portfolio.association.infrastructure.adapter.seeder;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.avenirsesr.portfolio.association.domain.data.AssociationData;
import fr.avenirsesr.portfolio.association.domain.model.Association;
import fr.avenirsesr.portfolio.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.association.infrastructure.adapter.mapper.AssociationMapper;
import fr.avenirsesr.portfolio.association.infrastructure.adapter.model.AssociationEntity;
import fr.avenirsesr.portfolio.association.infrastructure.adapter.seeder.data.AssociationCreationData;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.AvenirsBaseEntity;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.ESeederSource;
import fr.avenirsesr.portfolio.declaredskill.infrastructure.adapter.model.DeclaredSkillEntity;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederConfig;
import fr.avenirsesr.portfolio.shared.infrastructure.utils.FileReader;
import fr.avenirsesr.portfolio.student.progress.declared.activity.infrastructure.adapter.model.DeclaredActivityEntity;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
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
      List<DeclaredSkillEntity> savedDeclaredSkills) {
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
            creationData.stream().map(mapToAssociationCreationData(savedDeclaredSkills)).toList());

    log.info("✔ {} associations created", associations.size());
    return associations.stream().map(AssociationMapper.INSTANCE::fromDomain).toList();
  }

  private Function<AssociationCreationData, AssociationData> mapToAssociationCreationData(
      List<DeclaredSkillEntity> savedDeclaredSkills) {

    return data -> {
      Function<String, UUID> mapperId1 =
          switch (data.associationType()) {
            case DECLARED_ACTIVITY_TRACE, TRACE_DECLARED_SKILL, DECLARED_ACTIVITY_DECLARED_SKILL ->
                UUID::fromString;
          };

      Function<String, UUID> mapperId2 =
          switch (data.associationType()) {
            case DECLARED_ACTIVITY_TRACE -> UUID::fromString;
            case DECLARED_ACTIVITY_DECLARED_SKILL, TRACE_DECLARED_SKILL ->
                id -> resolveDynamicId(id, savedDeclaredSkills);
          };

      return new AssociationData(
          mapperId1.apply(data.id1()), mapperId2.apply(data.id2()), data.associationType());
    };
  }

  private UUID resolveDynamicId(String id, List<? extends AvenirsBaseEntity> savedEntities) {
    int index = Integer.parseInt(id.substring(id.lastIndexOf('_') + 1));

    if (index < 0 || index >= savedEntities.size()) {
      throw new IllegalArgumentException("Invalid dynamic id: " + id);
    }

    return savedEntities.get(index).getId();
  }
}
