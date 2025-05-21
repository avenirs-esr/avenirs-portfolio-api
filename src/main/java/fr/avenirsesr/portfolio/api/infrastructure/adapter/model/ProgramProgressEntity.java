package fr.avenirsesr.portfolio.api.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.api.domain.model.ProgramProgress;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "program_progress")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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

  public static ProgramProgressEntity fromDomain(ProgramProgress programProgress) {
    return new ProgramProgressEntity(
        programProgress.getId(),
        ProgramEntity.fromDomain(programProgress.getProgram()),
        UserEntity.fromDomain(programProgress.getStudent().getUser()),
        programProgress.getSkills().stream()
            .map(SkillEntity::fromDomain)
            .collect(Collectors.toSet()));
  }

  public static ProgramProgress toDomain(ProgramProgressEntity programProgressEntity) {
    return ProgramProgress.toDomain(
            programProgressEntity.getId(),
            ProgramEntity.toDomain(programProgressEntity.getProgram()),
            StudentEntity.toDomain(programProgressEntity.getStudent().getStudent(), programProgressEntity.getStudent()),
            programProgressEntity.getSkills().stream()
                    .map(SkillEntity::toDomain)
                    .collect(Collectors.toSet()));
  }
}
