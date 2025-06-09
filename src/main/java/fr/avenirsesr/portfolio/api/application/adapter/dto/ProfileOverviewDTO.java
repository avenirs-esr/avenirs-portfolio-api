package fr.avenirsesr.portfolio.api.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(requiredProperties = {"firstname", "lastname", "bio", "profilePicture", "coverPicture"})
public record ProfileOverviewDTO(
    String firstname, String lastname, String bio, String profilePicture, String coverPicture) {}
