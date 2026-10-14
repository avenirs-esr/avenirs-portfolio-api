package fr.avenirsesr.portfolio.staff.activity.application.adapter.dto;

import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityRight;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(requiredProperties = {"staff", "rights"})
public record ActivityAuthorizationDTO(
    StaffInfoDTO staff,
    @ArraySchema(schema = @Schema(ref = "#/components/schemas/EActivityRight"))
        List<EActivityRight> rights) {}
