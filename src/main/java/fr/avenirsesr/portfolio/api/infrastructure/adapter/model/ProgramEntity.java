package fr.avenirsesr.portfolio.api.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.api.domain.model.enums.EPortfolioType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "program")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProgramEntity {
  @Id private UUID id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private boolean isAPC;

  @ManyToOne(optional = false)
  private InstitutionEntity institution;
}
