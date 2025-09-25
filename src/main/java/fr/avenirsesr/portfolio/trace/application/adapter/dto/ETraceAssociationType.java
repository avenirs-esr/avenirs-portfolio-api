package fr.avenirsesr.portfolio.trace.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(ref = "#/components/schemas/ETraceAssociationType")
public enum ETraceAssociationType {
  AMS,
  SKILL_LEVEL,
  ADDITIONAL_SKILL
}
