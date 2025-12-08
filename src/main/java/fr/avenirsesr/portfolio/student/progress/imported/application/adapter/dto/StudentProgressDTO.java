package fr.avenirsesr.portfolio.student.progress.imported.application.adapter.dto;

import fr.avenirsesr.portfolio.common.temporal.domain.model.enums.EDurationUnit;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(requiredProperties = {"id", "studentId", "trainingPath"})
public record StudentProgressDTO(UUID id, UUID studentId, TrainingPathDTO trainingPath) {
  @Schema(requiredProperties = {"id", "name", "durationUnit", "durationCount"})
  public record TrainingPathDTO(
      UUID id,
      String name,
      @Schema(ref = "#/components/schemas/EDurationUnit") EDurationUnit durationUnit,
      Integer durationCount) {}
}
