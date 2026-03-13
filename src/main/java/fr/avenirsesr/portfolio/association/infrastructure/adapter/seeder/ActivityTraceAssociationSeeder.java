package fr.avenirsesr.portfolio.association.infrastructure.adapter.seeder;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.avenirsesr.portfolio.association.domain.data.AssociationData;
import fr.avenirsesr.portfolio.association.domain.model.Association;
import fr.avenirsesr.portfolio.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.association.infrastructure.adapter.mapper.AssociationMapper;
import fr.avenirsesr.portfolio.association.infrastructure.adapter.model.AssociationEntity;
import fr.avenirsesr.portfolio.association.infrastructure.adapter.seeder.data.AssociationCreationData;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.ESeederSource;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederConfig;
import fr.avenirsesr.portfolio.shared.infrastructure.utils.FileReader;
import fr.avenirsesr.portfolio.student.progress.declared.activity.infrastructure.adapter.model.DeclaredActivityEntity;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ActivityTraceAssociationSeeder {
  private static final String PATH_FILE = "seeder/associations.json";
  private final FileReader fileReader;
  private final AssociationService associationService;

  @Value("${seeder.source}")
  private ESeederSource seederSource;

  @Transactional
  public List<AssociationEntity> seed(
      List<DeclaredActivityEntity> savedActivities, List<TraceEntity> savedTraces) {
    log.info("Seeding activity / trace associations...");

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
                                          a.getId(),
                                          t.getId(),
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
                .map(data -> new AssociationData(data.id1(), data.id2(), data.associationType()))
                .toList());

    log.info("✔ {} activity / trace associations created", associations.size());
    return associations.stream().map(AssociationMapper.INSTANCE::fromDomain).toList();
  }
}
