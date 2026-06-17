package fr.avenirsesr.portfolio.user.application.adapter.dto;

import fr.avenirsesr.portfolio.shared.application.adapter.dto.FileDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(
    requiredProperties = {
      "id",
      "firstname",
      "lastname",
      "bio",
      "email",
      "profilePicture",
      "coverPicture"
    })
public record ProfileOverviewDTO(
    UUID id,
    String firstname,
    String lastname,
    String bio,
    String email,
    FileDTO profilePicture,
    FileDTO coverPicture) {}
