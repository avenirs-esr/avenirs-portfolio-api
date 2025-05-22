package fr.avenirsesr.portfolio.api.domain.model;

import fr.avenirsesr.portfolio.api.domain.model.enums.ELearningMethod;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Program {
  private final UUID id;
  private final Institution institution;
  private final String name;
  private ELearningMethod learningMethod;

  private Program(UUID id, Institution institution, String name, ELearningMethod learningMethod) {
    this.id = id;
    this.institution = institution;
    this.name = name;
    this.learningMethod = learningMethod;
  }

  public static Program create(
      Institution institution, String name, ELearningMethod learningMethod) {
    return new Program(UUID.randomUUID(), institution, name, learningMethod);
  }

  public static Program toDomain(
      UUID id, Institution institution, String name, ELearningMethod learningMethod) {
    return new Program(id, institution, name, learningMethod);
  }
}
