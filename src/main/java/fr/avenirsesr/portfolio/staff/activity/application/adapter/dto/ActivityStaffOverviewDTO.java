package fr.avenirsesr.portfolio.staff.activity.application.adapter.dto;

import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityStatus;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityThematic;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(requiredProperties = {"activityId", "title", "leaders", "activityStatus", "updatedAt"})
public record ActivityStaffOverviewDTO(
    UUID activityId,
    List<StaffInfoDTO> leaders,
    String title,
    @Schema(ref = "#/components/schemas/EActivityThematic") EActivityThematic thematic,
    @Schema(ref = "#/components/schemas/EActivityStatus") EActivityStatus activityStatus,
    Instant updatedAt) {}
