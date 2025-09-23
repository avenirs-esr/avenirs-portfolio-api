package fr.avenirsesr.portfolio.ams.domain.model;

import fr.avenirsesr.portfolio.ams.domain.model.enums.EAmsStatus;
import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.user.domain.model.User;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AMS extends AvenirsBaseModel {
  private final User user;
  private final String title;
  private final Instant startDate;
  private final Instant endDate;
  private EAmsStatus status;

  private AMS(
      UUID id,
      User user,
      String title,
      Instant startDate,
      Instant endDate,
      Instant createdAt,
      Instant updatedAt) {
    super(id, createdAt, updatedAt);
    this.user = user;
    this.title = title;
    this.startDate = startDate;
    this.endDate = endDate;
  }

  public static AMS create(UUID id, User user, String title, Instant startDate, Instant endDate) {
    var ams = new AMS(id, user, title, startDate, endDate, Instant.now(), Instant.now());
    ams.setStatus(EAmsStatus.NOT_STARTED);
    return ams;
  }

  public static AMS toDomain(
      UUID id,
      User user,
      String title,
      Instant startDate,
      Instant endDate,
      EAmsStatus status,
      Instant createdAt,
      Instant updatedAt) {
    var ams = new AMS(id, user, title, startDate, endDate, createdAt, updatedAt);
    ams.setStatus(status);
    return ams;
  }
}
