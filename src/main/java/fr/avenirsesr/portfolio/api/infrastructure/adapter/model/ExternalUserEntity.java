package fr.avenirsesr.portfolio.api.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.api.domain.model.ExternalUser;
import fr.avenirsesr.portfolio.api.domain.model.enums.EExternalSource;
import fr.avenirsesr.portfolio.api.domain.model.enums.EUserCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "external_users",
    uniqueConstraints = @UniqueConstraint(columnNames = {"eternal_id", "source"}))
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter
public class ExternalUserEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Setter(AccessLevel.NONE)
  private UUID id;

  @Column(nullable = false)
  private String externalId;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private EExternalSource source;

  @ManyToOne(optional = false)
  private UserEntity user;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private EUserCategory category;

  @Column(nullable = false)
  @Email
  private String email;

  @Column(nullable = false)
  private String firstName;

  @Column(nullable = false)
  private String lastName;

  public ExternalUserEntity(
      String externalId,
      EExternalSource source,
      UserEntity user,
      EUserCategory category,
      String email,
      String firstName,
      String lastName) {
    this.externalId = externalId;
    this.source = source;
    this.user = user;
    this.category = category;
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;
  }

  public static ExternalUserEntity fromDomain(ExternalUser externalUser) {
    return new ExternalUserEntity(
        externalUser.getExternalId(),
        externalUser.getSource(),
        UserEntity.fromDomain(externalUser.getUser()),
        externalUser.getCategory(),
        externalUser.getEmail(),
        externalUser.getFirstName(),
        externalUser.getLastName());
  }

  public static ExternalUser toDomain(ExternalUserEntity externalUserEntity) {
    return ExternalUser.create(
            UserEntity.toDomain(externalUserEntity.getUser()),
            externalUserEntity.getExternalId(),
            externalUserEntity.getSource(),
            externalUserEntity.getCategory(),
            externalUserEntity.getEmail(),
            externalUserEntity.getFirstName(),
            externalUserEntity.getLastName()
    );
  }
}
