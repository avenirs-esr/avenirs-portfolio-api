package fr.avenirsesr.portfolio.student.progress.imported.application.adapter.dto;

import fr.avenirsesr.portfolio.additionalskill.application.adapter.dto.AdditionalSkillCategoryDTO;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillLevel;
import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillType;
import fr.avenirsesr.portfolio.trace.application.adapter.dto.TraceOverviewDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(
    requiredProperties = {
      "id",
      "title",
      "pathSegments",
      "type",
      "level",
      "traceAssociations",
      "createdAt",
      "updatedAt"
    })
public record AdditionalSkillProgressDetailsDTO(
    UUID id,
    String title,
    List<AdditionalSkillCategoryDTO> pathSegments,
    String description,
    @Schema(ref = "#/components/schemas/EExternalSkillType") EExternalSkillType type,
    @Schema(ref = "#/components/schemas/EAdditionalSkillLevel") EAdditionalSkillLevel level,
    List<TraceOverviewDTO> traceAssociations,
    Instant createdAt,
    Instant updatedAt) {}
