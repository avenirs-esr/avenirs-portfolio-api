package fr.avenirsesr.portfolio.user.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;

@Schema(requiredProperties = {"firstname", "lastname", "roles"})
public record LoggedInUserDTO(String firstname, String lastname, Set<String> roles) {}
