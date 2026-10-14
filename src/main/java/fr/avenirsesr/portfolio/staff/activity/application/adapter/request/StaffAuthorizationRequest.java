package fr.avenirsesr.portfolio.staff.activity.application.adapter.request;

import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityRight;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

@Schema(requiredProperties = {"staffId", "rights"})
public record StaffAuthorizationRequest(
    UUID staffId,
    @ArraySchema(schema = @Schema(ref = "#/components/schemas/EActivityRight"))
        List<EActivityRight> rights) {}
