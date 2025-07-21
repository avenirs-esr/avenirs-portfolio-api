package fr.avenirsesr.portfolio.additionalskill.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(requiredProperties = {"id", "title", "pathSegments", "type"})
public record AdditionalSkillDTO(String id, String title, List<String> pathSegments, String type) {}
