package fr.avenirsesr.portfolio.file.domain.port.input;

import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.file.domain.model.ActivityBanner;
import java.io.IOException;
import java.util.UUID;

public interface ActivityResourceService {
  ActivityBanner uploadBannerFor(
      Activity activity, String fileName, String mimeType, long size, byte[] content)
      throws IOException;

  ActivityBanner getActivityFile(UUID fileID);

  ActivityBanner getActivityBanner(Activity activity);

  byte[] fetchContent(ActivityBanner activityFile) throws IOException;
}
