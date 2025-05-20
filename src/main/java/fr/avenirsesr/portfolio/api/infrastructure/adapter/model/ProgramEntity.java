package fr.avenirsesr.portfolio.api.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.api.domain.model.Program;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "program")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Getter
@Setter
public class ProgramEntity {
  @Id private UUID id;

  @Column(nullable = false)
  private String name;

  @ManyToOne(optional = false)
  private InstitutionEntity institution;

  public static ProgramEntity fromDomain(Program program) {
    return new ProgramEntity(
        program.getId(), program.getName(), InstitutionEntity.fromDomain(program.getInstitution()));
  }
}
