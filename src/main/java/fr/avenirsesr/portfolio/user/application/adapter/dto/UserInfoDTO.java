package fr.avenirsesr.portfolio.user.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(requiredProperties = {"id", "firstName", "lastName", "email"})
public record UserInfoDTO(UUID id, String firstName, String lastName, String email) {}
