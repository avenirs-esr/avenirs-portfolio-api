package fr.avenirsesr.portfolio.user.application.adapter.dto;

import fr.avenirsesr.portfolio.common.group.application.adapter.dto.GroupDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

/** Student identity enriched with the program held by the back-office. */
@Schema(requiredProperties = {"id", "firstName", "lastName", "email"})
public record StudentInfoDTO(
    UUID id, String firstName, String lastName, String email, GroupDTO program) {}
