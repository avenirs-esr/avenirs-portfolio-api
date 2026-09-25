package fr.avenirsesr.portfolio.user.domain.port.output.client;

import java.util.List;
import java.util.UUID;

public record StudentAccessScope(List<UUID> institutionIds, List<UUID> groupIds) {

  public static StudentAccessScope empty() {
    return new StudentAccessScope(List.of(), List.of());
  }
}
