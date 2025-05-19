package fr.avenirsesr.portfolio.api.infrastructure.adapter.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "trace")
@AllArgsConstructor
@NoArgsConstructor
public class TraceEntity {
  @Id private UUID id;

  @ManyToOne(optional = false)
  private UserEntity user;

  @ManyToMany private List<SkillLevelEntity> skillLevels;

  @ManyToMany private List<AMSEntity> amses;
}
