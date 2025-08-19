package fr.avenirsesr.portfolio.backoffice.configuration.shared.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.backoffice.configuration.shared.domain.model.EConfiguration;
import fr.avenirsesr.portfolio.backoffice.configuration.shared.domain.model.EConfigurationScope;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.model.AvenirsBaseEntity;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "configuration")
@NoArgsConstructor
@Getter
@Setter
public class ConfigurationEntity extends AvenirsBaseEntity {
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private EConfigurationScope scope;

  @Column(name = "\"key\"", nullable = false, unique = true)
  private String key;

  @Column(name = "\"value\"", nullable = false)
  private String value;

  private ConfigurationEntity(
      UUID id, EConfigurationScope scope, EConfiguration key, String value) {
    this.setId(id);
    this.scope = scope;
    this.key = key.name();
    this.value = value;
  }

  public static ConfigurationEntity of(
      UUID id, EConfigurationScope scope, EConfiguration key, String value) {
    return new ConfigurationEntity(id, scope, key, value);
  }
}
