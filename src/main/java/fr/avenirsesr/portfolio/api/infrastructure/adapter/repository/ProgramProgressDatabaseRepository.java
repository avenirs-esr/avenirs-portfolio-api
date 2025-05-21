package fr.avenirsesr.portfolio.api.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.api.domain.model.ProgramProgress;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.ProgramProgressRepository;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper.ProgramProgressMapper;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.ProgramProgressEntity;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.specification.ProgramProgressSpecifications;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProgramProgressDatabaseRepository implements ProgramProgressRepository {
  private final ProgramProgressJpaRepository jpaRepository;

  @Override
  public void save(ProgramProgress progress) {
    var entity = toEntity(progress);
    jpaRepository.save(entity);
  }

  @Override
  public void saveAll(List<ProgramProgress> progress) {
    var entities = progress.stream().map(ProgramProgressDatabaseRepository::toEntity).toList();
    jpaRepository.saveAll(entities);
  }

  @Override
  public List<ProgramProgress> getSkillsOverview(UUID userId) {
    return jpaRepository.findAll(ProgramProgressSpecifications.findByUserId(userId)).stream()
        .map(ProgramProgressMapper::fromEntityToModel)
        .toList();
  }

  public static ProgramProgressEntity toEntity(ProgramProgress programProgress) {
    return new ProgramProgressEntity(
        programProgress.getId(),
        ProgramDatabaseRepository.toEntity(programProgress.getProgram()),
        UserDatabaseRepository.toEntity(programProgress.getStudent().getUser()),
        programProgress.getSkills().stream()
            .map(SkillDatabaseRepository::toEntity)
            .collect(Collectors.toSet()));
  }
}
