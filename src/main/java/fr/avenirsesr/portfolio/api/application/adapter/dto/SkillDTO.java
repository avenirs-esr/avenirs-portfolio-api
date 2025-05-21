package fr.avenirsesr.portfolio.api.application.adapter.dto;

import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SkillDTO {
  private UUID id;
  private String name;
  private int trackCount;
  private int activityCount;
  private List<SkillLevelDTO> levels;
}
