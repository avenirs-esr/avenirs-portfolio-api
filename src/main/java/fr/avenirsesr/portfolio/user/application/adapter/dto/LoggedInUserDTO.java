package fr.avenirsesr.portfolio.user.application.adapter.dto;

import fr.avenirsesr.portfolio.common.security.accesscontrol.domain.model.enums.ERole;
import fr.avenirsesr.portfolio.user.domain.model.enums.EUniverse;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;

@Schema(requiredProperties = {"roles", "universes"})
public record LoggedInUserDTO(
    @ArraySchema(schema = @Schema(ref = "#/components/schemas/ERole")) Set<ERole> roles,
    @ArraySchema(schema = @Schema(ref = "#/components/schemas/EUniverse")) Set<EUniverse> universes,
    AcceptedCguDTO acceptedCgu) {}
