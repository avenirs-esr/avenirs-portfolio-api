package fr.avenirsesr.portfolio.program.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.EntityGrapher;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.common.language.infrastructure.adapter.utils.TranslationUtil;
import fr.avenirsesr.portfolio.program.domain.model.Program;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.ProgramEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.ProgramTranslationEntity;

public class ProgramMapper implements Mapper<ProgramEntity, Program> {
  public static ProgramMapper INSTANCE = new ProgramMapper();

  @Override
  public ProgramEntity fromDomain(Program program) {
    return ProgramEntity.of(
        program.getId(),
        program.isAPC(),
        InstitutionMapper.INSTANCE.fromDomain(program.getInstitution()),
        program.getDurationUnit().orElse(null),
        program.getDurationCount().orElse(null));
  }

  @Override
  public Program toDomain(ProgramEntity programEntity) {
    ProgramTranslationEntity translationEntity =
        TranslationUtil.getTranslation(programEntity.getTranslations());
    return Program.toDomain(
        programEntity.getId(),
        InstitutionMapper.INSTANCE.toDomain(programEntity.getInstitution()),
        translationEntity.getName(),
        programEntity.isAPC(),
        programEntity.getDurationUnit(),
        programEntity.getDurationCount(),
        programEntity.getCreatedAt(),
        programEntity.getUpdatedAt());
  }

  @Override
  public Program toDomain(ProgramEntity programEntity, EntityGrapher<?> graph) {
    var attributes = graph.attributes();
    ProgramTranslationEntity translationEntity =
        TranslationUtil.getTranslation(programEntity.getTranslations());

    return Program.toDomain(
        programEntity.getId(),
        attributes.contains("institution")
            ? InstitutionMapper.INSTANCE.toDomain(programEntity.getInstitution())
            : null,
        translationEntity.getName(),
        programEntity.isAPC(),
        programEntity.getDurationUnit(),
        programEntity.getDurationCount(),
        programEntity.getCreatedAt(),
        programEntity.getUpdatedAt());
  }
}
