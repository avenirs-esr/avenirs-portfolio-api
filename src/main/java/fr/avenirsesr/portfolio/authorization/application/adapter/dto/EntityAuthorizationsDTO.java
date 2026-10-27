package fr.avenirsesr.portfolio.authorization.application.adapter.dto;

import fr.avenirsesr.portfolio.common.authorization.domain.model.enums.EAuthorization;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

@Schema(requiredProperties = {"entityId", "authorizations"})
public record EntityAuthorizationsDTO(
    UUID entityId,
    @ArraySchema(schema = @Schema(ref = "#/components/schemas/EAuthorization"))
        List<EAuthorization> authorizations) {}
