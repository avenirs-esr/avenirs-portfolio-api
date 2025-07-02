package fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.shared.infrastructure.adapter.model.AvenirsBaseEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "program_progress")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProgramProgressEntity extends AvenirsBaseEntity {
  @ManyToOne(optional = false)
  private ProgramEntity program;

  @ManyToOne(optional = false)
  private UserEntity student;

  @OneToMany(mappedBy = "programProgress", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<SkillEntity> skills;

  public ProgramProgressEntity(
      UUID id, ProgramEntity program, UserEntity student, Set<SkillEntity> skills) {
    this.setId(id);
    this.program = program;
    this.student = student;
    this.skills = skills;
  }
}
