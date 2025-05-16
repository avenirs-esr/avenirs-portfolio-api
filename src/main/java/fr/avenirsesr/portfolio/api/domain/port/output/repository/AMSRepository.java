package fr.avenirsesr.portfolio.api.domain.port.output.repository;

import fr.avenirsesr.portfolio.api.domain.model.AMS;

import java.util.List;

public interface AMSRepository {
    void save(AMS ams);
    void saveAll(List<AMS> amses);
}
