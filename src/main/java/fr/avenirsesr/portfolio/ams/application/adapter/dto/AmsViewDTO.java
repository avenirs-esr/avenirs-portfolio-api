package fr.avenirsesr.portfolio.ams.application.adapter.dto;

import fr.avenirsesr.portfolio.ams.domain.model.enums.EAmsStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(requiredProperties = {"id", "title", "countSkills", "countTraces", "status", "progress"})
public record AmsViewDTO(
    UUID id,
    String title,
    int countSkills,
    int countTraces,
    @Schema(ref = "#/components/schemas/AmsStatus") EAmsStatus status,
    AmsProgressDTO progress) {}
