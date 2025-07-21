package fr.avenirsesr.portfolio.program.domain.model;

import fr.avenirsesr.portfolio.shared.domain.model.AvenirsBaseModel;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Skill extends AvenirsBaseModel {
  private final String name;

  private Skill(UUID id, String name) {
    super(id);
    this.name = name;
  }

  public static Skill create(UUID id, String name) {
    return new Skill(id, name);
  }

  public static Skill toDomain(UUID id, String name) {
    return new Skill(id, name);
  }
}
