package fr.avenirsesr.portfolio.trace.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.ams.infrastructure.adapter.mapper.AMSMapper;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.EntityGrapher;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.mapper.AdditionalSkillProgressMapper;
import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.mapper.SkillLevelProgressMapper;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;
import java.util.List;

public class TraceMapper implements Mapper<TraceEntity, Trace> {
  public static final TraceMapper INSTANCE = new TraceMapper();

  @Override
  public TraceEntity fromDomain(Trace trace) {
    return TraceEntity.of(
        trace.getId(),
        UserMapper.INSTANCE.fromDomain(trace.getUser()),
        trace.getTitle(),
        trace.getLanguage(),
        trace.getSkillLevels().stream().map(SkillLevelProgressMapper.INSTANCE::fromDomain).toList(),
        trace.getAdditionalSkillProgresses().stream()
            .map(AdditionalSkillProgressMapper.INSTANCE::fromDomain)
            .toList(),
        trace.getAmses().stream().map(AMSMapper.INSTANCE::fromDomain).toList(),
        trace.isGroup(),
        trace.getAiUseJustification().orElse(null),
        trace.getPersonalNote().orElse(null),
        trace.getCreatedAt(),
        trace.getUpdatedAt(),
        trace.getDeletedAt().orElse(null));
  }

  @Override
  public Trace toDomain(TraceEntity traceEntity) {
    Trace trace =
        Trace.toDomain(
            traceEntity.getId(),
            UserMapper.INSTANCE.toDomain(traceEntity.getUser()),
            traceEntity.getTitle(),
            List.of(),
            traceEntity.getAdditionalSkillsProgresses().stream()
                .map(AdditionalSkillProgressMapper.INSTANCE::toDomain)
                .toList(),
            traceEntity.getAmses().stream().map(AMSMapper.INSTANCE::toDomain).toList(),
            traceEntity.isGroup(),
            traceEntity.getAiUseJustification(),
            traceEntity.getPersonalNote(),
            traceEntity.getCreatedAt(),
            traceEntity.getUpdatedAt(),
            traceEntity.getDeletedAt(),
            traceEntity.getLanguage());
    trace.setSkillLevels(
        traceEntity.getSkillLevels().stream()
            .map(SkillLevelProgressMapper.INSTANCE::toDomain)
            .toList());
    return trace;
  }

  @Override
  public Trace toDomain(TraceEntity traceEntity, EntityGrapher<?> graph) {
    var attributes = graph.attributes();
    return Trace.toDomain(
        traceEntity.getId(),
        attributes.contains("user") ? UserMapper.INSTANCE.toDomain(traceEntity.getUser()) : null,
        traceEntity.getTitle(),
        attributes.contains("skillLevels")
            ? traceEntity.getSkillLevels().stream()
                .map(e -> SkillLevelProgressMapper.INSTANCE.toDomain(e, graph.from("skillLevels")))
                .toList()
            : List.of(),
        attributes.contains("additionalSkillsProgresses")
            ? traceEntity.getAdditionalSkillsProgresses().stream()
                .map(
                    e ->
                        AdditionalSkillProgressMapper.INSTANCE.toDomain(
                            e, graph.from("additionalSkillsProgresses")))
                .toList()
            : List.of(),
        attributes.contains("amses")
            ? traceEntity.getAmses().stream()
                .map(e -> AMSMapper.INSTANCE.toDomain(e, graph.from("amses")))
                .toList()
            : List.of(),
        traceEntity.isGroup(),
        traceEntity.getAiUseJustification(),
        traceEntity.getPersonalNote(),
        traceEntity.getCreatedAt(),
        traceEntity.getUpdatedAt(),
        traceEntity.getDeletedAt(),
        traceEntity.getLanguage());
  }
}
