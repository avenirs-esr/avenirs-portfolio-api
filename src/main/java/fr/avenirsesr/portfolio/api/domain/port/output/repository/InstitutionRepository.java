package fr.avenirsesr.portfolio.api.domain.port.output.repository;

import fr.avenirsesr.portfolio.api.domain.model.Institution;

import java.util.List;

public interface InstitutionRepository {
    void save(Institution institution);
    void saveAll(List<Institution> institutions);
}
