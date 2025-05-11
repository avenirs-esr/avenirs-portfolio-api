package fr.avenirsesr.portfolio.api.infrastructure.adapter.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "skill")
@AllArgsConstructor
@NoArgsConstructor
public class SkillEntity {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(optional = false)
    private ProgramProgressEntity programProgress;

    @OneToMany
    private Set<SkillLevelEntity> skillLevels;
}
