package fr.avenirsesr.portfolio.user.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(requiredProperties = {"id", "acceptedAt"})
public record AcceptedCguDTO(UUID id, Instant acceptedAt) {}
