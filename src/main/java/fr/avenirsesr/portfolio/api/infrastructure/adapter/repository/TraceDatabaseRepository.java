package fr.avenirsesr.portfolio.api.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.api.domain.model.Trace;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.TraceRepository;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.TraceEntity;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TraceDatabaseRepository implements TraceRepository {
  private final TraceJpaRepository jpaRepository;

  @Override
  public void save(Trace trace) {
    var entity = toEntity(trace);
    jpaRepository.save(entity);
  }

  @Override
  public void saveAll(List<Trace> traces) {
    var entities = traces.stream().map(TraceDatabaseRepository::toEntity).toList();
    jpaRepository.saveAll(entities);
  }

  public static TraceEntity toEntity(Trace trace) {
    return new TraceEntity(
        trace.getId(),
        UserDatabaseRepository.toEntity(trace.getUser()),
        trace.getSkillLevels().stream().map(SkillLevelDatabaseRepository::toEntity).toList(),
        trace.getAmses().stream().map(AMSDatabaseRepository::toEntity).toList());
  }
}
