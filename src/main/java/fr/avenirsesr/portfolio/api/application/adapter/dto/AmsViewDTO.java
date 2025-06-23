package fr.avenirsesr.portfolio.api.application.adapter.dto;

import fr.avenirsesr.portfolio.api.domain.model.enums.EAmsStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(requiredProperties = {"id", "title", "countSkills", "countTraces", "status", "progress"})
public record AmsViewDTO(
    UUID id,
    String title,
    int countSkills,
    int countTraces,
    @Schema(implementation = EAmsStatus.class) EAmsStatus status,
    AmsProgressDTO progress) {}
