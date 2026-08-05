package fr.avenirsesr.portfolio.staff.activity.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(requiredProperties = {"userId", "firstName", "lastName"})
public record AuthorDTO(UUID userId, String firstName, String lastName) {}
