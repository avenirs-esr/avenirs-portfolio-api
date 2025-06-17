package fr.avenirsesr.portfolio.api.domain.model;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Configuration {
  private final UUID id;
  private final String name;
  private final String value;

  private Configuration(UUID id, String name, String value) {
    this.id = id;
    this.name = name;
    this.value = value;
  }

  public static Configuration create(UUID id, String name, String value) {
    return new Configuration(id, name, value);
  }

  public static Configuration toDomain(UUID id, String name, String value) {
    return new Configuration(id, name, value);
  }
}
