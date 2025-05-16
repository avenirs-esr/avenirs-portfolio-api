package fr.avenirsesr.portfolio.api.infrastructure.adapter.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "program_progress")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProgramProgressEntity {
  @Id private UUID id;

  @ManyToOne(optional = false)
  private ProgramEntity program;

  @ManyToOne(optional = false)
  private UserEntity student;

  @OneToMany
  @JoinTable(
      name = "program_progress_skills",
      joinColumns = @JoinColumn(name = "program_progress_id"),
      inverseJoinColumns = @JoinColumn(name = "skill_id"))
  private Set<SkillEntity> skills;
}
