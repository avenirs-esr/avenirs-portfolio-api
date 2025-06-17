package fr.avenirsesr.portfolio.api.infrastructure.adapter.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
public class ConfigurationEntity {
  @Id
  @Column(nullable = false)
  private UUID id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String value;
}
