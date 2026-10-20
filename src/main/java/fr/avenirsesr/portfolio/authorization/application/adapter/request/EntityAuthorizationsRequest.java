package fr.avenirsesr.portfolio.authorization.application.adapter.request;

import fr.avenirsesr.portfolio.authorization.domain.port.input.EntityAuthorizationService;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

public record EntityAuthorizationsRequest(
    @NotEmpty @Size(max = EntityAuthorizationService.MAX_ENTITY_IDS)
        List<@NotNull UUID> entityIds) {}
