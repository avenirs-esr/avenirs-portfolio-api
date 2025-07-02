package fr.avenirsesr.portfolio.configuration.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.shared.infrastructure.adapter.model.AvenirsBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "configuration")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ConfigurationEntity extends AvenirsBaseEntity {
  @Column(nullable = false)
  private String name;

  @Column(name = "\"value\"", nullable = false)
  private String value;

  public ConfigurationEntity(UUID id, String name, String value) {
    this.setId(id);
    this.name = name;
    this.value = value;
  }
}
