package fr.avenirsesr.portfolio.api.application.adapter.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SkillLevelDTO {
  private UUID id;
  private String name;
  private String status;
}
