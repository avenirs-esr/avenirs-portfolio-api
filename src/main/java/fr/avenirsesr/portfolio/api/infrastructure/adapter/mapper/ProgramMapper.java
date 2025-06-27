package fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.api.domain.model.Program;
import fr.avenirsesr.portfolio.api.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.ProgramEntity;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.ProgramTranslationEntity;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.util.TranslationUtil;

public interface ProgramMapper {
  static ProgramEntity fromDomain(Program program) {
    return new ProgramEntity(
        program.getId(),
        program.isAPC(),
        InstitutionMapper.fromDomain(program.getInstitution()),
        program.getDurationUnit(),
        program.getDurationCount());
  }

  static Program toDomain(ProgramEntity programEntity) {
    return toDomain(programEntity, ELanguage.FRENCH);
  }

  static Program toDomain(ProgramEntity programEntity, ELanguage language) {
    ProgramTranslationEntity translationEntity =
        TranslationUtil.getTranslation(programEntity.getTranslations());
    return Program.toDomain(
        programEntity.getId(),
        InstitutionMapper.toDomain(programEntity.getInstitution()),
        translationEntity.getName(),
        programEntity.isAPC(),
        programEntity.getDurationUnit(),
        programEntity.getDurationCount());
  }
}
