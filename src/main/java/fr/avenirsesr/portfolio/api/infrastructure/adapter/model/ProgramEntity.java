package fr.avenirsesr.portfolio.api.infrastructure.adapter.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "program")
@AllArgsConstructor
@NoArgsConstructor
public class ProgramEntity {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(optional = false)
    private InstitutionEntity institution;
}
