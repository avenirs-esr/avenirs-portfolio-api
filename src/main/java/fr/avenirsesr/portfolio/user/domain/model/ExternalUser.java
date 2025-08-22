package fr.avenirsesr.portfolio.user.domain.model;

import fr.avenirsesr.portfolio.shared.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.user.domain.model.enums.EExternalSource;
import fr.avenirsesr.portfolio.user.domain.model.enums.EUserCategory;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;

@Getter
public class ExternalUser extends AvenirsBaseModel {
  private final String externalId;
  private final EExternalSource source;
  private final User user;
  private final EUserCategory category;
  private final String email;
  private final String firstName;
  private final String lastName;

  private ExternalUser(
      UUID id,
      Instant createdAt,
      Instant updatedAt,
      User user,
      String externalId,
      EExternalSource source,
      EUserCategory category,
      String email,
      String firstName,
      String lastName) {
    super(id, createdAt, updatedAt);
    this.user = user;
    this.externalId = externalId;
    this.source = source;
    this.category = category;
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;
  }

  public static ExternalUser create(
      User user,
      String externalId,
      EExternalSource source,
      EUserCategory category,
      String email,
      String firstName,
      String lastName) {
    return new ExternalUser(
        UUID.randomUUID(),
        Instant.now(),
        Instant.now(),
        user,
        externalId,
        source,
        category,
        email,
        firstName,
        lastName);
  }

  public static ExternalUser toDomain(
      UUID id,
      Instant createdAt,
      Instant updatedAt,
      User user,
      String externalId,
      EExternalSource source,
      EUserCategory category,
      String email,
      String firstName,
      String lastName) {
    return new ExternalUser(
        id, createdAt, updatedAt, user, externalId, source, category, email, firstName, lastName);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ExternalUser that = (ExternalUser) o;
    return Objects.equals(externalId, that.externalId) && Objects.equals(source, that.source);
  }

  @Override
  public int hashCode() {
    return Objects.hash(externalId, source);
  }

  @Override
  public String toString() {
    return "ExternalUser[User - " + user.getId() + ']';
  }
}
