package fr.avenirsesr.portfolio.api.domain.port.output.repository;

import fr.avenirsesr.portfolio.api.domain.model.Trace;

import java.util.List;

public interface TraceRepository {
    void save(Trace trace);
    void saveAll(List<Trace> traces);
}
