package fr.avenirsesr.portfolio.api.infrastructure.adapter.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ams")
@AllArgsConstructor
@NoArgsConstructor
public class AMSEntity {
    @Id
    private UUID id;

    @ManyToOne(optional = false)
    private UserEntity user;

    @ManyToMany
    private List<SkillLevelEntity> skillLevels;
}
