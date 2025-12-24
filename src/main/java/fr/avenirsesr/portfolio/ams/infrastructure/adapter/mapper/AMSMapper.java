package fr.avenirsesr.portfolio.ams.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.AMSEntity;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.AMSTranslationEntity;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.EntityGrapher;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.common.language.infrastructure.adapter.utils.TranslationUtil;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.StudentMapper;

public class AMSMapper implements Mapper<AMSEntity, AMS> {
  public static final AMSMapper INSTANCE = new AMSMapper();

  @Override
  public AMSEntity fromDomain(AMS ams) {
    return AMSEntity.of(
        ams.getId(),
        StudentMapper.INSTANCE.fromDomain(ams.getStudent()),
        ams.getStatus(),
        ams.getStartDate(),
        ams.getEndDate());
  }

  @Override
  public AMS toDomain(AMSEntity entity) {
    AMSTranslationEntity translationEntity =
        TranslationUtil.getTranslation(entity.getTranslations());
    return AMS.toDomain(
        entity.getId(),
        StudentMapper.INSTANCE.toDomain(entity.getStudent()),
        translationEntity.getTitle(),
        entity.getStartDate(),
        entity.getEndDate(),
        entity.getStatus(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }

  @Override
  public AMS toDomain(AMSEntity entity, EntityGrapher<?> graph) {
    AMSTranslationEntity translationEntity =
        TranslationUtil.getTranslation(entity.getTranslations());
    var attributes = graph.attributes();
    return AMS.toDomain(
        entity.getId(),
        attributes.contains("student")
            ? StudentMapper.INSTANCE.toDomain(entity.getStudent(), graph.from("student"))
            : null,
        translationEntity.getTitle(),
        entity.getStartDate(),
        entity.getEndDate(),
        entity.getStatus(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }
}
