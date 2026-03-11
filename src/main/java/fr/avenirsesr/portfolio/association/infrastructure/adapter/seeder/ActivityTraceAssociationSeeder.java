package fr.avenirsesr.portfolio.association.infrastructure.adapter.seeder;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.avenirsesr.portfolio.association.domain.data.ActivityTraceAssociationData;
import fr.avenirsesr.portfolio.association.domain.model.ActivityTraceAssociation;
import fr.avenirsesr.portfolio.association.domain.port.input.ActivityTraceAssociationService;
import fr.avenirsesr.portfolio.association.infrastructure.adapter.mapper.ActivityTraceAssociationMapper;
import fr.avenirsesr.portfolio.association.infrastructure.adapter.model.ActivityTraceAssociationEntity;
import fr.avenirsesr.portfolio.association.infrastructure.adapter.seeder.data.ActivityTraceAssociationCreationData;
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
  private static final String PATH_FILE = "seeder/activitiy-trace-associations.json";
  private final FileReader fileReader;
  private final ActivityTraceAssociationService activityTraceAssociationService;

  @Value("${seeder.source}")
  private ESeederSource seederSource;

  @Transactional
  public List<ActivityTraceAssociationEntity> seed(
      List<DeclaredActivityEntity> savedActivities, List<TraceEntity> savedTraces) {
    log.info("Seeding activity / trace associations...");

    List<ActivityTraceAssociationCreationData> creationData =
        switch (seederSource) {
          case CSV ->
              fileReader.readJSON(
                  PATH_FILE, new TypeReference<List<ActivityTraceAssociationCreationData>>() {});
          case FAKER ->
              savedActivities.stream()
                  .flatMap(
                      a ->
                          savedTraces.stream()
                              .map(
                                  t ->
                                      new ActivityTraceAssociationCreationData(
                                          a.getId(), t.getId())))
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

    List<ActivityTraceAssociation> associations =
        activityTraceAssociationService.createAll(
            creationData.stream()
                .map(data -> new ActivityTraceAssociationData(data.activityId(), data.traceId()))
                .toList());

    log.info("✔ {} activity / trace associations created", associations.size());
    return associations.stream().map(ActivityTraceAssociationMapper.INSTANCE::fromDomain).toList();
  }
}
