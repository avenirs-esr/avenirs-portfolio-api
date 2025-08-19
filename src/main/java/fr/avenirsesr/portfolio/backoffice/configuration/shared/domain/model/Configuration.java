package fr.avenirsesr.portfolio.backoffice.configuration.shared.domain.model;

import fr.avenirsesr.portfolio.shared.domain.model.AvenirsBaseModel;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Configuration extends AvenirsBaseModel {
  private final EConfigurationScope scope;
  private final EConfiguration key;
  private String value;

  private Configuration(UUID id, EConfigurationScope scope, EConfiguration key, String value) {
    super(id);
    this.scope = scope;
    this.key = key;
    this.value = value;
  }

  public static Configuration create(
      UUID id, EConfigurationScope scope, EConfiguration key, String value) {
    return new Configuration(id, scope, key, value);
  }

  public static Configuration toDomain(
      UUID id, EConfigurationScope scope, EConfiguration key, String value) {
    return new Configuration(id, scope, key, value);
  }
}
