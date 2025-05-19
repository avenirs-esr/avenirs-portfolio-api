package fr.avenirsesr.portfolio.api.infrastructure.adapter.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "program_progress")
@AllArgsConstructor
@NoArgsConstructor
public class ProgramProgressEntity {
  @Id private UUID id;

  @ManyToOne(optional = false)
  private ProgramEntity program;

  @ManyToOne(optional = false)
  private UserEntity student;

  @OneToMany private Set<SkillEntity> skills;
}
