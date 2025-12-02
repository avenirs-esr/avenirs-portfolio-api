package fr.avenirsesr.portfolio.program.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.repository.GenericTranslatedJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.program.domain.model.Program;
import fr.avenirsesr.portfolio.program.domain.port.output.repository.ProgramRepository;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.mapper.ProgramMapper;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.ProgramEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.ProgramTranslationEntity;
import org.springframework.stereotype.Component;

@Component
public class ProgramDatabaseRepository
    extends GenericTranslatedJpaRepositoryAdapter<Program, ProgramEntity, ProgramTranslationEntity>
    implements ProgramRepository {

  public ProgramDatabaseRepository(
      ProgramJpaRepository jpaRepository,
      ProgramTranslationJpaRepository translationJpaRepository) {
    super(
        jpaRepository,
        jpaRepository,
        translationJpaRepository,
        translationJpaRepository,
        ProgramMapper::fromDomain,
        ProgramMapper::toDomain,
        ProgramMapper::toDomain);
  }
}
