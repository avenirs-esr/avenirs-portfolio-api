package fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.api.domain.model.Program;
import fr.avenirsesr.portfolio.api.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.ProgramEntity;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.ProgramTranslationEntity;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.util.TranslationUtil;

public interface ProgramMapper {
  static ProgramEntity fromDomain(Program program) {
    return new ProgramEntity(
        program.getId(), program.isAPC(), InstitutionMapper.fromDomain(program.getInstitution()));
  }

  static Program toDomain(ProgramEntity programEntity) {
    return toDomain(programEntity, ELanguage.FRENCH);
  }

  static Program toDomain(ProgramEntity programEntity, ELanguage language) {
    ELanguage fallbackLanguage = ELanguage.FRENCH;
    ProgramTranslationEntity translationEntity =
        TranslationUtil.getTranslation(programEntity.getTranslations(), language, fallbackLanguage);
    return Program.toDomain(
        programEntity.getId(),
        InstitutionMapper.toDomain(programEntity.getInstitution(), language),
        translationEntity.getName(),
        programEntity.isAPC(),
        language);
  }
}
