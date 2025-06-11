package fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.api.domain.model.Program;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.ProgramTranslationEntity;

public interface ProgramTranslationMapper {
  static ProgramTranslationEntity fromDomain(Program program) {
    return new ProgramTranslationEntity(
        program.getId(),
        program.getLanguage(),
        program.getName(),
        ProgramMapper.fromDomain(program));
  }
}
