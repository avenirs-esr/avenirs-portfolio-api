package fr.avenirsesr.portfolio.activity.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(requiredProperties = {"title", "items"})
public record ActivityNavigationDTO(String title, List<ActivityItemNavigationDTO> items) {}
