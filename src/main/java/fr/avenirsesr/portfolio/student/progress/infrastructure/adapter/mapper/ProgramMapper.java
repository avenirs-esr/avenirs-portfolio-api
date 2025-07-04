package fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.domain.model.Program;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.ProgramEntity;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.ProgramTranslationEntity;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.utils.TranslationUtil;

public interface ProgramMapper {
  static ProgramEntity fromDomain(Program program) {
    return ProgramEntity.of(
        program.getId(),
        program.isAPC(),
        InstitutionMapper.fromDomain(program.getInstitution()),
        program.getDurationUnit().orElse(null),
        program.getDurationCount().orElse(null));
  }

  static Program toDomain(ProgramEntity programEntity) {
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
