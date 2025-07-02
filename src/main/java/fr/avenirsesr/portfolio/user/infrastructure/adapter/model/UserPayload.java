package fr.avenirsesr.portfolio.user.infrastructure.adapter.model;

import java.time.Instant;
import java.util.UUID;
import lombok.Data;

@Data
public class UserPayload {
  private UUID sub;
  private Instant iat;
  private Instant exp;
}
