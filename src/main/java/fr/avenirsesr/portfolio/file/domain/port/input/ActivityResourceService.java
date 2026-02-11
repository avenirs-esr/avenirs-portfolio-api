package fr.avenirsesr.portfolio.file.domain.port.input;

import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.file.domain.model.ActivityBanner;
import java.io.IOException;

public interface ActivityResourceService {
  ActivityBanner uploadBannerFor(
      Activity activity, String fileName, String mimeType, long size, byte[] content)
      throws IOException;
}
