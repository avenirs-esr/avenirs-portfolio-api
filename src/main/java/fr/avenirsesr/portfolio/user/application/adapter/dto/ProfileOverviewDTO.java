package fr.avenirsesr.portfolio.user.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

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
    String profilePicture,
    String coverPicture) {}
