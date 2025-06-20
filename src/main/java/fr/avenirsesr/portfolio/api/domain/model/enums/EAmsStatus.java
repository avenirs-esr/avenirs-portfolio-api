package fr.avenirsesr.portfolio.api.domain.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AmsStatus", description = "Status of the AMS", enumAsRef = true)
public enum EAmsStatus {
  NOT_STARTED,
  IN_PROGRESS,
  SUBMITTED,
  COMPLETED
}
