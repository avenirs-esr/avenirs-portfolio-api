package fr.avenirsesr.portfolio.user.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(
    requiredProperties = {
      "firstname",
      "lastname",
      "bio",
      "email",
      "profilePicture",
      "coverPicture"
    })
public record ProfileOverviewDTO(
    String firstname,
    String lastname,
    String bio,
    String email,
    PictureDTO profilePicture,
    PictureDTO coverPicture) {

  @Schema(requiredProperties = {"url"})
  public record PictureDTO(UUID fileId, String fileName, String url) {}
}
