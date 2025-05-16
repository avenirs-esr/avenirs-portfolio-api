package fr.avenirsesr.portfolio.api.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.api.domain.model.Institution;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.InstitutionRepository;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.InstitutionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InstitutionDatabaseRepository implements InstitutionRepository {
    private final InstitutionJpaRepository jpaRepository;

    @Override
    public void save(Institution institution) {
        var entity = toEntity(institution);
        jpaRepository.save(entity);
    }

    @Override
    public void saveAll(List<Institution> institutions) {
        var entities = institutions.stream().map(InstitutionDatabaseRepository::toEntity).toList();
        jpaRepository.saveAll(entities);
    }

    public static InstitutionEntity toEntity(Institution institution) {
        return new InstitutionEntity(
                institution.getId(),
                institution.getName(),
                institution.getEnabledFields()
        );
    }
}
