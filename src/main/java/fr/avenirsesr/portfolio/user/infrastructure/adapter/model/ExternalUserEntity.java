package fr.avenirsesr.portfolio.user.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.AvenirsBaseEntity;
import fr.avenirsesr.portfolio.user.domain.model.enums.EExternalSource;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "external_user",
    uniqueConstraints = @UniqueConstraint(columnNames = {"external_id", "source"}),
    indexes = {
      @Index(name = "idx_ext_user_user", columnList = "user_id"),
      @Index(name = "idx_ext_user_email", columnList = "email")
    })
@NoArgsConstructor
@Getter
@Setter
public class ExternalUserEntity extends AvenirsBaseEntity {
  @Column(nullable = false, name = "external_id")
  private String externalId;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private EExternalSource source;

  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private EUserCategory category;

  @Column(nullable = false)
  @Email
  private String email;

  @Column(nullable = false, name = "first_name")
  private String firstName;

  @Column(nullable = false, name = "last_name")
  private String lastName;

  private ExternalUserEntity(
      UUID id,
      String externalId,
      EExternalSource source,
      UserEntity user,
      EUserCategory category,
      String email,
      String firstName,
      String lastName) {
    this.setId(id);
    this.externalId = externalId;
    this.source = source;
    this.user = user;
    this.category = category;
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;
  }

  public static ExternalUserEntity of(
      UUID id,
      String externalId,
      EExternalSource source,
      UserEntity user,
      EUserCategory category,
      String email,
      String firstName,
      String lastName) {
    return new ExternalUserEntity(
        id, externalId, source, user, category, email, firstName, lastName);
  }
}
