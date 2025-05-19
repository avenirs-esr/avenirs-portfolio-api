package fr.avenirsesr.portfolio.api.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.api.domain.model.Program;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.ProgramRepository;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.ProgramEntity;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProgramDatabaseRepository implements ProgramRepository {
  private final ProgramJpaRepository jpaRepository;

  @Override
  public void save(Program program) {
    var entity = toEntity(program);
    jpaRepository.save(entity);
  }

  @Override
  public void saveAll(List<Program> programs) {
    var entities = programs.stream().map(ProgramDatabaseRepository::toEntity).toList();
    jpaRepository.saveAll(entities);
  }

  public static ProgramEntity toEntity(Program program) {
    return new ProgramEntity(
        program.getId(),
        program.getName(),
        InstitutionDatabaseRepository.toEntity(program.getInstitution()));
  }
}
