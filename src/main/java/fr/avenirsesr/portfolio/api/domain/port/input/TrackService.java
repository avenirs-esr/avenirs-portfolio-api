package fr.avenirsesr.portfolio.api.domain.port.input;

import fr.avenirsesr.portfolio.api.domain.model.Track;
import fr.avenirsesr.portfolio.api.domain.model.User;
import java.util.List;

public interface TrackService {
  String programNameOfTrack(Track track);

  List<Track> lastTracksOf(User user);
}
