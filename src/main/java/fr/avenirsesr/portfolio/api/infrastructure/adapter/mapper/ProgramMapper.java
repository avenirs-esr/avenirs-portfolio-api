package fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.api.domain.model.Program;
import fr.avenirsesr.portfolio.api.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.ProgramEntity;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.ProgramTranslationEntity;

public interface ProgramMapper {
  static ProgramEntity fromDomain(Program program) {
    return new ProgramEntity(
        program.getId(), program.isAPC(), InstitutionMapper.fromDomain(program.getInstitution()));
  }

  static Program toDomain(ProgramEntity programEntity) {
    return toDomain(programEntity, ELanguage.FRENCH);
  }

  static Program toDomain(ProgramEntity programEntity, ELanguage language) {
    String name =
        programEntity.getTranslations().stream()
            .filter(t -> t.getLanguage().equals(language))
            .findFirst()
            .map(ProgramTranslationEntity::getName)
            .orElse(null);
    return Program.toDomain(
        programEntity.getId(),
        InstitutionMapper.toDomain(programEntity.getInstitution()),
        name,
        programEntity.isAPC(),
        language);
  }
}
