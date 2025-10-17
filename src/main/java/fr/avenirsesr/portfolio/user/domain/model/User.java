package fr.avenirsesr.portfolio.user.domain.model;

import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User extends AvenirsBaseModel {
  private String firstName;
  private String lastName;
  private String email;

  private User(
      UUID id,
      String firstName,
      String lastName,
      String email,
      Instant createdAt,
      Instant updatedAt) {
    super(id, createdAt, updatedAt);
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
  }

  public static User create(String firstName, String lastName, String email) {
    return new User(UUID.randomUUID(), firstName, lastName, email, Instant.now(), Instant.now());
  }

  public static User toDomain(
      UUID id,
      String firstName,
      String lastName,
      String email,
      Instant createdAt,
      Instant updatedAt) {
    return new User(id, firstName, lastName, email, createdAt, updatedAt);
  }
}
