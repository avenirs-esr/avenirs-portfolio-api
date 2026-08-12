package fr.avenirsesr.portfolio.user.application.adapter.dto;

import fr.avenirsesr.portfolio.common.security.accesscontrol.domain.model.enums.ERole;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;

@Schema(requiredProperties = {"firstname", "lastname", "roles"})
public record LoggedInUserDTO(
    String firstname,
    String lastname,
    @Schema(ref = "#/components/schemas/ERole") Set<ERole> roles) {}
