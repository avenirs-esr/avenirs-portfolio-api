package fr.avenirsesr.portfolio.api.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.api.domain.model.enums.ESkillLevelStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "skill_level")
@AllArgsConstructor
@NoArgsConstructor
public class SkillLevelEntity {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(optional = false)
    private SkillEntity skill;

    @Column
    @Enumerated(EnumType.STRING)
    private ESkillLevelStatus status;

    @ManyToMany
    private List<TraceEntity> traces;

    @ManyToMany
    private List<AMSEntity> amses;
}
