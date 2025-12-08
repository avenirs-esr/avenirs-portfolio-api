package fr.avenirsesr.portfolio.student.progress.declared.program.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.student.progress.declared.program.domain.model.DeclaredProgram;
import fr.avenirsesr.portfolio.student.progress.declared.program.domain.port.output.DeclaredProgramRepository;
import fr.avenirsesr.portfolio.student.progress.declared.program.infrastructure.adapter.mapper.DeclaredProgramMapper;
import fr.avenirsesr.portfolio.student.progress.declared.program.infrastructure.adapter.model.DeclaredProgramEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.repository.GenericUserJpaRepositoryAdapter;
import org.springframework.stereotype.Repository;

@Repository
public class DeclaredProgramDatabaseRepository
    extends GenericUserJpaRepositoryAdapter<DeclaredProgram, DeclaredProgramEntity> implements DeclaredProgramRepository {
  public DeclaredProgramDatabaseRepository(DeclaredProgramJpaRepository jpaRepository) {
    super(
        jpaRepository,
        jpaRepository,
        DeclaredProgramMapper::fromDomain,
        DeclaredProgramMapper::toDomain);
  }
}
