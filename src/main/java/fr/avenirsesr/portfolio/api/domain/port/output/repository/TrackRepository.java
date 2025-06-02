package fr.avenirsesr.portfolio.api.domain.port.output.repository;

import fr.avenirsesr.portfolio.api.domain.model.Track;
import fr.avenirsesr.portfolio.api.domain.model.User;
import java.util.List;

public interface TrackRepository extends GenericRepositoryPort<Track> {
  List<Track> findLastsOf(User user, int limit);
}
