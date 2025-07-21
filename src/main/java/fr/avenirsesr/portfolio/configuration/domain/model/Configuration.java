package fr.avenirsesr.portfolio.configuration.domain.model;

import fr.avenirsesr.portfolio.shared.domain.model.AvenirsBaseModel;
import java.util.UUID;
import lombok.Getter;

@Getter
@Setter
public class Configuration extends AvenirsBaseModel {
  private final String name;
  private final String value;

  private Configuration(UUID id, String name, String value) {
    super(id);
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
