package fr.avenirsesr.portfolio.api.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.api.domain.model.ProgramProgress;
import fr.avenirsesr.portfolio.api.domain.model.Student;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.ProgramProgressRepository;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper.ProgramProgressMapper;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper.UserMapper;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.ProgramProgressEntity;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.specification.ProgramProgressSpecification;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ProgramProgressDatabaseRepository
    extends GenericJpaRepositoryAdapter<ProgramProgress, ProgramProgressEntity>
    implements ProgramProgressRepository {
  public ProgramProgressDatabaseRepository(ProgramProgressJpaRepository jpaRepository) {
    super(
        jpaRepository,
        jpaRepository,
        ProgramProgressMapper::fromDomain,
        ProgramProgressMapper::toDomain);
  }

  @Override
  public List<ProgramProgress> findAllByStudent(Student student) {
    return jpaSpecificationExecutor
        .findAll(ProgramProgressSpecification.hasStudent(UserMapper.fromDomain(student)))
        .stream()
        .map(ProgramProgressMapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public List<ProgramProgress> findAllAPCByStudent(Student student) {
    return jpaSpecificationExecutor
        .findAll(
            ProgramProgressSpecification.hasStudent(UserMapper.fromDomain(student))
                .and(ProgramProgressSpecification.isAPC()))
        .stream()
        .map(ProgramProgressMapper::toDomain)
        .collect(Collectors.toList());
  }
}
