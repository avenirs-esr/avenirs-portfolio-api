package fr.avenirsesr.portfolio.api.domain.port.output.repository;

import fr.avenirsesr.portfolio.api.domain.model.Track;
import java.util.List;

public interface TrackRepository {
  void save(Track track);

  void saveAll(List<Track> tracks);
}
