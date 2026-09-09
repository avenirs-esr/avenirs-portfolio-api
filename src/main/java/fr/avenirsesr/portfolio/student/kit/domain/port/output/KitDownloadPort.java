package fr.avenirsesr.portfolio.student.kit.domain.port.output;

import fr.avenirsesr.portfolio.student.kit.domain.model.KitMediaEntry;
import java.util.List;

public interface KitDownloadPort {
  List<KitMediaEntry> getMedia();
}
