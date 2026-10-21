package fr.avenirsesr.portfolio.skill.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.skill.domain.model.NationalSkillDetails;
import fr.avenirsesr.portfolio.skill.infrastructure.adapter.model.NationalSkillDetailsEntity;

public class NationalSkillDetailsMapper
    implements Mapper<NationalSkillDetailsEntity, NationalSkillDetails> {
  public static final NationalSkillDetailsMapper INSTANCE = new NationalSkillDetailsMapper();

  @Override
  public NationalSkillDetailsEntity fromDomain(NationalSkillDetails domain) {
    return NationalSkillDetailsEntity.of(
        domain.getId(),
        SkillMapper.INSTANCE.fromDomain(domain.getSkill()),
        domain.getKind(),
        domain.getCreatedAt(),
        domain.getUpdatedAt());
  }

  @Override
  public NationalSkillDetails toDomain(NationalSkillDetailsEntity entity) {
    return NationalSkillDetails.toDomain(
        entity.getId(),
        SkillMapper.INSTANCE.toDomain(entity.getSkill()),
        entity.getKind(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }
}
