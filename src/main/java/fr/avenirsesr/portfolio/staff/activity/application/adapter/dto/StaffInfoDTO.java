package fr.avenirsesr.portfolio.staff.activity.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(requiredProperties = {"id", "firstName", "lastName"})
public record StaffInfoDTO(UUID id, String firstName, String lastName) {}
