package fr.avenirsesr.portfolio.api.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.api.domain.model.enums.EExternalSource;
import fr.avenirsesr.portfolio.api.domain.model.enums.EUserCategory;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(
        name = "external_users",
        uniqueConstraints = @UniqueConstraint(columnNames = {"eternal_id", "source"})
)
@AllArgsConstructor
@NoArgsConstructor
public class ExternalUserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String externalId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EExternalSource source;

    @ManyToOne(optional = false)
    private UserEntity user;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EUserCategory category;

    @Column(nullable = false)
    @Email
    private String email;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;
}
