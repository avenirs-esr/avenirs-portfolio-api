package fr.avenirsesr.portfolio.user.domain.model;

import fr.avenirsesr.portfolio.user.domain.model.enums.EExternalSource;
import fr.avenirsesr.portfolio.user.domain.model.enums.EUserCategory;
import java.util.Objects;
import lombok.Getter;

@Getter
public class ExternalUser {
  private final String externalId;
  private final EExternalSource source;
  private final User user;
  private final EUserCategory category;
  private final String email;
  private final String firstName;
  private final String lastName;

  private ExternalUser(
      User user,
      String externalId,
      EExternalSource source,
      EUserCategory category,
      String email,
      String firstName,
      String lastName) {
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
    return new ExternalUser(user, externalId, source, category, email, firstName, lastName);
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
