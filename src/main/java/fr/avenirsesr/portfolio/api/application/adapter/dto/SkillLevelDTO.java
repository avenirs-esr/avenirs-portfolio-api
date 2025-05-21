package fr.avenirsesr.portfolio.api.application.adapter.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class SkillLevelDTO {
  private UUID id;
  private String name;
  private String status;
}
