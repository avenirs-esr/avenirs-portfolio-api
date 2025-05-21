package fr.avenirsesr.portfolio.api.application.adapter.dto;

import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProgramProgressDTO {
  private UUID id;
  private String name;
  private List<SkillDTO> skills;
}
